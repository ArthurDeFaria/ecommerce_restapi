package com.ckweb.rest_api.service.interfaces;

import java.util.List;

import com.ckweb.rest_api.dto.shipment.QuoteResponseDTO;

public interface ShipmentServiceInterface {
    public List<QuoteResponseDTO> cotarFrete(String cep, List<Long> produtosIds);
}
