package com.ckweb.rest_api.dto.coupon;

import jakarta.validation.constraints.NotBlank;

public record ValidateCouponRequestDTO(
        @NotBlank String codigo
) {}