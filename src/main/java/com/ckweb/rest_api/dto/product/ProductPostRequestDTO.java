package com.ckweb.rest_api.dto.product;

public record ProductPostRequestDTO(
    String nome,
    String categoria,
    String descricao,
    double preco,
    int quantidadeEstoque,
    String imagemUrl,
    Double peso,
    Double altura,
    Double largura,
    Double comprimento
) {
    
}
