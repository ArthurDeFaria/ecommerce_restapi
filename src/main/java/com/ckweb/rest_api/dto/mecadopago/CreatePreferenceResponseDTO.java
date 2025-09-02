package com.ckweb.rest_api.dto.mecadopago;

public record CreatePreferenceResponseDTO(
        String preferenceId,
        String redirectUrl
) {}