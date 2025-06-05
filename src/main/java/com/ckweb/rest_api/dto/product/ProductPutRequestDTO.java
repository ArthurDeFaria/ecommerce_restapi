package com.ckweb.rest_api.dto.product;

public record ProductPutRequestDTO(
    Long id,
    String nome,
    String categoria,
    String descricao,
    double preco,
    int quantidadeEstoque,
    String imagemUrl
) {

}
