package cl.lowcost.ufs.DTOs;

import cl.lowcost.ufs.Models.Brands;
import cl.lowcost.ufs.Models.Figure;
import cl.lowcost.ufs.Models.States;

// Versión simplificada de Figure + State, para las listas de compras del admin
public record FigureDTO(
        Integer id,
        String name,
        Brands brand,
        String imageUrl,
        States state) {

    public static FigureDTO from(Figure f) {
        return new FigureDTO(f.getId(), f.getName(), f.getBrand(), f.getImageUrl(), f.getState());
    }
}
