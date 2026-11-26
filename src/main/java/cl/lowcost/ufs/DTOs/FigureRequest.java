package cl.lowcost.ufs.DTOs;

import cl.lowcost.ufs.Models.Brands;
import cl.lowcost.ufs.Models.States;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record FigureRequest(
        @NotBlank @Size(max = 200) String name,
        @NotNull Brands brand,
        @NotNull @Positive BigDecimal price,
        @NotNull States state,
        List<String> tags,
        @NotNull @Min(0) Integer units,
        @Size(max = 500) String imageUrl,
        String description) {
}
