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
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.service.UserService;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/usuarios")
public class UserController {
    
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/info")
    public ResponseEntity<UserGetDTO> getUsuarioInfo(@RequestHeader("Authorization") String token) {
        UserGetDTO user = userService.getUsuarioLogadoInfo(token);
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<UserGetDTO> update(@RequestBody UserUpdateDTO user) {
        try {
            UserGetDTO usuarioAtualizado = userService.update(user);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/info")
    public ResponseEntity<UserGetDTO> updateUsuarioInfo(@RequestHeader("Authorization") String token, @RequestBody UserAutoUpdateDTO user) {
        try {
            UserGetDTO usuarioAtualizado = userService.updateUsuarioLogadoInfo(token, user);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/info")
    public ResponseEntity<Void> deleteUsuarioInfo(@RequestHeader("Authorization") String token) {
        try {
            userService.deleteUsuarioLogadoInfo(token);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
