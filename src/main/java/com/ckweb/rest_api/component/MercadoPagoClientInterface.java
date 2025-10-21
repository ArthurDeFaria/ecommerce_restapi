package com.ckweb.rest_api.component;

import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceRequestDTO;
import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceResponseDTO;
import com.ckweb.rest_api.dto.mecadopago.PaymentStatusInfo;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

public interface MercadoPagoClientInterface {

    void init();

    CreatePreferenceResponseDTO createPreference(CreatePreferenceRequestDTO request, String orderId);

    PaymentStatusInfo getPaymentStatus(Long id) throws MPApiException, MPException;
}