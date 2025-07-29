package com.ckweb.rest_api.dto.cart;

public record CartAddItemRequestDTO (Long idProduto, int quantidade) {
    
    public CartAddItemRequestDTO {
        if (idProduto == null) {
            throw new IllegalArgumentException("idProduto must not be null");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("quantidade must be greater than zero");
        }
    }
    
}
