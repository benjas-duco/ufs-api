package cl.lowcost.ufs.DTOs;

import cl.lowcost.ufs.Models.Purchase;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PurchaseResponse(
        Integer id,
        UUID userId,
        String userNickname,
        BigDecimal total,
        Instant date,
        List<PurchaseItemResponse> items,
        ShippingResponse shipping,
        boolean completed) {

    public static PurchaseResponse from(Purchase p) {
        return new PurchaseResponse(
                p.getId(),
                p.getUser().getId(),
                p.getUser().getNickname(),
                p.getTotal(),
                p.getDate(),
                p.getItems().stream().map(PurchaseItemResponse::from).toList(),
                p.getShipping() == null ? null : ShippingResponse.from(p.getShipping()),
                p.isCompleted());
    }
}
