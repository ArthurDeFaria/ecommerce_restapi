package com.ckweb.rest_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ckweb.rest_api.dto.UserAutoUpdateDTO;
import com.ckweb.rest_api.dto.UserGetDTO;
import com.ckweb.rest_api.dto.UserUpdateDTO;
import com.ckweb.rest_api.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/usuarios")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuários", description = "Operações com usuários")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping
    @Operation(summary = "Listar as informações de todos os usuários", description = "Retorna uma lista de todos os usuarios. Precisa do cargo ADMIN")
    public ResponseEntity<List<UserGetDTO>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Listar as informações um usuário pelo ID", description = "Retorna um usuário a partir do ID. Precisa do cargo ADMIN ou MANAGER.")
    public ResponseEntity<UserGetDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/info")
    @Operation(summary = "Lista as informações do usuário logado", description = "Retorna o usuario logando a partir do ID no JWT token.")
    public ResponseEntity<UserGetDTO> getUsuarioInfo(@RequestHeader("Authorization") String token) {
        UserGetDTO user = userService.getUsuarioLogadoInfo(token);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    @Operation(summary = "Atualiza as informações de um usuário qualquer", description = "Atualiza um usuário qualquer no banco de dados a partir dos dados no body. Precisa do cargo ADMIN ou MANAGER.")
    public ResponseEntity<UserGetDTO> update(@RequestBody UserUpdateDTO user) {
        try {
            UserGetDTO usuarioAtualizado = userService.update(user);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/info")
    @Operation(summary = "Atualiza as informações do usuário logado", description = "Atualiza o usuário, que está logado, no banco de dados a partir dos dados no body.")
    public ResponseEntity<UserGetDTO> updateUsuarioInfo(@RequestHeader("Authorization") String token, @RequestBody UserAutoUpdateDTO user) {
        try {
            UserGetDTO usuarioAtualizado = userService.updateUsuarioLogadoInfo(token, user);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta o usuário pelo ID", description = "Deleta o usuaŕio a partir do ID. Precisa do cargo ADMIN.")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/info")
    @Operation(summary = "Deleta o usuário logado", description = "Deleta o usuaŕio a partir do ID dentro do JWT token.")
    public ResponseEntity<Void> deleteUsuarioInfo(@RequestHeader("Authorization") String token) {
        try {
            userService.deleteUsuarioLogadoInfo(token);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
