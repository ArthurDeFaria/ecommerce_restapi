package com.ckweb.rest_api.dto.shipment;

import java.util.List;

public record ShipmentQuoteRequest(String cep, List<Long> produtosIds) {
    
}
