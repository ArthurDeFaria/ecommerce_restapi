package com.ckweb.rest_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.ckweb.rest_api.dto.user.AuthRequest;
import com.ckweb.rest_api.dto.user.RegisterRequestAdm;
import com.ckweb.rest_api.dto.user.RegisterRequestUser;
import com.ckweb.rest_api.service.impl.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação de Usuários", description = "Operações de autenticação com usuários")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Endpoint de Login", description = "Retorna o JWT Token que deve ser utilizado emrequisições protegidas pelo Spring Security(Endpoints que tem o cadeado).")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest authRequest) {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @PostMapping("/registro")
    @Operation(summary = "Endpoint de Registro USER", description = "Utilizado para registrar um usuário comum(Do tipo USER).")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequestUser registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("/registro/adm")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Endpoint de registro ADMIN e MANAGER", description = "Utilizado para registrar um usuário com permissões(Do tipo ADMIN ou MANAGER).")
    public ResponseEntity<?> registerAdm(@RequestBody @Valid RegisterRequestAdm registerRequest) {
        return authService.registerAdm(registerRequest);
    }
}
