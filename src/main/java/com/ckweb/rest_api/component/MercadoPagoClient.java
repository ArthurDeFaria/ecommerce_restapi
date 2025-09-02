package com.ckweb.rest_api.component;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceRequestDTO;
import com.ckweb.rest_api.dto.mecadopago.CreatePreferenceResponseDTO;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MercadoPagoClient {
    @Value("${api.mercado-pago.token}")
    private String accessToken;

    @Value("${api.mercado-pago.notification-url}")
    private String notificationUrl;

    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
        log.info("Iniciando cliente do Mercado Pago");
    }

    public CreatePreferenceResponseDTO createPreference(CreatePreferenceRequestDTO request, String orderId) {
        PreferenceClient PreferenceClient = new PreferenceClient();

        List<PreferenceItemRequest> items = request.items().stream()
            .map(item -> PreferenceItemRequest.builder()
                .id(item.id())
                .title(item.title())
                .quantity(item.quantity())
                .unitPrice(item.unitPrice())
                .build())
            .collect(Collectors.toList());

        PreferencePayerRequest payer = PreferencePayerRequest.builder()
            .name(request.payer().name())
            .email(request.payer().email())
            .build();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
            .success(request.backUrls().success() != null ? request.backUrls().success() : "")
            .failure(request.backUrls().failure() != null ? request.backUrls().failure() : "")
            .pending(request.backUrls().pending() != null ? request.backUrls().pending() : "")
            .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
            .items(items)
            .payer(payer)
            .backUrls(backUrls)
            .notificationUrl(notificationUrl)
            .externalReference(orderId)
            .autoReturn("approved")
            .build();

        try {
            Preference preference = PreferenceClient.create(preferenceRequest);
            return new CreatePreferenceResponseDTO(
                preference.getId(),
                preference.getInitPoint()
            );
        } catch (MPApiException e) {
            System.err.println("Mercado Pago error: " + e.getApiResponse().getContent());
            return null;
        } catch (com.mercadopago.exceptions.MPException e) {
            log.error("Error creating MercadoPago preference", e);
            return null;
        }
    }

    public Payment getPaymentStatus(Long id) throws MPApiException, com.mercadopago.exceptions.MPException {
        com.mercadopago.client.payment.PaymentClient paymentClient = new com.mercadopago.client.payment.PaymentClient();
        return paymentClient.get(id);
    }
}