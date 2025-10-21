package com.ckweb.rest_api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ckweb.rest_api.component.MercadoPagoClient;
import com.ckweb.rest_api.component.MercadoPagoClientInterface;
import com.ckweb.rest_api.dto.mecadopago.ProcessWebhookResponseDTO;
import com.ckweb.rest_api.model.Order;
import com.ckweb.rest_api.model.enumeration.PaymentStatus;
import com.ckweb.rest_api.model.enumeration.ShipmentStatus;
import com.ckweb.rest_api.repository.OrderRepository;
import com.ckweb.rest_api.service.interfaces.PaymentServiceInterface;
import com.mercadopago.resources.payment.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PaymentService implements PaymentServiceInterface {

    @Autowired
    private MercadoPagoClientInterface mercadoPagoClient;

    @Autowired
    private OrderRepository orderRepository; 

    
    @Transactional
    @Override
    public ProcessWebhookResponseDTO processWebhook(Long paymentId, String type) {
        
        try {
            Payment paymentInfo = mercadoPagoClient.getPaymentStatus(paymentId);
            log.info("Webhook recebido para o pagamento: {}", paymentInfo.getId());

            String orderIdStr = paymentInfo.getExternalReference();
            if (orderIdStr == null) {
                log.error("Webhook para pagamento {} sem external_reference.", paymentId);
                throw new IllegalArgumentException("External reference não encontrada.");
            }
            Long orderId = Long.parseLong(orderIdStr);

            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido com ID " + orderId + " não encontrado."));

            String mpStatus = paymentInfo.getStatus();
            com.ckweb.rest_api.model.Payment pedidoPagamento = order.getPagamento();

            switch (mpStatus) {
                case "approved":
                    pedidoPagamento.setStatusPagamento(PaymentStatus.APPROVED);
                    order.getEnvio().setStatusEnvio(ShipmentStatus.WAITING_PICKUP); 
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
                    break;

                case "in_process":
                case "pending":
                default:
                    pedidoPagamento.setStatusPagamento(PaymentStatus.PENDING);
                    break;
            }
            
            pedidoPagamento.setDataPagamento(paymentInfo.getDateApproved() != null ? paymentInfo.getDateApproved().toString() : null);
            pedidoPagamento.setFormaPagamento(paymentInfo.getPaymentTypeId());
            orderRepository.save(order);

            log.info("Pedido {} atualizado com status de pagamento: {}", orderId, pedidoPagamento.getStatusPagamento().name());
            return new ProcessWebhookResponseDTO(true, mpStatus);

        } catch (Exception e) {
            log.error("Erro ao processar webhook do Mercado Pago para o pagamento ID {}: {}", paymentId, e.getMessage());
            return new ProcessWebhookResponseDTO(false, "error");
        }
    }
}