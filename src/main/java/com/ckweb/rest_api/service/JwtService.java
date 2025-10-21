package com.ckweb.rest_api.service;

import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.service.interfaces.JwtServiceInterface;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;

@Service
public class JwtService implements JwtServiceInterface{

    @Value("${api.security.jwt.secret}")
    private String secret;

    @Override
    public String generateToken(User user) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("ckweb")
                    .withSubject(user.getEmail())
                    .withExpiresAt(getExpirationTime())
                    .sign(algorithm);
                    return token;
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error while generating JWT token", e);
        }
    }

    @Override
    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            
            return JWT.require(algorithm)
                    .withIssuer("ckweb")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException e) {
            return "";
        }
    }

    private Instant getExpirationTime() {
        return LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.of("-03:00"));
    }

    @Override
    public String extractEmailFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            DecodedJWT decodedJWT = JWT.require(algorithm)
                .withIssuer("ckweb")
                .build()
                .verify(token);
            return decodedJWT.getSubject(); // aqui retorna o email
        } catch (Exception e) {
            throw new RuntimeException("Token inválido ou expirado", e);
        }
    }

}
