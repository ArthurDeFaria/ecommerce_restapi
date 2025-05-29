package com.ckweb.rest_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ckweb.rest_api.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    public Optional<Product> findById(Long id);
    public Optional<Product> findByNome(String nome);
    public Optional<List<Product>> findByCategoria(String categoria);
}
