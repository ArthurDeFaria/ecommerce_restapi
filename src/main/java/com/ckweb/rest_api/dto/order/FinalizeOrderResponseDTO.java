package com.ckweb.rest_api.dto.order;

public record FinalizeOrderResponseDTO(
    OrderResponseDTO order,
    String paymentUrl
) {}