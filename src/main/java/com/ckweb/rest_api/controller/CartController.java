package com.ckweb.rest_api.controller;

import com.ckweb.rest_api.dto.cart.CartAddItemRequestDTO;
import com.ckweb.rest_api.dto.cart.CartGetResponseDTO;
import com.ckweb.rest_api.service.impl.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/carrinho")
@SecurityRequirement(name = "bearAuth")
@Tag(name = "Carrinho", description = "Operações com o carrinho de compras")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/info")
    @Operation(summary = "Obter informações do carrinho do usuário logado", description = "Retorna o carrinho do usuário logado a partir do ID no JWT token.")
    public ResponseEntity<CartGetResponseDTO> getCartInfo(@RequestHeader("Authorization") String token) {
        CartGetResponseDTO response = cartService.getCartInfo(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/adicionar")
    @Operation(summary = "Adicionar item ao carrinho", description = "Adiciona um item ao carrinho do usuário logado.")
    public ResponseEntity<CartGetResponseDTO> addToCart(@RequestHeader("Authorization") String token, @RequestBody CartAddItemRequestDTO request) {
        CartGetResponseDTO response = cartService.addItemToCart(token, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/atualizar/{itemId}")
    @Operation(summary = "Atualizar quantidade de um item no carrinho", description = "Atualiza a quantidade de um item específico no carrinho do usuário logado.")
    public ResponseEntity<CartGetResponseDTO> updateItemQuantity(@RequestHeader("Authorization") String token, @PathVariable Long itemId, @RequestParam int quantity) {
        CartGetResponseDTO response = cartService.updateItemQuantity(token, itemId, quantity);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/remover/{id}")
    @Operation(summary = "Remover item do carrinho", description = "Remove um item específico do carrinho do usuário logado.")
    public ResponseEntity<CartGetResponseDTO> removeFromCart(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        CartGetResponseDTO response = cartService.removeItemFromCart(token, id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/limpar")
    @Operation(summary = "Esvaziar carrinho", description = "Remove todos os itens do carrinho do usuário logado.")
    public ResponseEntity<CartGetResponseDTO> clearCart(@RequestHeader("Authorization") String token) {
        CartGetResponseDTO response = cartService.clearCart(token);
        return ResponseEntity.ok(response);
    }

}