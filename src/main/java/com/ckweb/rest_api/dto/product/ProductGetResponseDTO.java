package com.ckweb.rest_api.dto.product;

import java.util.List;

import com.ckweb.rest_api.model.Review;

public record ProductGetResponseDTO(
    Long id,
    String nome,
    String descricao,
    Double preco,
    String categoria,
    String imagemUrl,
    List<Review> avaliacoes
) {
    
}
