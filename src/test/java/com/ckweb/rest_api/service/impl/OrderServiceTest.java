package com.ckweb.rest_api.service.impl;

import com.ckweb.rest_api.component.MercadoPagoClientInterface;
import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceResponseDTO;
import com.ckweb.rest_api.dto.order.OrderPostRequestDTO;
import com.ckweb.rest_api.model.Cart;
import com.ckweb.rest_api.model.CartItem;
import com.ckweb.rest_api.model.Product;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.model.enumeration.Cargo;
import com.ckweb.rest_api.repository.CartItemRepository;
import com.ckweb.rest_api.repository.CartRepository;
import com.ckweb.rest_api.repository.OrderRepository;
import com.ckweb.rest_api.repository.ProductRepository;
import com.ckweb.rest_api.repository.UserRepository;
import com.ckweb.rest_api.service.JwtService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private JwtService jwtService;

    @MockBean
    private MercadoPagoClientInterface mercadoPagoClient;

    private String userToken;
    private User testUser;

    private Cart testCart;

    @BeforeEach
    void setUp() {
        testUser = User.builder().email("order@test.com").senha("123456").cargo(Cargo.USER).build();
        userRepository.save(testUser);

        Product testProduct = Product.builder().nome("Produto").preco(100.0).build();
        productRepository.save(testProduct);

        testCart = Cart.builder().usuario(testUser).build();
        CartItem item = CartItem.builder().carrinho(testCart).produto(testProduct).quantidade(1).build();
        testCart.getItens().add(item);
        cartRepository.save(testCart);
        testUser.setCarrinho(testCart);

        userToken = "Bearer " + jwtService.generateToken(testUser);
    }

    @Test
    void deveFazerRollbackDoPedidoSeMercadoPagoFalhar() {
        when(mercadoPagoClient.createPreference(any(), anyString()))
                .thenThrow(new RuntimeException("API do Mercado Pago fora do ar"));

        OrderPostRequestDTO request = new OrderPostRequestDTO(BigDecimal.ZERO, null);

        assertThrows(RuntimeException.class, () -> {
            orderService.createOrderAndPaymentPreference(userToken, request);
        });

        assertEquals(0, orderRepository.count(), "Nenhum pedido devia ter sido salvo (ROLLBACK)");
    }

    @Test
    void naoDeveLimparCarrinhoAoCriarPreferenciaDePagamento() {
        CreatePreferenceResponseDTO mockResponse = new CreatePreferenceResponseDTO("pref_123", "http://mp.com");
        when(mercadoPagoClient.createPreference(any(), anyString())).thenReturn(mockResponse);
        OrderPostRequestDTO request = new OrderPostRequestDTO(BigDecimal.ZERO, null);
        Long cartIdParaVerificar = testCart.getId();

        orderService.createOrderAndPaymentPreference(userToken, request);

        assertEquals(1, orderRepository.count(), "Um pedido devia ter sido salvo");

        long itemCount = cartItemRepository.countByCarrinhoId(cartIdParaVerificar);
        assertEquals(1, itemCount, "O CartItem NÃO devia ter sido limpo");
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }
}