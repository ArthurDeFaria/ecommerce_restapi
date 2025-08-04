package com.ckweb.rest_api.dto.shipment.quote;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QuoteRequestDTO(
    AddressDTO from,
    AddressDTO to,
    String services,
    OptionsDTO options,

    @JsonProperty("package")
    PackageDTO pack
) {}