package com.ckweb.rest_api.dto.shipment.quote;

public record OptionsDTO(
    boolean own_hand,
    boolean receipt,
    double insurance_value,
    boolean use_insurance_value
) {}