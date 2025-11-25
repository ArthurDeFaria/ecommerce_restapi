package com.ckweb.rest_api.controller;

import com.ckweb.rest_api.dto.coupon.CouponPostRequestDTO;
import com.ckweb.rest_api.dto.coupon.CouponResponseDTO;
import com.ckweb.rest_api.dto.coupon.ValidateCouponResponseDTO;
import com.ckweb.rest_api.model.Cart;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.model.enumeration.Cargo;
import com.ckweb.rest_api.model.enumeration.CouponType;
import com.ckweb.rest_api.repository.CartRepository;
import com.ckweb.rest_api.repository.UserRepository;
import com.ckweb.rest_api.service.interfaces.CouponServiceInterface;
import com.ckweb.rest_api.service.interfaces.JwtServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpHeaders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mockamos o serviço pois o foco é testar o Controller e a Segurança
    @MockBean
    private CouponServiceInterface couponService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private JwtServiceInterface jwtService;

    private User testAdmin;
    private User testManager;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Criar usuários com diferentes roles para testes de permissão
        testAdmin = User.builder().email("admin_coupon@test.com").senha("pw").cargo(Cargo.ADMIN).build();
        testManager = User.builder().email("manager_coupon@test.com").senha("pw").cargo(Cargo.MANAGER).build();
        testUser = User.builder().email("user_coupon@test.com").senha("pw").cargo(Cargo.USER).build();
        userRepository.saveAll(List.of(testAdmin, testManager, testUser));

        // Criar carrinho para o user de teste
        Cart userCart = Cart.builder().usuario(testUser).build();
        cartRepository.save(userCart);
        testUser.setCarrinho(userCart); // Associar
    }

    // --- Testes de Permissão ---

    @Test
    @WithMockUser(username = "admin_coupon@test.com", roles = "ADMIN")
    void adminDeveConseguirCriarCupom() throws Exception {
        CouponPostRequestDTO request = new CouponPostRequestDTO("ADMINCUPOM", CouponType.FIXED_AMOUNT, 10.0, LocalDate.now().plusDays(1), null, 1);
        CouponResponseDTO response = new CouponResponseDTO(1L, "ADMINCUPOM", CouponType.FIXED_AMOUNT, 10.0, LocalDate.now().plusDays(1), null, 1, true);

        when(couponService.createCoupon(any(CouponPostRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "manager_coupon@test.com", roles = "MANAGER")
    void managerNaoDeveConseguirCriarCupom() throws Exception {
        CouponPostRequestDTO request = new CouponPostRequestDTO("MANAGERCUPOM", CouponType.FIXED_AMOUNT, 10.0, LocalDate.now().plusDays(1), null, 1);
        mockMvc.perform(post("/cupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "manager_coupon@test.com", roles = "MANAGER")
    void managerDeveConseguirListarCupons() throws Exception {
        when(couponService.listCoupons()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/cupons"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user_coupon@test.com", roles = "USER")
    void userNaoDeveConseguirListarCupons() throws Exception {
        mockMvc.perform(get("/cupons"))
                .andExpect(status().isForbidden());
    }

    @Test
    // @WithMockUser(username = "user_coupon@test.com", roles = "USER")
    void userDeveConseguirValidarCupom() throws Exception {
        // 1. Given
        ValidateCouponResponseDTO validResponse = new ValidateCouponResponseDTO(true, "Válido", BigDecimal.TEN, 1L);
        
        // 'testUser' foi criado e salvo no @BeforeEach
        // Gerar um token real para este utilizador
        String userToken = jwtService.generateToken(testUser);

        // 2. Configurar o Mock do Serviço
        // O controller vai extrair o ID de 'testUser' e buscar o 'testUserCart'
        when(couponService.validateCoupon(
            eq("TEST"),                 // O código do cupom
            eq(testUser.getId()),       // O ID do usuário
            anyList()                   // O carrinho (não importa para este mock)
        )).thenReturn(validResponse);

        // 3. When & Then
        mockMvc.perform(get("/cupons/validar/TEST")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)) // <-- ADICIONAR O HEADER COM TOKEN REAL
                .andExpect(status().isOk()) // <-- Agora deve ser 200
                .andExpect(jsonPath("$.valido").value(true))
                .andExpect(jsonPath("$.mensagem").value("Válido"));
    }

    @Test
    @WithMockUser(username = "admin_coupon@test.com", roles = "ADMIN")
    void adminDeveConseguirDeletarCupom() throws Exception {
        // Configura o mock para não fazer nada (void) quando delete for chamado
        doNothing().when(couponService).deleteCoupon(anyLong());

        mockMvc.perform(delete("/cupons/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "manager_coupon@test.com", roles = "MANAGER")
    void managerNaoDeveConseguirDeletarCupom() throws Exception {
        mockMvc.perform(delete("/cupons/1"))
                .andExpect(status().isForbidden());
    }
}