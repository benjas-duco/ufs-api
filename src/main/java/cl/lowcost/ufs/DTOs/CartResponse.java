package cl.lowcost.ufs.DTOs;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(List<CartItemResponse> items, BigDecimal total) {
}
