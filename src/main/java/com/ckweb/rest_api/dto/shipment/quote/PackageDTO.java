package com.ckweb.rest_api.dto.shipment.quote;

public record PackageDTO(
    double height,
    double width,
    double length,
    double weight
) {}