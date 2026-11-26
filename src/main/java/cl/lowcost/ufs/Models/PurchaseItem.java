package cl.lowcost.ufs.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// Struct que vive dentro de Purchase (no se maneja como entidad aparte).
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class PurchaseItem {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "figure_id")
    private Figure figure;

    @Column(nullable = false)
    private Integer units;

    // Precio de la figura al momento de comprar
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    // true cuando este ítem ya fue despachado
    @Column(nullable = false)
    private boolean sent;
}
