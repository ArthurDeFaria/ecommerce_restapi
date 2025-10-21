package com.ckweb.rest_api.controller;

import com.ckweb.rest_api.dto.user.AuthRequest;
import com.ckweb.rest_api.dto.user.RegisterRequestUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @SpringBootTest carrega o contexto completo da aplicação (incluindo a base H2)
@SpringBootTest
// @AutoConfigureMockMvc habilita o MockMvc para fazer requisições HTTP falsas
@AutoConfigureMockMvc
// @Transactional garante que os dados criados (ex: registo) sejam revertidos após o teste
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Utilitário do Spring para converter objetos Java <-> JSON

    @Test
    void deveRegistrarNovoUsuarioComSucesso() throws Exception {
        // 1. Given (Arrange)
        RegisterRequestUser request = new RegisterRequestUser(
                "Test User",
                "registro@test.com",
                "senha123",
                "11122233344",
                "01/01/2000",
                "991112222"
        );

        // 2. When (Act) & 3. Then (Assert)
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void naoDeveRegistrarUsuarioComEmailDuplicado() throws Exception {
        // 1. Given (Arrange)
        RegisterRequestUser request1 = new RegisterRequestUser(
                "User 1", "duplicado@test.com", "senha123",
                "11122233344", "01/01/2000", "991112222"
        );
        // Primeiro registo (deve funcionar)
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // Segunda tentativa com o mesmo email
        RegisterRequestUser request2 = new RegisterRequestUser(
                "User 2", "duplicado@test.com", "senha456",
                "55566677788", "02/02/2000", "993334444"
        );

        // 2. When (Act) & 3. Then (Assert)
        // Esperamos um "Bad Request" (400)
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAutenticarUsuarioRegistradoEretornarToken() throws Exception {
        // 1. Given (Arrange)
        // Primeiro, regista um usuário
        RegisterRequestUser registerRequest = new RegisterRequestUser(
                "Login Test User",
                "login@test.com",
                "senhaForte123",
                "98765432100",
                "01/01/2000",
                "998887777"
        );
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Prepara a requisição de login
        AuthRequest loginRequest = new AuthRequest("login@test.com", "senhaForte123");

        // 2. When (Act) & 3. Then (Assert)
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                // Verifica se a resposta JSON contém a chave "token" e se ela não está vazia
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}