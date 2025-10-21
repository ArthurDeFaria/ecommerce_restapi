package com.ckweb.rest_api.fluxo;

import com.ckweb.rest_api.component.MercadoPagoClientInterface;
import com.ckweb.rest_api.dto.cart.CartAddItemRequestDTO;
import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceResponseDTO;
import com.ckweb.rest_api.dto.order.OrderPostRequestDTO;
import com.ckweb.rest_api.dto.user.AuthRequest;
import com.ckweb.rest_api.dto.user.RegisterRequestUser;
import com.ckweb.rest_api.model.Order;
import com.ckweb.rest_api.model.Product;
import com.ckweb.rest_api.repository.OrderRepository;
import com.ckweb.rest_api.repository.ProductRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FluxoDeCompraTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private MercadoPagoClientInterface mercadoPagoClient;

    private String userToken;
    private Product testProduct;

    @BeforeEach
    void setUp() throws Exception {
        // 1. REGISTAR UM PRODUTO
        testProduct = Product.builder()
                .nome("Teste")
                .preco(100.0)
                .quantidadeEstoque(10)
                .imagem_url("http://test.com/img.png") // <-- CORREÇÃO AQUI
                .peso(1.0) // Adicionado por segurança, caso DTOs futuros o usem
                .altura(1.0)
                .largura(1.0)
                .comprimento(1.0)
                .build();
        productRepository.save(testProduct);

        // 2. REGISTAR UM UTILIZADOR
        RegisterRequestUser registerRequest = new RegisterRequestUser(
                "Fluxo User", "fluxo@test.com", "senha123",
                "12312312312", "01/01/1990", "991234567"
        );
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // 3. OBTER O TOKEN DE LOGIN
        AuthRequest loginRequest = new AuthRequest("fluxo@test.com", "senha123");
        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        userToken = jsonNode.get("token").asText();
    }

    @Test
    void deveRealizarFluxoDeCompraCompleto() throws Exception {
        // Etapa 1: Adicionar item ao carrinho
        CartAddItemRequestDTO addItemRequest = new CartAddItemRequestDTO(testProduct.getId(), 2);

        mockMvc.perform(post("/carrinho/adicionar")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens[0].nome").value("Teste"))
                .andExpect(jsonPath("$.itens[0].quantidade").value(2));

        // Etapa 2: Configurar o Mock do Pagamento
        CreatePreferenceResponseDTO mockPaymentResponse = new CreatePreferenceResponseDTO("pref_123", "http://mp.com/pay");
        when(mercadoPagoClient.createPreference(any(), anyString())).thenReturn(mockPaymentResponse);

        // Etapa 3: Finalizar o Pedido
        OrderPostRequestDTO orderRequest = new OrderPostRequestDTO(BigDecimal.valueOf(25.0), null); // 25.0 de frete

        mockMvc.perform(post("/pedidos/finalizar")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentUrl").value("http://mp.com/pay")) // Verifica o URL de pagamento
                .andExpect(jsonPath("$.order.totalProdutos").value(200.0)) // 2 x 100.0
                .andExpect(jsonPath("$.order.totalFrete").value(25.0))
                .andExpect(jsonPath("$.order.totalPedido").value(225.0));

        // Etapa 4 (Verificação Opcional): Garantir que o pedido foi salvo na base de dados
        List<Order> orders = orderRepository.findAll();
        assertEquals(1, orders.size());
        assertEquals(225.0, orders.get(0).getTotalPedido().doubleValue());
        assertEquals("fluxo@test.com", orders.get(0).getUsuario().getEmail());
    }
}