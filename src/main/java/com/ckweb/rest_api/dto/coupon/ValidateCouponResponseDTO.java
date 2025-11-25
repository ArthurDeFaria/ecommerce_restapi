package com.ckweb.rest_api.dto.coupon;

import java.math.BigDecimal;

public record ValidateCouponResponseDTO(
        boolean valido,
        String mensagem,
        BigDecimal valorDesconto,
        Long couponId
) {}