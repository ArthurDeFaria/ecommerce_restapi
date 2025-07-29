package com.ckweb.rest_api.dto.cart.item;

public record CartItemGetDTO(
    Long id,
    String nome, 
    Double preco, 
    String imagem_url,
    int quantidade
    ) {
        
    public CartItemGetDTO {
        if (id == null || nome == null || preco == null || imagem_url == null || quantidade < 0) {
            throw new IllegalArgumentException("Invalid CartItemGetDTO parameters");
        }
    }
	
}
