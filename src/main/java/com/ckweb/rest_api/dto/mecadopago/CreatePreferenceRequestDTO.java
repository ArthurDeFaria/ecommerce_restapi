package com.ckweb.rest_api.dto.mecadopago;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CreatePreferenceRequestDTO(
    Long userId,

    BigDecimal totalAmount,

    @NotNull
    PayerDTO payer,

    @NotNull
    BackUrlsDTO backUrls,

    @NotNull
    DeliveryAddressDTO deliveryAddress,

    @NotNull
    List<ItemDTO> items
) {
    public record PayerDTO(
        String name,
        String email
    ) {}

    public record BackUrlsDTO(
        String success,
        String failure,
        String pending
    ) {}

    public record DeliveryAddressDTO(
        String zipCode,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state
    ) {}

    public record ItemDTO(
        String id,
        String title,
        Integer quantity,
        BigDecimal unitPrice
    ) {}
}
