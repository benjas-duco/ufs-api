package cl.lowcost.ufs.Controllers;

import cl.lowcost.ufs.DTOs.PurchaseResponse;
import cl.lowcost.ufs.DTOs.TrackingRequest;
import cl.lowcost.ufs.Services.PurchaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// Ver y despachar pedidos: SHIPPER o ADMIN. Quitar un pedido de la cola: solo ADMIN
// (las reglas están en SecurityConfig).
@RestController
@RequestMapping("/api/admin/purchases")
@RequiredArgsConstructor
public class AdminPurchaseController {

    private final PurchaseService purchaseService;

    // ?pending=true -> con ítems por enviar | ?pending=false -> completas | sin parámetro -> todas
    @GetMapping
    public List<PurchaseResponse> list(@RequestParam(required = false) Boolean pending) {
        return purchaseService.listAll(pending);
    }

    @PatchMapping("/{purchaseId}/items/{figureId}/sent")
    public PurchaseResponse markSent(@PathVariable Integer purchaseId, @PathVariable Integer figureId) {
        return purchaseService.markItemSent(purchaseId, figureId);
    }

    @PatchMapping("/{purchaseId}/tracking")
    public PurchaseResponse tracking(@PathVariable Integer purchaseId, @Valid @RequestBody TrackingRequest req) {
        return purchaseService.updateTracking(purchaseId, req.trackingCode());
    }

    @DeleteMapping("/{purchaseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Integer purchaseId) {
        purchaseService.cancel(purchaseId);
    }
}
