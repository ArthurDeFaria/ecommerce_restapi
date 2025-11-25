package com.ckweb.rest_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ckweb.rest_api.dto.favorite.FavoritePostRequestDTO;
import com.ckweb.rest_api.dto.favorite.FavoriteResponseDTO;
import com.ckweb.rest_api.service.interfaces.FavoriteServiceInterface;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/favoritos")
@SecurityRequirement(name = "bearAuth")
@Tag(name = "Favoritos", description = "Operações com os favoritos dos usuários")
public class FavoriteController {

    @Autowired
    private FavoriteServiceInterface favoriteService;
    
    @GetMapping
    @Operation(summary = "Listar favoritos", description = "Lista os produtos favoritos do usuário logado.")
    public ResponseEntity<List<FavoriteResponseDTO>> findAllByUsuarioId(@RequestHeader("Authorization") String token) {
        List<FavoriteResponseDTO> response = favoriteService.findAllByUsuarioId(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Adicionar favorito", description = "Adiciona um produto aos favoritos do usuário logado.")
    public ResponseEntity<FavoriteResponseDTO> save(@RequestHeader("Authorization") String token, @RequestBody FavoritePostRequestDTO request) {
        FavoriteResponseDTO response = favoriteService.save(token, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover favorito", description = "Remove um produto dos favoritos do usuário logado.")
    public ResponseEntity<?> delete(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        favoriteService.delete(token, id);
        return ResponseEntity.ok().build();
    }
}
