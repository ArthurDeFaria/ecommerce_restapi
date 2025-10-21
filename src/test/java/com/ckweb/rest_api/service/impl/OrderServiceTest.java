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
    private JwtService jwtService; // O JwtService real para gerar um token de teste

    @MockBean
    private MercadoPagoClientInterface mercadoPagoClient;

    private String userToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        // 1. Cria um utilizador e um produto
        testUser = User.builder().email("order@test.com").senha("123456").cargo(Cargo.USER).build();
        userRepository.save(testUser);

        Product testProduct = Product.builder().nome("Produto").preco(100.0).build();
        productRepository.save(testProduct);

        // 2. Cria um carrinho para o utilizador com 1 item
        Cart cart = Cart.builder().usuario(testUser).build();
        CartItem item = CartItem.builder().carrinho(cart).produto(testProduct).quantidade(1).build();
        cart.getItens().add(item);
        cartRepository.save(cart);
        testUser.setCarrinho(cart);

        // 3. Gera um token JWT real para este utilizador
        userToken = "Bearer " + jwtService.generateToken(testUser);
    }

    @Test
    void deveFazerRollbackDoPedidoSeMercadoPagoFalhar() {
        // 1. Given (Arrange)
        // Simula a falha da API externa
        when(mercadoPagoClient.createPreference(any(), anyString()))
                .thenThrow(new RuntimeException("API do Mercado Pago fora do ar"));

        OrderPostRequestDTO request = new OrderPostRequestDTO(BigDecimal.ZERO, null);

        // 2. When (Act) & 3. Then (Assert)
        // Verifica se a exceção correta é lançada
        assertThrows(RuntimeException.class, () -> {
            orderService.createOrderAndPaymentPreference(userToken, request);
        });

        // A asserção MAIS IMPORTANTE: Verifica se NENHUM pedido foi salvo
        assertEquals(0, orderRepository.count(), "Nenhum pedido devia ter sido salvo (ROLLBACK)");
    }

    @Test
    void naoDeveLimparCarrinhoAoCriarPreferenciaDePagamento() {
        // 1. Given (Arrange)
        // Simula o sucesso da API externa
        CreatePreferenceResponseDTO mockResponse = new CreatePreferenceResponseDTO("pref_123", "http://mp.com");
        when(mercadoPagoClient.createPreference(any(), anyString())).thenReturn(mockResponse);

        OrderPostRequestDTO request = new OrderPostRequestDTO(BigDecimal.ZERO, null);

        // 2. When (Act)
        orderService.createOrderAndPaymentPreference(userToken, request);

        // 3. Then (Assert)
        // Verifica se o pedido foi salvo
        assertEquals(1, orderRepository.count(), "Um pedido devia ter sido salvo");

        // A asserção MAIS IMPORTANTE: Verifica se o carrinho AINDA está cheio
        Cart cartPosPedido = cartRepository.findByUsuario(testUser);
        assertFalse(cartPosPedido.getItens().isEmpty(), "O carrinho NÃO devia ter sido limpo");
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