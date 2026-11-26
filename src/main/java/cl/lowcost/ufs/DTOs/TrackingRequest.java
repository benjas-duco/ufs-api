package cl.lowcost.ufs.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TrackingRequest(@NotBlank @Size(max = 100) String trackingCode) {
}
