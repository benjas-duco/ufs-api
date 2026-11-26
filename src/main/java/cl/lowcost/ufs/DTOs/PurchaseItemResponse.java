package cl.lowcost.ufs.DTOs;

import cl.lowcost.ufs.Models.PurchaseItem;

import java.math.BigDecimal;

public record PurchaseItemResponse(
        FigureDTO figure,
        Integer units,
        BigDecimal unitPrice,
        BigDecimal subtotal,
        boolean sent) {

    public static PurchaseItemResponse from(PurchaseItem i) {
        return new PurchaseItemResponse(
                FigureDTO.from(i.getFigure()),
                i.getUnits(),
                i.getUnitPrice(),
                i.getUnitPrice().multiply(BigDecimal.valueOf(i.getUnits())),
                i.isSent());
    }
}
