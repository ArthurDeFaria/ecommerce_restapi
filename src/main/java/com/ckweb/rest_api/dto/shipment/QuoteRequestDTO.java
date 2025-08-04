package com.ckweb.rest_api.dto.shipment;

import com.ckweb.rest_api.dto.shipment.quote.AddressDTO;
import com.ckweb.rest_api.dto.shipment.quote.OptionsDTO;
import com.ckweb.rest_api.dto.shipment.quote.PackageDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

public record QuoteRequestDTO(
    AddressDTO from,
    AddressDTO to,
    String services,
    OptionsDTO options,

    @JsonProperty("package")
    PackageDTO pack
) {}