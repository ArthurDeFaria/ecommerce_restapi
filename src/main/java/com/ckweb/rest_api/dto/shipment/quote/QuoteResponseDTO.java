package com.ckweb.rest_api.dto.shipment.quote;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record QuoteResponseDTO(
    int id,
    String name,
    Double price,
    String discount,
    String currency,
    @JsonProperty("delivery_time") Integer deliveryTime,
    @JsonProperty("delivery_range") DeliveryRange deliveryRange,
    List<PackageDetail> packages,
    @JsonProperty("additional_services") AdditionalServices additionalServices,
    Company company,
    @JsonProperty("has_error") Boolean hasError,
    String error
) {

    public record Company(
        int id,
        String name,
        String picture
    ) {}

    public record DeliveryRange(
        int min,
        int max
    ) {}

    public record PackageDetail(
        double price,
        String discount,
        String format,
        Dimensions dimensions,
        String weight,
        @JsonProperty("insurance_value") double insuranceValue
    ) {}

    public record Dimensions(
        String height,
        String width,
        String length
    ) {}

    public record AdditionalServices(
        boolean receipt,
        @JsonProperty("own_hand") boolean ownHand
    ) {}
}
