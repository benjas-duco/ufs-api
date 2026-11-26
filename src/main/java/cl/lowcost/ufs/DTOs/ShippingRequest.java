package cl.lowcost.ufs.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ShippingRequest(
        @NotBlank @Size(max = 150) String recipientName,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Size(max = 100) String region,
        @NotBlank @Size(max = 100) String comuna,
        @NotBlank @Size(max = 200) String street,
        @NotBlank @Size(max = 30) String streetNumber,
        @Size(max = 100) String apartment,
        @Size(max = 300) String reference) {
}
