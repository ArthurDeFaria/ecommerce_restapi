package com.ckweb.rest_api.service.interfaces;

import com.ckweb.rest_api.dto.mecadopago.ProcessWebhookResponseDTO;

public interface PaymentServiceInterface {
    public ProcessWebhookResponseDTO processWebhook(Long paymentId, String type);
}
