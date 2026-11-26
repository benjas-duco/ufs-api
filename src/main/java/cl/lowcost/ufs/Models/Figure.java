package cl.lowcost.ufs.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "figures")
@Getter
@Setter
@NoArgsConstructor
public class Figure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Brands brand;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private States state;

    @ElementCollection
    @CollectionTable(name = "figure_tags", joinColumns = @JoinColumn(name = "figure_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    // Stock disponible
    @Column(nullable = false)
    private Integer units;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(columnDefinition = "text")
    private String description;
}
