package cl.lowcost.ufs.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddCartItemRequest(
        @NotNull Integer figureId,
        @NotNull @Min(1) Integer units) {
}
