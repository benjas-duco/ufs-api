package cl.lowcost.ufs.DTOs;

import cl.lowcost.ufs.Models.Brands;
import cl.lowcost.ufs.Models.Figure;
import cl.lowcost.ufs.Models.States;
import cl.lowcost.ufs.Models.Tag;

import java.math.BigDecimal;
import java.util.List;

public record FigureResponse(
        Integer id,
        String name,
        Brands brand,
        String brandLabel,
        BigDecimal price,
        States state,
        List<String> tags,
        Integer units,
        String imageUrl,
        String description) {

    public static FigureResponse from(Figure f) {
        return new FigureResponse(f.getId(), f.getName(), f.getBrand(), f.getBrand().getLabel(),
                f.getPrice(), f.getState(), f.getTags().stream().map(Tag::getName).sorted().toList(), f.getUnits(),
                f.getImageUrl(), f.getDescription());
    }
}
