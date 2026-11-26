package cl.lowcost.ufs.DTOs;

import cl.lowcost.ufs.Models.ShippingInfo;

public record ShippingResponse(
        String recipientName,
        String phone,
        String region,
        String comuna,
        String street,
        String streetNumber,
        String apartment,
        String reference,
        String trackingCode) {

    public static ShippingResponse from(ShippingInfo s) {
        return new ShippingResponse(s.getRecipientName(), s.getPhone(), s.getRegion(), s.getComuna(),
                s.getStreet(), s.getStreetNumber(), s.getApartment(), s.getReference(), s.getTrackingCode());
    }
}
