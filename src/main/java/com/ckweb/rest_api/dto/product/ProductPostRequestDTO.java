package com.ckweb.rest_api.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record ProductPostRequestDTO(
    @NotBlank(message = "Nome não pode ser vazio")
    String nome,
    
    @NotBlank(message = "Categoria não pode ser vazia")
    String categoria,
    
    String descricao,
    
    @NotNull(message = "Preço não pode ser nulo")
    @Positive(message = "Preço deve ser maior que zero")
    double preco,
    
    @NotNull(message = "Quantidade não pode ser nula")
    @PositiveOrZero(message = "Quantidade não pode ser negativa")
    int quantidadeEstoque,
    
    String imagemUrl,
    
    @Positive(message = "Peso deve ser positivo")
    Double peso,
    @Positive(message = "Altura deve ser positiva")
    Double altura,
    @Positive(message = "Largura deve ser positiva")
    Double largura,
    @Positive(message = "Comprimento deve ser positivo")
    Double comprimento
) {}