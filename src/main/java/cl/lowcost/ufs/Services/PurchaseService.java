package cl.lowcost.ufs.Services;

import cl.lowcost.ufs.DTOs.PurchaseResponse;
import cl.lowcost.ufs.DTOs.ShippingRequest;
import cl.lowcost.ufs.Models.CartItem;
import cl.lowcost.ufs.Models.Figure;
import cl.lowcost.ufs.Models.Purchase;
import cl.lowcost.ufs.Models.PurchaseItem;
import cl.lowcost.ufs.Models.ShippingInfo;
import cl.lowcost.ufs.Models.States;
import cl.lowcost.ufs.Models.User;
import cl.lowcost.ufs.Repos.CartItemRepository;
import cl.lowcost.ufs.Repos.FigureRepository;
import cl.lowcost.ufs.Repos.PurchaseRepository;
import cl.lowcost.ufs.Repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchases;
    private final CartItemRepository cart;
    private final FigureRepository figures;
    private final UserRepository users;

    // Compra todo el carrito del usuario. Si algo falla, no se guarda nada.
    @Transactional
    public PurchaseResponse checkout(User user, ShippingRequest req) {
        List<CartItem> items = cart.findByUserIdOrderById(user.getId());
        if (items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El carrito está vacío");
        }

        // Figuras bloqueadas hasta terminar la compra (evita vender stock que ya no existe)
        List<Integer> ids = items.stream().map(i -> i.getFigure().getId()).toList();
        Map<Integer, Figure> locked = figures.findAllByIdForUpdate(ids).stream()
                .collect(Collectors.toMap(Figure::getId, Function.identity()));

        // 1) Validar todo antes de tocar nada
        List<String> problems = new ArrayList<>();
        for (CartItem item : items) {
            Figure f = locked.get(item.getFigure().getId());
            if (!f.getState().isPurchasable()) {
                problems.add("'" + f.getName() + "' ya no está disponible para la compra");
            } else if (f.getState().needsStock() && f.getUnits() < item.getUnits()) {
                problems.add("Solo quedan " + f.getUnits() + " unidades de '" + f.getName() + "'");
            }
        }
        if (!problems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.join("; ", problems));
        }

        // 2) Armar la compra, descontar stock y calcular el total con los precios de la base de datos
        Purchase purchase = new Purchase();
        purchase.setUser(users.getReferenceById(user.getId()));
        purchase.setDate(Instant.now());
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : items) {
            Figure f = locked.get(item.getFigure().getId());
            int units = item.getUnits();

            PurchaseItem line = new PurchaseItem();
            line.setFigure(f);
            line.setUnits(units);
            line.setUnitPrice(f.getPrice());
            line.setSent(false);
            purchase.getItems().add(line);

            total = total.add(f.getPrice().multiply(BigDecimal.valueOf(units)));

            if (f.getState().needsStock()) {
                f.setUnits(f.getUnits() - units);
                if (f.getUnits() == 0) {
                    f.setState(States.UNAVAILABLE);
                }
            }
        }
        purchase.setTotal(total);
        purchase.attachShipping(toShipping(req));

        purchases.save(purchase);
        cart.deleteByUserId(user.getId());
        return PurchaseResponse.from(purchase);
    }

    @Transactional(readOnly = true)
    public List<PurchaseResponse> listMine(User user) {
        return purchases.findByUserIdOrderByDateDesc(user.getId()).stream()
                .map(PurchaseResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PurchaseResponse getMine(User user, Integer id) {
        return purchases.findByIdAndUserId(id, user.getId())
                .map(PurchaseResponse::from)
                .orElseThrow(() -> notFound("Compra no encontrada"));
    }

    // ---------- Admin ----------

    // pending = true: compras con ítems por enviar; false: compras completas; null: todas
    @Transactional(readOnly = true)
    public List<PurchaseResponse> listAll(Boolean pending) {
        return purchases.findAllByOrderByDateDesc().stream()
                .filter(p -> pending == null || pending == !p.isCompleted())
                .map(PurchaseResponse::from)
                .toList();
    }

    // Marca un ítem como enviado. Las figuras en PRE_SALE todavía no se pueden enviar.
    @Transactional
    public PurchaseResponse markItemSent(Integer purchaseId, Integer figureId) {
        Purchase purchase = purchases.findById(purchaseId)
                .orElseThrow(() -> notFound("Compra no encontrada"));
        PurchaseItem item = purchase.getItems().stream()
                .filter(i -> i.getFigure().getId().equals(figureId))
                .findFirst()
                .orElseThrow(() -> notFound("Esa figura no está en la compra"));

        if (!item.isSent()) {
            if (item.getFigure().getState() == States.PRE_SALE) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "'" + item.getFigure().getName() + "' sigue en preventa, aún no se puede enviar");
            }
            item.setSent(true);
        }
        return PurchaseResponse.from(purchase);
    }

    @Transactional
    public PurchaseResponse updateTracking(Integer purchaseId, String trackingCode) {
        Purchase purchase = purchases.findById(purchaseId)
                .orElseThrow(() -> notFound("Compra no encontrada"));
        if (purchase.getShipping() == null) {
            throw notFound("La compra no tiene datos de envío");
        }
        purchase.getShipping().setTrackingCode(trackingCode.trim());
        return PurchaseResponse.from(purchase);
    }

    private ShippingInfo toShipping(ShippingRequest r) {
        ShippingInfo s = new ShippingInfo();
        s.setRecipientName(r.recipientName().trim());
        s.setPhone(r.phone().trim());
        s.setRegion(r.region().trim());
        s.setComuna(r.comuna().trim());
        s.setStreet(r.street().trim());
        s.setStreetNumber(r.streetNumber().trim());
        s.setApartment(r.apartment());
        s.setReference(r.reference());
        return s;
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }
}
