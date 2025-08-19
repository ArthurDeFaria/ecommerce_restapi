package com.ckweb.rest_api.dto.orderitem;

public record OrderItemResponseDTO(
    Long id,
    Long productId,
    String productName,
    Integer quantity,
    Double precoUnitario
) {
    
}
