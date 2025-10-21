package com.ckweb.rest_api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ckweb.rest_api.dto.user.AuthRequest;
import com.ckweb.rest_api.dto.user.AuthResponse;
import com.ckweb.rest_api.dto.user.RegisterRequestAdm;
import com.ckweb.rest_api.dto.user.RegisterRequestUser;
import com.ckweb.rest_api.model.Cart;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.model.enumeration.Cargo;
import com.ckweb.rest_api.repository.UserRepository;
import com.ckweb.rest_api.service.interfaces.AuthServiceInterface;
import com.ckweb.rest_api.service.interfaces.JwtServiceInterface;

@Service
public class AuthService implements AuthServiceInterface{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtServiceInterface jwtService;

    public AuthResponse login(AuthRequest authRequest){
        var usernamePassword = new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = jwtService.generateToken((User) auth.getPrincipal());
        return new AuthResponse(token);
    }

    public ResponseEntity<?> register(RegisterRequestUser registerRequest) {
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

    public ResponseEntity<?> registerAdm(RegisterRequestAdm registerRequest) {
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
