package cl.lowcost.ufs.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchases")
@Getter
@Setter
@NoArgsConstructor
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "purchase_date", nullable = false)
    private Instant date;

    @ElementCollection
    @CollectionTable(name = "purchase_items", joinColumns = @JoinColumn(name = "purchase_id"))
    @OrderColumn(name = "position")
    private List<PurchaseItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "purchase", cascade = CascadeType.ALL)
    private ShippingInfo shipping;

    // La compra está completa cuando todos sus ítems ya se enviaron (se calcula, no se guarda)
    public boolean isCompleted() {
        return !items.isEmpty() && items.stream().allMatch(PurchaseItem::isSent);
    }

    public void attachShipping(ShippingInfo info) {
        this.shipping = info;
        info.setPurchase(this);
    }
}
