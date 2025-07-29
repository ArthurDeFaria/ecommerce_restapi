package com.ckweb.rest_api.dto.cart;

import java.util.List;

import com.ckweb.rest_api.dto.cart.item.CartItemGetDTO;

public record CartGetResponseDTO(Long id, List<CartItemGetDTO> itens) {
    
}
