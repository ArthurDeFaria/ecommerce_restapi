package com.ckweb.rest_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ckweb.rest_api.repository.ProductRepository;
import com.ckweb.rest_api.dto.product.ProductGetResponseDTO;
import com.ckweb.rest_api.dto.product.ProductPostRequestDTO;
import com.ckweb.rest_api.dto.product.ProductPutRequestDTO;
import com.ckweb.rest_api.model.Product;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public ResponseEntity<List<ProductGetResponseDTO>> findAll() {
        List<Product> products = productRepository.findAll();
        List<ProductGetResponseDTO> productDTOs = products.stream()
            .map(product -> new ProductGetResponseDTO(
            product.getId(),
            product.getNome(),
            product.getDescricao(),
            product.getPreco(),
            product.getCategoria(),
            product.getImagem_url(),
            product.getAvaliacoes()
            ))
            .toList();
        return ResponseEntity.ok().body(productDTOs);
    }

    public ResponseEntity<?> findById(Long id) {
        return productRepository.findById(id)
            .map(product -> {
                ProductGetResponseDTO dto = new ProductGetResponseDTO(
                product.getId(),
                product.getNome(),
                product.getDescricao(),
                product.getPreco(),
                product.getCategoria(),
                product.getImagem_url(),
                product.getAvaliacoes()
                );
                return ResponseEntity.ok().body(dto);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<List<ProductGetResponseDTO>> findByCategoria(String categoria) {
        Optional<List<Product>> products = productRepository.findByCategoria(categoria);
        List<ProductGetResponseDTO> productDTOs = products.get().stream()
            .map(product -> new ProductGetResponseDTO(
            product.getId(),
            product.getNome(),
            product.getDescricao(),
            product.getPreco(),
            product.getCategoria(),
            product.getImagem_url(),
            product.getAvaliacoes()
            ))
            .toList();
        return ResponseEntity.ok().body(productDTOs);
    }

    public ResponseEntity<?> searchProducts(String query) {
        List<Product> products = productRepository.findAll();
        List<ProductGetResponseDTO> filteredProducts = products.stream()
            .filter(product -> product.getNome().toLowerCase().contains(query.toLowerCase()))
            .map(product -> new ProductGetResponseDTO(
                product.getId(),
                product.getNome(),
                product.getDescricao(),
                product.getPreco(),
                product.getCategoria(),
                product.getImagem_url(),
                product.getAvaliacoes()
            ))
            .toList();

        if (filteredProducts.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok().body(filteredProducts);
    }

    public ResponseEntity<?> save(ProductPostRequestDTO product) {
        Product newProduct = new Product(
            product.nome(),
            product.categoria(),
            product.descricao(),
            product.preco(),
            product.quantidadeEstoque(),
            product.imagemUrl()
        );
        
        productRepository.save(newProduct);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> update(ProductPutRequestDTO product) {
        Optional<Product> existingProduct = productRepository.findById(product.id());
        
        if (existingProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Product updatedProduct = existingProduct.get();
        updatedProduct.setNome(product.nome());
        updatedProduct.setCategoria(product.categoria());
        updatedProduct.setDescricao(product.descricao());
        updatedProduct.setPreco(product.preco());
        updatedProduct.setQuantidadeEstoque(product.quantidadeEstoque());
        updatedProduct.setImagem_url(product.imagemUrl());
        
        productRepository.save(updatedProduct);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> delete(Long id) {
        Optional<Product> existingProduct = productRepository.findById(id);
        
        if (existingProduct.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        productRepository.delete(existingProduct.get());
        return ResponseEntity.ok().build();
    }
    
}
