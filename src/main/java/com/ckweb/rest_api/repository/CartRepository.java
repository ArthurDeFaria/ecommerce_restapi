package com.ckweb.rest_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ckweb.rest_api.model.Cart;
import com.ckweb.rest_api.model.User;

public interface CartRepository extends JpaRepository<Cart, Long> {
    public Optional<Cart> findById(Long id);
    public Cart findByUsuario(User usuario);
}
