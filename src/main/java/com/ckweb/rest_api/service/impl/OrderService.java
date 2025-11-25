package com.ckweb.rest_api.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ckweb.rest_api.component.MercadoPagoClientInterface;
import com.ckweb.rest_api.dto.coupon.ValidateCouponResponseDTO;
import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceRequestDTO;
import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceRequestDTO.BackUrlsDTO;
import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceRequestDTO.ItemDTO;
import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceRequestDTO.PayerDTO;
import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceResponseDTO;
import com.ckweb.rest_api.dto.order.FinalizeOrderResponseDTO;
import com.ckweb.rest_api.dto.order.OrderPostRequestDTO;
import com.ckweb.rest_api.dto.order.OrderResponseDTO;
import com.ckweb.rest_api.dto.orderitem.OrderItemResponseDTO;
import com.ckweb.rest_api.dto.payment.PaymentResonseDTO;
import com.ckweb.rest_api.dto.shipment.ShipmentResponseDTO;
import com.ckweb.rest_api.exception.ResourceNotFoundException;
import com.ckweb.rest_api.model.Cart;
import com.ckweb.rest_api.model.Coupon;
// import com.ckweb.rest_api.model.Coupon;
import com.ckweb.rest_api.model.Order;
import com.ckweb.rest_api.model.OrderItem;
import com.ckweb.rest_api.model.Payment;
// import com.ckweb.rest_api.model.Product;
import com.ckweb.rest_api.model.Shipment;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.model.enumeration.PaymentStatus;
import com.ckweb.rest_api.model.enumeration.ShipmentStatus;
import com.ckweb.rest_api.repository.*;
import com.ckweb.rest_api.service.interfaces.CouponServiceInterface;
import com.ckweb.rest_api.service.interfaces.JwtServiceInterface;
import com.ckweb.rest_api.service.interfaces.OrderServiceInterface;

import jakarta.transaction.Transactional;


@Service
public class OrderService implements OrderServiceInterface {

        @Autowired
        private OrderRepository orderRepository;
        
        @Autowired
        private UserRepository userRepository;

        // @Autowired
        // private ProductRepository productRepository;

        @Autowired
        private CartRepository cartRepository;

        @Autowired
        private JwtServiceInterface jwtService;

        @Autowired
        private CouponRepository couponRepository;

        @Autowired
        private CouponServiceInterface couponService;

        // @Autowired
        // private CartService cartService;

        @Autowired
        private MercadoPagoClientInterface mercadoPagoClient;

