package cl.lowcost.ufs.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

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

    // Las etiquetas viven en la tabla tags; si no existen, se crean al guardar la figura
    @ManyToMany
    @JoinTable(name = "figure_tag_links",
            joinColumns = @JoinColumn(name = "figure_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new LinkedHashSet<>();

    // Stock disponible
    @Column(nullable = false)
    private Integer units;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(columnDefinition = "text")
    private String description;
}
