package cl.lowcost.ufs.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shipping_info")
@Getter
@Setter
@NoArgsConstructor
public class ShippingInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_id", nullable = false, unique = true)
    private Purchase purchase;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private String comuna;

    @Column(nullable = false)
    private String street;

    @Column(name = "street_number", nullable = false)
    private String streetNumber;

    // Depto, casa, oficina... (opcional)
    private String apartment;

    @Column(name = "reference_note")
    private String reference;

    // Lo completa el admin al despachar (opcional)
    @Column(name = "tracking_code")
    private String trackingCode;
}
