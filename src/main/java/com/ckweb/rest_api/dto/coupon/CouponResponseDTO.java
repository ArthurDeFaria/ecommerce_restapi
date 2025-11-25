package com.ckweb.rest_api.dto.coupon;

import com.ckweb.rest_api.model.enumeration.CouponType;
import java.time.LocalDate;

public record CouponResponseDTO(
        Long id,
        String codigo,
        CouponType tipo,
        Double valor,
        LocalDate dataValidade,
        Integer usoMaximo,
        Integer usoMaximoIndividual,
        Boolean ativo
) {}