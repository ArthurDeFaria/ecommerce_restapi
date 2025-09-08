package com.ckweb.rest_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ckweb.rest_api.dto.mecadopago.MercadoPagoWebhookRequestDTO;
import com.ckweb.rest_api.service.impl.PaymentService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/pagamentos")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/webhooks")
    public ResponseEntity<Void> handleWebhook(@RequestBody MercadoPagoWebhookRequestDTO request) {
        String eventType = request.getType();
        if ("payment".equals(eventType)) {
            String resourceId = request.getData().getId();
            String resourceType = request.getAction();
            try {
                var result = paymentService.processWebhook(Long.parseLong(resourceId), resourceType);
                log.info("Webhook processado com sucesso: {}\n{}\n{}", resourceId, resourceType, result);
            } catch (Exception e) {
                // TODO: handle exception
            }
        } else {
            log.info("Evento '{}' recebido e ignorado.", eventType);
        }
        return ResponseEntity.ok().build();
    }

}
