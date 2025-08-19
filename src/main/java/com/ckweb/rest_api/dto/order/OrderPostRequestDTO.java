package com.ckweb.rest_api.dto.order;

import java.math.BigDecimal;

public record OrderPostRequestDTO(
    BigDecimal totalFrete,
    Long couponId
) {
    
}
