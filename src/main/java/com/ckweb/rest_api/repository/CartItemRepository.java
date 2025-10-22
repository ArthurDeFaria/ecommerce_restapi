package com.ckweb.rest_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.ckweb.rest_api.model.Cart;
import com.ckweb.rest_api.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    public List<CartItem> findByCarrinho(Cart cart);
    
    @Transactional
    void deleteByCarrinho(Cart cart);
    
    long countByCarrinhoId(Long carrinhoId);
}
