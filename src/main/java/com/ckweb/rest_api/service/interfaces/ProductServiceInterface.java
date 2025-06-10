package com.ckweb.rest_api.service.interfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.ckweb.rest_api.dto.product.ProductGetResponseDTO;
import com.ckweb.rest_api.dto.product.ProductPostRequestDTO;

public interface ProductServiceInterface {
    public ResponseEntity<List<ProductGetResponseDTO>> findAll();
    public ResponseEntity<?> findById(Long id);
    public ResponseEntity<List<ProductGetResponseDTO>> findByCategoria(String categoria);
    public ResponseEntity<?> searchProducts(String query);
    public ResponseEntity<?> save(ProductPostRequestDTO product);
    public ResponseEntity<?> update(Long id, ProductPostRequestDTO product);
    public ResponseEntity<?> delete(Long id);
}
