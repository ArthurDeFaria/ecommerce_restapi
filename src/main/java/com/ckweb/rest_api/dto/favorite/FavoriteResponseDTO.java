package com.ckweb.rest_api.dto.favorite;

public record FavoriteResponseDTO(
    Long id, 
    Long produtoId,
    String nome,
    Double preco,
    String imagem_url,
    String data_adicionado) {
}