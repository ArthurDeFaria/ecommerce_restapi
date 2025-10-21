package com.ckweb.rest_api.service.interfaces;

import com.ckweb.rest_api.dto.cart.CartAddItemRequestDTO;
import com.ckweb.rest_api.dto.cart.CartGetResponseDTO;
import com.ckweb.rest_api.model.User;

public interface CartServiceInterface {

    public CartGetResponseDTO findById(Long id);
    public CartGetResponseDTO getCartInfo(String token);
    public CartGetResponseDTO addItemToCart(String token, CartAddItemRequestDTO request);
    public CartGetResponseDTO removeItemFromCart(String token, Long id);
    public CartGetResponseDTO updateItemQuantity(String token, Long itemId, int quantity);
    public CartGetResponseDTO clearCart(String token);
    public void clearCartByUser(User user);
    
}
