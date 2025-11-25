package com.ckweb.rest_api.service.impl;

import com.ckweb.rest_api.model.OrderItem;
import com.ckweb.rest_api.model.Product;
import com.ckweb.rest_api.model.enumeration.PaymentStatus;
import com.ckweb.rest_api.model.enumeration.ShipmentStatus;
import com.ckweb.rest_api.repository.ProductRepository;
import com.ckweb.rest_api.service.interfaces.CartServiceInterface;
import com.ckweb.rest_api.service.interfaces.CouponServiceInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ckweb.rest_api.component.MercadoPagoClientInterface;
import com.ckweb.rest_api.dto.mecadopago.PaymentStatusInfo;
import com.ckweb.rest_api.dto.mecadopago.ProcessWebhookResponseDTO;
import com.ckweb.rest_api.model.Order;
import com.ckweb.rest_api.repository.OrderRepository;
import com.ckweb.rest_api.service.interfaces.PaymentServiceInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PaymentService implements PaymentServiceInterface {

    @Autowired
    private MercadoPagoClientInterface mercadoPagoClient;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartServiceInterface cartService;

    @Autowired
    private CouponServiceInterface couponService;

    @Override
    @Transactional
    public ProcessWebhookResponseDTO processWebhook(Long paymentId, String type) {
        try {
            PaymentStatusInfo paymentInfo = mercadoPagoClient.getPaymentStatus(paymentId);
            log.info("Webhook recebido para o pagamento: {}", paymentId);

            String orderIdStr = paymentInfo.externalReference();
            if (orderIdStr == null) {
                 log.error("Webhook para pagamento {} sem external_reference.", paymentId);
                 throw new IllegalArgumentException("External reference não encontrada.");
            }
            Long orderId = Long.parseLong(orderIdStr);
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido com ID " + orderId + " não encontrado."));

            String mpStatus = paymentInfo.status();
            com.ckweb.rest_api.model.Payment pedidoPagamento = order.getPagamento();

            switch (mpStatus) {
                case "approved":
                    pedidoPagamento.setStatusPagamento(PaymentStatus.APPROVED);
                    order.getEnvio().setStatusEnvio(ShipmentStatus.WAITING_PICKUP);

                    log.info("Abatendo stock para o pedido {}", orderId);
                    for (OrderItem item : order.getItensPedido()) {
                        Product produto = item.getProduto();
                        int quantidadeComprada = item.getQuantidade();
                        int novoStock = Math.max(0, produto.getQuantidadeEstoque() - quantidadeComprada);
                        if (produto.getQuantidadeEstoque() < quantidadeComprada) {
                            log.error("STOCK INCONSISTENTE! Pedido {} aprovado, mas produto {} (ID: {}) não tem stock suficiente (disponível: {}, comprado: {}).",
                                     orderId, produto.getNome(), produto.getId(), produto.getQuantidadeEstoque(), quantidadeComprada);
                        }
                        produto.setQuantidadeEstoque(novoStock);
                        productRepository.save(produto);
                    }

                    try {
                        cartService.clearCartByUser(order.getUsuario());
                        log.info("Carrinho do usuário {} limpo com sucesso.", order.getUsuario().getEmail());
                    } catch (Exception e) {
                        log.error("Falha ao tentar limpar o carrinho do usuário {}: {}", order.getUsuario().getEmail(), e.getMessage());
                    }

                    try {
                       couponService.registerCouponUsage(order);
                       log.info("Uso do cupom registrado para o pedido {}", order.getId());
                    } catch (Exception e) {
                       log.error("Falha ao registrar uso do cupom para o pedido {}: {}", order.getId(), e.getMessage());
                       // Não falhar o webhook por causa disso, apenas logar.
                    }
                    break;

                case "rejected":
                    pedidoPagamento.setStatusPagamento(PaymentStatus.REJECTED);
                    order.getEnvio().setStatusEnvio(ShipmentStatus.CANCELLED);
                    break;

                case "cancelled":
                case "refunded":
                case "charged_back":
                    pedidoPagamento.setStatusPagamento(PaymentStatus.CANCELLED);
                    order.getEnvio().setStatusEnvio(ShipmentStatus.CANCELLED);
                    // Futuramente: Adicionar lógica para repor stock cancelado/devolvido
                    break;

                case "in_process":
                case "pending":
                default:
                    pedidoPagamento.setStatusPagamento(PaymentStatus.PENDING);
                    break;
            }

            orderRepository.save(order);

            log.info("Pedido {} atualizado com status de pagamento: {}", orderId, pedidoPagamento.getStatusPagamento().name());
            return new ProcessWebhookResponseDTO(true, mpStatus);

        } catch (Exception e) {
             log.error("Erro ao processar webhook do Mercado Pago para o pagamento ID {}: {}", paymentId, e.getMessage());
             throw new RuntimeException("Erro ao processar webhook: " + e.getMessage(), e);
        }
    }
}