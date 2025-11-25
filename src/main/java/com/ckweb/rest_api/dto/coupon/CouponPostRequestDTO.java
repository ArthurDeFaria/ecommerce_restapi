package com.ckweb.rest_api.dto.coupon;

import com.ckweb.rest_api.model.enumeration.CouponType;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record CouponPostRequestDTO(
        @NotBlank(message = "Código não pode ser vazio")
        String codigo,

        @NotNull(message = "Tipo não pode ser nulo")
        CouponType tipo, // Usa o Enum diretamente

        @NotNull(message = "Valor não pode ser nulo")
        @Positive(message = "Valor deve ser positivo")
        Double valor,

        @FutureOrPresent(message = "Data de validade deve ser hoje ou no futuro")
        LocalDate dataValidade, // Usar LocalDate para datas

        @PositiveOrZero(message = "Uso máximo não pode ser negativo")
        Integer usoMaximo, // Uso total permitido

        @Positive(message = "Uso máximo individual deve ser maior que zero")
        Integer usoMaximoIndividual // Uso por usuário
) {}