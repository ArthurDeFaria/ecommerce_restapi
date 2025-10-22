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

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRegistrarNovoUsuarioComSucesso() throws Exception {
        RegisterRequestUser request = new RegisterRequestUser(
                "Test User",
                "registro@test.com",
                "senha123",
                "11122233344",
                "01/01/2000",
                "991112222"
        );

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void naoDeveRegistrarUsuarioComEmailDuplicado() throws Exception {
        RegisterRequestUser request1 = new RegisterRequestUser(
                "User 1", "duplicado@test.com", "senha123",
                "11122233344", "01/01/2000", "991112222"
        );
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        RegisterRequestUser request2 = new RegisterRequestUser(
                "User 2", "duplicado@test.com", "senha456",
                "55566677788", "02/02/2000", "993334444"
        );

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deveAutenticarUsuarioRegistradoEretornarToken() throws Exception {
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

        AuthRequest loginRequest = new AuthRequest("login@test.com", "senhaForte123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void naoDeveRegistrarUsuarioComDadosInvalidos() throws Exception {
        RegisterRequestUser request = new RegisterRequestUser(
                "Test User",
                "email-invalido.com", // Email inválido
                "123", // Senha curta (min 6)
                "", // CPF vazio
                "01/01/2000",
                "991112222"
        );

        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()) // Espera 400
                .andExpect(jsonPath("$.message").value("Erro de validação"))
                .andExpect(jsonPath("$.errors.email").value("Formato de email inválido"))
                .andExpect(jsonPath("$.errors.senha").value("Senha deve ter no mínimo 6 caracteres"))
                .andExpect(jsonPath("$.errors.cpf").value("CPF não pode ser vazio"));
    }
}