package com.ckweb.rest_api.dto.order;

import java.math.BigDecimal;
import java.util.List;

import com.ckweb.rest_api.dto.orderitem.OrderItemResponseDTO;
import com.ckweb.rest_api.dto.payment.PaymentResonseDTO;
import com.ckweb.rest_api.dto.shipment.ShipmentResponseDTO;

public record OrderResponseDTO(
    Long id,
    String dataPedido,

    BigDecimal totalProdutos,
    BigDecimal totalFrete,
    BigDecimal totalPedido,
    Long usuarioId,
    Long cupomId,
    PaymentResonseDTO pagamento,
    ShipmentResponseDTO envio,
    List<OrderItemResponseDTO> itensPedido
) {
    
}
