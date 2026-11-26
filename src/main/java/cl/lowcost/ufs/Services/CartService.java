package cl.lowcost.ufs.Services;

import cl.lowcost.ufs.DTOs.CartItemResponse;
import cl.lowcost.ufs.DTOs.CartResponse;
import cl.lowcost.ufs.DTOs.FigureResponse;
import cl.lowcost.ufs.Models.CartItem;
import cl.lowcost.ufs.Models.Figure;
import cl.lowcost.ufs.Models.User;
import cl.lowcost.ufs.Repos.CartItemRepository;
import cl.lowcost.ufs.Repos.FigureRepository;
import cl.lowcost.ufs.Repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cart;
    private final FigureRepository figures;
    private final UserRepository users;

    @Transactional(readOnly = true)
    public CartResponse get(User user) {
        return build(user);
    }

    // Suma unidades si la figura ya estaba en el carrito
    @Transactional
    public CartResponse addItem(User user, Integer figureId, int units) {
        Figure figure = findFigure(figureId);
        CartItem item = cart.findByUserIdAndFigureId(user.getId(), figureId).orElseGet(() -> {
            CartItem created = new CartItem();
            created.setUser(users.getReferenceById(user.getId()));
            created.setFigure(figure);
            created.setUnits(0);
            return created;
        });
        int newUnits = item.getUnits() + units;
        validate(figure, newUnits);
        item.setUnits(newUnits);
        cart.save(item);
        return build(user);
    }

    // Fija la cantidad exacta de una figura que ya está en el carrito
    @Transactional
    public CartResponse setUnits(User user, Integer figureId, int units) {
        CartItem item = cart.findByUserIdAndFigureId(user.getId(), figureId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Esa figura no está en tu carrito"));
        validate(item.getFigure(), units);
        item.setUnits(units);
        return build(user);
    }

    @Transactional
    public CartResponse removeItem(User user, Integer figureId) {
        cart.findByUserIdAndFigureId(user.getId(), figureId).ifPresent(cart::delete);
        return build(user);
    }

    @Transactional
    public CartResponse clear(User user) {
        cart.deleteByUserId(user.getId());
        return new CartResponse(List.of(), BigDecimal.ZERO);
    }

    private Figure findFigure(Integer id) {
        return figures.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Figura no encontrada"));
    }

    private void validate(Figure f, int units) {
        if (!f.getState().isPurchasable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "'" + f.getName() + "' no está disponible para la compra");
        }
        if (f.getState().needsStock() && units > f.getUnits()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo quedan " + f.getUnits() + " unidades de '" + f.getName() + "'");
        }
    }

    private CartResponse build(User user) {
        List<CartItemResponse> items = cart.findByUserIdOrderById(user.getId()).stream()
                .map(c -> new CartItemResponse(
                        FigureResponse.from(c.getFigure()),
                        c.getUnits(),
                        c.getFigure().getPrice().multiply(BigDecimal.valueOf(c.getUnits()))))
                .toList();
        BigDecimal total = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(items, total);
    }
}
