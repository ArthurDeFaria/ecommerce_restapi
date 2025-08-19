package com.ckweb.rest_api.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ckweb.rest_api.dto.order.OrderPostRequestDTO;
import com.ckweb.rest_api.dto.order.OrderResponseDTO;
import com.ckweb.rest_api.dto.orderitem.OrderItemResponseDTO;
import com.ckweb.rest_api.dto.payment.PaymentResonseDTO;
import com.ckweb.rest_api.dto.shipment.ShipmentResponseDTO;
import com.ckweb.rest_api.model.Cart;
import com.ckweb.rest_api.model.Coupon;
import com.ckweb.rest_api.model.Order;
import com.ckweb.rest_api.model.OrderItem;
import com.ckweb.rest_api.model.Payment;
import com.ckweb.rest_api.model.Product;
import com.ckweb.rest_api.model.Shipment;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.model.enumeration.PaymentStatus;
import com.ckweb.rest_api.model.enumeration.ShipmentStatus;
import com.ckweb.rest_api.repository.*;
import com.ckweb.rest_api.service.JwtService;
import com.ckweb.rest_api.service.interfaces.OrderServiceInterface;

@Service
public class OrderService implements OrderServiceInterface {

        @Autowired
        private OrderRepository orderRepository;
        
        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private AddressRepository addressRepository;

        @Autowired
        private CartRepository cartRepository;

        @Autowired
        private JwtService jwtService;

        @Autowired
        private CouponRepository couponRepository;

        @Autowired
        private CartService cartService;

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

        @Override
        public OrderResponseDTO save(String token, OrderPostRequestDTO request) {
                String cleanedToken = token.replace("Bearer ", "");
                String email = jwtService.extractEmailFromToken(cleanedToken);
                User user = userRepository.findByEmail(email);

                Cart cart = cartRepository.findByUsuario(user);
                if (cart == null || cart.getItens().isEmpty()) {
                        throw new IllegalStateException("Não é possível criar um pedido com o carrinho vazio.");
                }

                Coupon cupom = null;
                if (request.couponId() != null) {
                        cupom = couponRepository.findById(request.couponId())
                                .orElseThrow(() -> new RuntimeException("Cupom não encontrado: " + request.couponId()));
                }

                BigDecimal totalProdutos = cart.getItens().stream()
                        .map(item -> {
                                Product produto = productRepository.findById(item.getProduto().getId())
                                        .orElseThrow(() -> new RuntimeException(
                                                "Produto não encontrado: " + item.getProduto().getId()));

                                return BigDecimal.valueOf(produto.getPreco()) // converte Double → BigDecimal
                                        .multiply(BigDecimal.valueOf(item.getQuantidade()));
                        })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalFrete = request.totalFrete() != null ? request.totalFrete() : BigDecimal.ZERO;
                BigDecimal totalPedido = totalProdutos.add(totalFrete);

                Payment pagamento = Payment.builder()
                        .valor(totalPedido.doubleValue())
                        .parcelas(null)
                        .formaPagamento(null)
                        .statusPagamento(PaymentStatus.PENDING)
                        .dataPagamento(null)
                        .build();

                Shipment envio = Shipment.builder()
                        .transportadora(null)
                        .tipoEnvio(null)
                        .statusEnvio(ShipmentStatus.WAITING_PAYMENT)
                        .codRastreamento(null)
                        .dataEnvio(null)
                        .dataEntregaPrevista(null)
                        .dataEntregaRealizada(null)
                        .endereco(null) // depois pode preencher com endereço da request
                        .build();

                Order pedido = Order.builder()
                        .dataPedido(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        .totalProdutos(totalProdutos)
                        .totalFrete(totalFrete)
                        .totalPedido(totalPedido)
                        .usuario(user)
                        .cupom(cupom)
                        .pagamento(pagamento)
                        .envio(envio)
                        .build();

                List<OrderItem> itens = cart.getItens().stream()
                        .map(item -> {
                                Product produto = productRepository.findById(item.getProduto().getId())
                                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + item.getProduto().getId()));

                                return OrderItem.builder()
                                        .pedido(pedido)
                                        .produto(produto)
                                        .precoUnitario(produto.getPreco().doubleValue())
                                        .quantidade(item.getQuantidade())
                                        .build();
                        })
                        .toList();

                pedido.setItensPedido(itens);

                Order savedOrder = orderRepository.save(pedido);

                cartService.clearCart(token);

                return convertToDTO(savedOrder);
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
