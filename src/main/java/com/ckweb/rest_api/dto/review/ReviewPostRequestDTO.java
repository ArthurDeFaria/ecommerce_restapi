package com.ckweb.rest_api.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewPostRequestDTO(
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    Integer nota, String comentario, Long produtoId) {

    public ReviewPostRequestDTO {
        if (nota == null || nota < 1 || nota > 5) {
            throw new IllegalArgumentException("Nota deve estar entre 1 e 5");
        }
        if (comentario == null) {
            throw new IllegalArgumentException("Comentário não pode ser nulo");
        }
        if (produtoId == null || produtoId <= 0) {
            throw new IllegalArgumentException("ID do produto deve ser válido");
        }
    }

}
