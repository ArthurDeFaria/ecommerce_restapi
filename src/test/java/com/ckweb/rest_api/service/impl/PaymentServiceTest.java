package com.ckweb.rest_api.service.impl;
import com.ckweb.rest_api.component.MercadoPagoClientInterface;
import com.ckweb.rest_api.dto.mecadopago.PaymentStatusInfo;
import com.ckweb.rest_api.model.*;
import com.ckweb.rest_api.model.enumeration.Cargo;
import com.ckweb.rest_api.model.enumeration.PaymentStatus;
import com.ckweb.rest_api.model.enumeration.ShipmentStatus;
import com.ckweb.rest_api.repository.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

import jakarta.persistence.EntityManager;

@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;

    @MockBean
    private MercadoPagoClientInterface mercadoPagoClient;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CartItemRepository cartItemRepository;

    private Order testOrder;
    private Product testProduct;
    private User testUser;
    private Long fakePaymentId = 12345L;

    @BeforeEach
    void setUp(){
        // 1. Cria User e Product
        testUser = User.builder().email("payment@test.com").senha("123").cargo(Cargo.USER).build();
        userRepository.save(testUser);

        testProduct = Product.builder().nome("Produto Stock")
                .preco(100.0)
                .quantidadeEstoque(50) // Stock inicial
                .build();
        productRepository.save(testProduct);

        // 2. Cria um Carrinho cheio (para testar a limpeza)
        Cart cart = Cart.builder().usuario(testUser).build();
        CartItem cartItem = CartItem.builder().carrinho(cart).produto(testProduct).quantidade(10).build();
        cart.getItens().add(cartItem);
        cartRepository.save(cart);
        testUser.setCarrinho(cart);

        // 3. Cria um Pedido (com 2 unidades do produto)
        testOrder = Order.builder()
                .usuario(testUser)
                .totalPedido(BigDecimal.valueOf(200.0))
                .pagamento(com.ckweb.rest_api.model.Payment.builder().statusPagamento(PaymentStatus.PENDING).build())
                .envio(Shipment.builder().statusEnvio(ShipmentStatus.WAITING_PAYMENT).build())
                .build();
        
        OrderItem orderItem = OrderItem.builder()
                .pedido(testOrder)
                .produto(testProduct)
                .quantidade(2) // Quantidade comprada
                .build();
        testOrder.setItensPedido(List.of(orderItem));
        orderRepository.save(testOrder);

        // 4. Simula a resposta DO NOSSO DTO
        PaymentStatusInfo mockPaymentInfo = new PaymentStatusInfo(
            "approved",
            testOrder.getId().toString()
        );

        try {
             when(mercadoPagoClient.getPaymentStatus(fakePaymentId)).thenReturn(mockPaymentInfo);
        } catch (MPApiException | MPException e) {
             fail("Erro ao configurar mock do MercadoPagoClient: " + e.getMessage());
        }
    }

    @Test
    void deveAbaterStockELimparCarrinhoQuandoPagamentoAprovado() {
        // 1. Given
        Long productId = testProduct.getId();
        Long userId = testUser.getId();

        // 2. When
        paymentService.processWebhook(fakePaymentId, "payment");

        entityManager.flush();
        entityManager.clear();

        // 3. Then
        
        Product productAposVenda = productRepository.findById(productId)
                .orElseThrow(() -> new AssertionError("Produto não encontrado após venda!"));

        assertEquals(48, productAposVenda.getQuantidadeEstoque(), "O stock devia ser 50 - 2 = 48");

        // Teste de Limpeza de Carrinho
        User userAposVenda = userRepository.findById(userId).get();
        Cart cartAposVenda = cartRepository.findByUsuario(userAposVenda);
        assertTrue(cartAposVenda == null || cartAposVenda.getItens().isEmpty(), "O carrinho do usuário devia estar vazio ou nulo");
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