        @Override
        public List<OrderResponseDTO> findAll() {
                return orderRepository.findAll()
                                .stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());
        }

        @Override
        public OrderResponseDTO findOrderById(Long id) {
                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));
                return convertToDTO(order);
        }

        @Override
        public List<OrderResponseDTO> findOrdersByUserId(Long userId) {
                return orderRepository.findByUsuarioId(userId)
                                .stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());
        }

        @Transactional
        public FinalizeOrderResponseDTO createOrderAndPaymentPreference(String token, OrderPostRequestDTO request) {
                String cleanedToken = token.replace("Bearer ", "");
                String email = jwtService.extractEmailFromToken(cleanedToken);
                User user = userRepository.findByEmail(email);
                if (user == null) {
                    throw new ResourceNotFoundException("Usuário não encontrado para o token fornecido.");
                }

                Cart cart = cartRepository.findByUsuario(user);

                if (cart == null || cart.getItens() == null || cart.getItens().isEmpty()) {
                // ---------------------
                    throw new IllegalStateException("Não é possível criar um pedido com o carrinho vazio.");
                }

                Coupon couponAplicado = null;
                BigDecimal valorDesconto = BigDecimal.ZERO;

                if (request.codigoCupom() != null && !request.codigoCupom().isBlank()) {
                ValidateCouponResponseDTO validacao = couponService.validateCoupon(
                        request.codigoCupom(), user.getId(), cart.getItens()
                );
                if (!validacao.valido()) {
                        throw new IllegalArgumentException("Cupom inválido: " + validacao.mensagem());
                }
                valorDesconto = validacao.valorDesconto();
                couponAplicado = couponRepository.findById(validacao.couponId()).orElse(null); // Busca o cupom
                }

                Order pedido = buildInitialOrder(request, user, cart, couponAplicado, valorDesconto);

                Order savedOrder = orderRepository.save(pedido);

                CreatePreferenceRequestDTO preferenceRequest = buildPreferenceRequest(savedOrder, user);

                CreatePreferenceResponseDTO paymentPreference = mercadoPagoClient.createPreference(
                preferenceRequest, 
                savedOrder.getId().toString() 
                );

                if (paymentPreference == null) {
                throw new RuntimeException("Falha ao criar preferência de pagamento no Mercado Pago.");
                }

                OrderResponseDTO orderResponse = convertToDTO(savedOrder);
                return new FinalizeOrderResponseDTO(orderResponse, paymentPreference.redirectUrl());
        }
        
        private Order buildInitialOrder(OrderPostRequestDTO request, User user, Cart cart, Coupon coupon, BigDecimal desconto) { // Novos parâmetros
                BigDecimal totalProdutos = cart.getItens().stream()
                        .map(item -> BigDecimal.valueOf(item.getProduto().getPreco())
                                .multiply(BigDecimal.valueOf(item.getQuantidade())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalFrete = request.totalFrete() != null ? request.totalFrete() : BigDecimal.ZERO;
                BigDecimal subTotalComDesconto = totalProdutos.subtract(desconto);
                BigDecimal totalPedido = subTotalComDesconto.add(totalFrete);

                Payment pagamento = Payment.builder()
                        .valor(totalPedido.doubleValue())
                        .statusPagamento(PaymentStatus.PENDING)
                        .build();

                Shipment envio = Shipment.builder()
                        .statusEnvio(ShipmentStatus.WAITING_PAYMENT)
                        .endereco(null)
                        .build();

                Order pedido = Order.builder()
                        .dataPedido(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .totalProdutos(totalProdutos)
                        .totalFrete(totalFrete)
                        .totalPedido(totalPedido)
                        .usuario(user)
                        .cupom(coupon)
                        .pagamento(pagamento)
                        .envio(envio)
                        .build();
                
                List<OrderItem> itens = cart.getItens().stream()
                .map(item -> OrderItem.builder()
                        .pedido(pedido)
                        .produto(item.getProduto())
                        .precoUnitario(item.getProduto().getPreco().doubleValue())
                        .quantidade(item.getQuantidade())
                        .build())
                .toList();

                pedido.setItensPedido(itens);
                return pedido;
        }
        
        private CreatePreferenceRequestDTO buildPreferenceRequest(Order order, User user) {
                List<ItemDTO> items = new ArrayList<>();
                order.getItensPedido().forEach(item -> {
                ItemDTO itemDTO = new ItemDTO(
                        item.getProduto().getId().toString(),
                        item.getProduto().getNome(),
                        item.getQuantidade(),
                        BigDecimal.valueOf(item.getPrecoUnitario())
                );
                items.add(itemDTO);
                });

                if (order.getTotalFrete().compareTo(BigDecimal.ZERO) > 0) {
                items.add(new ItemDTO("frete", "Custo de Envio", 1, order.getTotalFrete()));
                }

                PayerDTO payer = new PayerDTO(user.getNome(), user.getEmail());

                BackUrlsDTO backUrls = new BackUrlsDTO(
                "https://www.seusite.com/pagamento/sucesso",
                "https://www.seusite.com/pagamento/falha",
                "https://www.seusite.com/pagamento/pendente"
                );

                CreatePreferenceRequestDTO preferenceRequest = CreatePreferenceRequestDTO.builder()
                        .items(items)
                        .payer(payer)
                        .backUrls(backUrls)
                        .build();
                return preferenceRequest;
        }
        
        @Override
        public OrderResponseDTO save(String token, OrderPostRequestDTO request) {
                throw new UnsupportedOperationException("Use o método createOrderAndPaymentPreference para finalizar um pedido.");
        }

        private OrderResponseDTO convertToDTO(Order order) {
                PaymentResonseDTO paymentDTO = null;
                if (order.getPagamento() != null) {
                        paymentDTO = new PaymentResonseDTO(
                                        order.getPagamento().getId(),
                                        order.getPagamento().getValor(),
                                        order.getPagamento().getParcelas(),
                                        order.getPagamento().getFormaPagamento(),
                                        order.getPagamento().getStatusPagamento().name(),
                                        order.getPagamento().getDataPagamento(),
                                        null,
                                        null);
                }

                ShipmentResponseDTO shipmentDTO = null;
                if (order.getEnvio() != null) {
                        shipmentDTO = new ShipmentResponseDTO(
                                        order.getEnvio().getId(),
                                        order.getEnvio().getTransportadora(),
                                        order.getEnvio().getTipoEnvio(),
                                        order.getEnvio().getStatusEnvio(),
                                        order.getEnvio().getCodRastreamento(),
                                        order.getEnvio().getDataEnvio(),
                                        order.getEnvio().getDataEntregaPrevista(),
                                        order.getEnvio().getDataEntregaRealizada(),
                                        order.getEnvio().getEndereco());
                }

                List<OrderItemResponseDTO> itensDTO = order.getItensPedido().stream()
                                .map(item -> new OrderItemResponseDTO(
                                                item.getId(),
                                                item.getProduto().getId(),
                                                item.getProduto().getNome(),
                                                item.getQuantidade(),
                                                item.getPrecoUnitario()))
                                .collect(Collectors.toList());

                return new OrderResponseDTO(
                                order.getId(),
                                order.getDataPedido(),
                                order.getTotalProdutos(),
                                order.getTotalFrete(),
                                order.getTotalPedido(),
                                order.getUsuario().getId(),
                                order.getCupom() != null ? order.getCupom().getId() : null,
                                paymentDTO,
                                shipmentDTO,
                                itensDTO);
        }
}
