package com.ckweb.rest_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ckweb.rest_api.dto.review.ReviewPostRequestDTO;
import com.ckweb.rest_api.dto.review.ReviewResponseDTO;
import com.ckweb.rest_api.service.impl.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/avaliacoes")
@Tag(name = "Avaliações", description = "Operações com as avaliações dos produtos")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    @GetMapping("/{id}")
    @Operation(summary = "Buscar avaliações por ID de produto", description = "Retorna uma lista de avaliações pelo ID do produto")
    public ResponseEntity<List<ReviewResponseDTO>> findAllById(@PathVariable Long id) {
        List<ReviewResponseDTO> response = reviewService.findAllById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @SecurityRequirement(name = "bearAuth")
    @Operation(summary = "Criar nova avaliação", description = "Cria uma nova avaliação para um produto")
    public ResponseEntity<ReviewResponseDTO> save(@RequestHeader("Authorization") String token, @RequestBody ReviewPostRequestDTO request) {
        System.out.println("Token: " + token);
        System.out.println("Request: " + request);
        ReviewResponseDTO response = reviewService.save(token, request);
        return ResponseEntity.ok(response);
    }
    
}
