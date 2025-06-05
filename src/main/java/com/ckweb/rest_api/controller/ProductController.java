package com.ckweb.rest_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ckweb.rest_api.dto.product.ProductGetResponseDTO;
import com.ckweb.rest_api.dto.product.ProductPostRequestDTO;
import com.ckweb.rest_api.dto.product.ProductPutRequestDTO;
import com.ckweb.rest_api.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/produtos")
// @SecurityRequirement(name = "bearAuth")
@Tag(name = "Produtos", description = "Operações com os produtos")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping
    @Operation(summary = "Listar todos os produtos", description = "Retorna uma lista de todos os produtos disponíveis.")
    public ResponseEntity<List<ProductGetResponseDTO>> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar produto por ID", description = "Retorna os detalhes de um produto específico pelo seu ID.")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping("/categoria/{categoria}")
    @Operation(summary = "Buscar produtos por categoria", description = "Retorna uma lista de produtos filtrados por categoria.")
    public ResponseEntity<List<ProductGetResponseDTO>> findByCategoria(@PathVariable String categoria) {
        return productService.findByCategoria(categoria);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar produtos por consulta", description = "Permite buscar produtos com base em uma consulta de texto.")
    public ResponseEntity<?> searchProducts(@RequestParam String query) {
        return productService.searchProducts(query);
    }

    @PostMapping
    @SecurityRequirement(name = "bearAuth")
    @Operation(summary = "Salvar novo produto", description = "Cria um novo produto com os detalhes fornecidos.")
    public ResponseEntity<?> save(@RequestBody ProductPostRequestDTO product) {
        return productService.save(product);
    }

    @PutMapping
    @SecurityRequirement(name = "bearAuth")
    @Operation(summary = "Atualizar produto", description = "Atualiza os detalhes de um produto existente.")
    public ResponseEntity<?> update(@RequestBody ProductPutRequestDTO product) {
        return productService.update(product);
    }

    @DeleteMapping("/{id}")
    @SecurityRequirement(name = "bearAuth")
    @Operation(summary = "Deletar produto", description = "Remove um produto do banco de dados pelo seu ID.")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return productService.delete(id);
    }
    
}
