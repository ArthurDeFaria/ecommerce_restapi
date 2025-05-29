package com.ckweb.rest_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ckweb.rest_api.dto.AuthRequest;
import com.ckweb.rest_api.dto.AuthResponse;
import com.ckweb.rest_api.dto.RegisterRequestAdm;
import com.ckweb.rest_api.dto.RegisterRequestUser;
import com.ckweb.rest_api.repository.UserRepository;
import com.ckweb.rest_api.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.ckweb.rest_api.model.Cargo;
import com.ckweb.rest_api.model.Cart;
import com.ckweb.rest_api.model.User;

import jakarta.validation.Valid;
import lombok.var;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação de Usuários", description = "Operações de autenticação com usuários")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    @Operation(summary = "Endpoint de Login", description = "Retorna o JWT Token que deve ser utilizado emrequisições protegidas pelo Spring Security(Endpoints que tem o cadeado).")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest authRequest) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = jwtService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/registro")
    @Operation(summary = "Endpoint de Registro USER", description = "Utilizado para registrar um usuário comum(Do tipo USER).")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequestUser registerRequest) {
        if (this.userRepository.findByEmail(registerRequest.email()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(registerRequest.senha());

        User newUser = new User(
            registerRequest.nome(),
            registerRequest.email(),
            encryptedPassword,
            registerRequest.cpf(),
            registerRequest.dataNascimento(),
            registerRequest.telefone(),
            Cargo.USER
        );

        Cart carrinho = Cart.builder()
            .usuario(newUser)
            .build();
            
        newUser.setCarrinho(carrinho);

        this.userRepository.save(newUser);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/registro/adm")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Endpoint de registro ADMIN e MANAGER", description = "Utilizado para registrar um usuário com permissões(Do tipo ADMIN ou MANAGER).")
    public ResponseEntity<?> registerAdm(@RequestBody @Valid RegisterRequestAdm registerRequest) {
        if (this.userRepository.findByEmail(registerRequest.email()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(registerRequest.senha());

        User newUser = new User(
            registerRequest.nome(),
            registerRequest.email(),
            encryptedPassword,
            registerRequest.cpf(),
            registerRequest.dataNascimento(),
            registerRequest.telefone(),
            registerRequest.cargo()
        );

        Cart carrinho = Cart.builder()
            .usuario(newUser)
            .build();
            
        newUser.setCarrinho(carrinho);

        this.userRepository.save(newUser);

        return ResponseEntity.ok().build();
    }
}
