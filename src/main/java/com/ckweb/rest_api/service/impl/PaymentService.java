package com.ckweb.rest_api.service.impl;

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

    
    @Transactional
    public ProcessWebhookResponseDTO processWebhook(Long paymentId, String type) {

        try {
            // 1. Obtenha os detalhes DO NOSSO DTO
            PaymentStatusInfo paymentInfo = mercadoPagoClient.getPaymentStatus(paymentId); // 2. Usar o novo tipo
            log.info("Webhook recebido para o pagamento: {}", paymentId);

            // 2. Extraia a referência externa
            String orderIdStr = paymentInfo.externalReference(); // 3. Usar o campo do DTO
            if (orderIdStr == null) {
                // ... (erro)
            }
            Long orderId = Long.parseLong(orderIdStr);
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido com ID " + orderId + " não encontrado."));

            // 4. Mapeie o status do Mercado Pago
            String mpStatus = paymentInfo.status(); // 4. Usar o campo do DTO
            com.ckweb.rest_api.model.Payment pedidoPagamento = order.getPagamento();

            // --- IMPORTANTE: Remover dependências de outros campos ---
            // Se precisar de mais campos (data, forma de pagamento), adicione-os ao DTO PaymentStatusInfo
            // e mapeie-os no MercadoPagoClient.
            // Por agora, vamos remover as linhas que dependiam do objeto Payment original:
            // pedidoPagamento.setDataPagamento(paymentInfo.getDateApproved() != null ? paymentInfo.getDateApproved().toString() : null); // REMOVER
            // pedidoPagamento.setFormaPagamento(paymentInfo.getPaymentTypeId()); // REMOVER
            // --------------------------------------------------------

            switch (mpStatus) {
                case "approved":
                    // ... (lógica existente, incluindo stock e limpeza de carrinho)
                    break;
                // ... (outros cases)
            }

            // 5. Salve as alterações
            orderRepository.save(order);
            // ... (return)

        } catch (Exception e) {
            // ... (catch)
        }
        return new ProcessWebhookResponseDTO(false, "error"); // Adicione um retorno aqui para o caso de exceção
    }
}