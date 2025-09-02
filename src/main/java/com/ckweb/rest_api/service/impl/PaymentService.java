package com.ckweb.rest_api.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ckweb.rest_api.component.MercadoPagoClient;
import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceRequestDTO;
import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceResponseDTO;
import com.ckweb.rest_api.dto.mecadopago.ProcessWebhookResponseDTO;
import com.mercadopago.resources.payment.Payment;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private MercadoPagoClient mercadoPagoClient;
    
    public CreatePreferenceResponseDTO createPreference(CreatePreferenceRequestDTO request){

        log.info("Criando preferência com os dados: {}", request);

        // validar a request
        // validar se o usuario existe
        // validar se os produtos existem
        // validar se os preços estão corretos

        String orderId = "123123123123"; // id da ordem criada no sistema

        try {
            CreatePreferenceResponseDTO response = mercadoPagoClient.createPreference(request, orderId);
            return response;
        } catch (Exception e) {
            log.error("Erro ao criar preferência no Mercado Pago", e);
            return null;
        }
    }

    public ProcessWebhookResponseDTO processWebhook(Long id, String type) {
        
        try {
            Payment payment = mercadoPagoClient.getPaymentStatus(id);
            //update order status based on payment status
            log.info("Pagamento recebido: {}", payment);
            return new ProcessWebhookResponseDTO(true, payment.getStatus());
        } catch (Exception e) {
            log.error("Erro ao processar webhook do Mercado Pago", e);
            return new ProcessWebhookResponseDTO(false, "error");
        }
    }

}
