package com.ckweb.rest_api.controller;

import com.ckweb.rest_api.dto.coupon.*;
import com.ckweb.rest_api.model.Cart;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.repository.CartRepository;
import com.ckweb.rest_api.repository.UserRepository;
import com.ckweb.rest_api.service.interfaces.CouponServiceInterface;
import com.ckweb.rest_api.service.interfaces.JwtServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/cupons")
@Tag(name = "Cupons", description = "Operações com cupons de desconto")
@SecurityRequirement(name = "bearerAuth")
public class CouponController {

    @Autowired
    private CouponServiceInterface couponService;

    @Autowired
    private JwtServiceInterface jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar novo cupom", description = "Cria um novo cupom de desconto.")
    public ResponseEntity<CouponResponseDTO> createCoupon(@Valid @RequestBody CouponPostRequestDTO requestDTO) {
        CouponResponseDTO createdCoupon = couponService.createCoupon(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCoupon);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Listar todos os cupons", description = "Retorna uma lista de todos os cupons cadastrados.")
    public ResponseEntity<List<CouponResponseDTO>> listCoupons() {
        return ResponseEntity.ok(couponService.listCoupons());
    }

    @GetMapping("/validar/{codigo}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MANAGER')")
    @Operation(summary = "Validar cupom", description = "Verifica se um cupom é válido para o usuário atual e seu carrinho.")
    public ResponseEntity<ValidateCouponResponseDTO> validateCoupon(@PathVariable String codigo, @RequestHeader("Authorization") String token) {
        String email = jwtService.extractEmailFromToken(token.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Ou lançar exceção
        }
        Cart cart = cartRepository.findByUsuario(user); // Busca o carrinho atual

        ValidateCouponResponseDTO response = couponService.validateCoupon(codigo, user.getId(), cart != null ? cart.getItens() : Collections.emptyList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir cupom", description = "Exclui um cupom pelo ID.")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
}