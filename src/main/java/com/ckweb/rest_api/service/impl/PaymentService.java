package com.ckweb.rest_api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ckweb.rest_api.component.MercadoPagoClient;
import com.ckweb.rest_api.dto.mecadopago.ProcessWebhookResponseDTO;
import com.ckweb.rest_api.model.Order;
import com.ckweb.rest_api.model.enumeration.PaymentStatus;
import com.ckweb.rest_api.model.enumeration.ShipmentStatus;
import com.ckweb.rest_api.repository.OrderRepository;
import com.mercadopago.resources.payment.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private MercadoPagoClient mercadoPagoClient;

    @Autowired
    private OrderRepository orderRepository; 

    @Transactional // É uma boa prática tornar o método transacional
    public ProcessWebhookResponseDTO processWebhook(Long paymentId, String type) {
        
        try {
            // 1. Obtenha os detalhes completos do pagamento do Mercado Pago
            Payment paymentInfo = mercadoPagoClient.getPaymentStatus(paymentId);
            log.info("Webhook recebido para o pagamento: {}", paymentInfo.getId());

            // 2. Extraia a referência externa (nosso ID do pedido)
            String orderIdStr = paymentInfo.getExternalReference();
            if (orderIdStr == null) {
                log.error("Webhook para pagamento {} sem external_reference.", paymentId);
                throw new IllegalArgumentException("External reference não encontrada.");
            }
            Long orderId = Long.parseLong(orderIdStr);

            // 3. Busque o pedido no seu banco de dados
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido com ID " + orderId + " não encontrado."));

            // 4. Mapeie o status do Mercado Pago para seus Enums e atualize o pedido
            String mpStatus = paymentInfo.getStatus();
            com.ckweb.rest_api.model.Payment pedidoPagamento = order.getPagamento();

            // Lógica de atualização usando seus Enums
            switch (mpStatus) {
                case "approved":
                    pedidoPagamento.setStatusPagamento(PaymentStatus.APPROVED);
                    // Com o pagamento aprovado, o envio passa para "Aguardando retirada"
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
                    // Qualquer outro status não final do MP mantém o nosso como PENDENTE
                    pedidoPagamento.setStatusPagamento(PaymentStatus.PENDING);
                    // O status de envio não muda, continua como WAITING_PAYMENT
                    break;
            }
            
            // Preencha mais detalhes do pagamento se desejar
            pedidoPagamento.setDataPagamento(paymentInfo.getDateApproved() != null ? paymentInfo.getDateApproved().toString() : null);
            pedidoPagamento.setFormaPagamento(paymentInfo.getPaymentTypeId());
            // ...

            // 5. Salve as alterações no banco de dados
            orderRepository.save(order);

            log.info("Pedido {} atualizado com status de pagamento: {}", orderId, pedidoPagamento.getStatusPagamento().name());
            return new ProcessWebhookResponseDTO(true, mpStatus);

        } catch (Exception e) {
            log.error("Erro ao processar webhook do Mercado Pago para o pagamento ID {}: {}", paymentId, e.getMessage());
            // Em caso de erro, é importante não retornar um status 200 OK para que o Mercado Pago tente reenviar a notificação.
            // Considerar lançar a exceção para que o controller a capture e retorne um status 500.
            return new ProcessWebhookResponseDTO(false, "error");
        }
    }
}