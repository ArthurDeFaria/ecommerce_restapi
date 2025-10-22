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
        testProduct = Product.builder()
                .nome("Teste")
                .preco(100.0)
                .quantidadeEstoque(10)
                .imagem_url("http://test.com/img.png")
                .peso(1.0)
                .altura(1.0)
                .largura(1.0)
                .comprimento(1.0)
                .build();
        productRepository.save(testProduct);

        RegisterRequestUser registerRequest = new RegisterRequestUser(
                "Fluxo User", "fluxo@test.com", "senha123",
                "12312312312", "01/01/1990", "991234567"
        );
        mockMvc.perform(post("/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

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
        CartAddItemRequestDTO addItemRequest = new CartAddItemRequestDTO(testProduct.getId(), 2);

        mockMvc.perform(post("/carrinho/adicionar")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itens[0].nome").value("Teste"))
                .andExpect(jsonPath("$.itens[0].quantidade").value(2));

        CreatePreferenceResponseDTO mockPaymentResponse = new CreatePreferenceResponseDTO("pref_123", "http://mp.com/pay");
        when(mercadoPagoClient.createPreference(any(), anyString())).thenReturn(mockPaymentResponse);

        OrderPostRequestDTO orderRequest = new OrderPostRequestDTO(BigDecimal.valueOf(25.0), null);

        mockMvc.perform(post("/pedidos/finalizar")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentUrl").value("http://mp.com/pay"))
                .andExpect(jsonPath("$.order.totalProdutos").value(200.0))
                .andExpect(jsonPath("$.order.totalFrete").value(25.0))
                .andExpect(jsonPath("$.order.totalPedido").value(225.0));

        List<Order> orders = orderRepository.findAll();
        assertEquals(1, orders.size());
        assertEquals(225.0, orders.get(0).getTotalPedido().doubleValue());
        assertEquals("fluxo@test.com", orders.get(0).getUsuario().getEmail());
    }
}