package com.ckweb.rest_api.component;

import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceRequestDTO;
import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceResponseDTO;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;

public interface MercadoPagoClientInterface {

    void init();

    CreatePreferenceResponseDTO createPreference(CreatePreferenceRequestDTO request, String orderId);

    Payment getPaymentStatus(Long id) throws MPApiException, MPException;
}