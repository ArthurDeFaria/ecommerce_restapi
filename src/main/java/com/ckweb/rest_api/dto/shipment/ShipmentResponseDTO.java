package com.ckweb.rest_api.dto.shipment;

import com.ckweb.rest_api.model.Address;
import com.ckweb.rest_api.model.enumeration.ShipmentStatus;

public record ShipmentResponseDTO(
    Long id,
    String transportadora,
    String tipoEnvio,
    ShipmentStatus statusEnvio,
    String codRastreamento,
    String dataEnvio,
    String dataEntregaPrevista,
    String dataEntregaRealizada,
    Address endereco
) {
    
}
