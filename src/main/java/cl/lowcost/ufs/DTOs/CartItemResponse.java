package cl.lowcost.ufs.DTOs;

import java.math.BigDecimal;

public record CartItemResponse(FigureResponse figure, Integer units, BigDecimal subtotal) {
}
