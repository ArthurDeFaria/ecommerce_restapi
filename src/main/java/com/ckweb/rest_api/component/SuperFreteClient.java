package com.ckweb.rest_api.component;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.ckweb.rest_api.dto.shipment.quote.QuoteRequestDTO;
import com.ckweb.rest_api.dto.shipment.quote.QuoteResponseDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SuperFreteClient {

    private final WebClient.Builder webClientBuilder;

    private static final String SUPER_FRETE_URL = "https://sandbox.superfrete.com/api/v0/calculator";

    @Value("${api.super-frete.token}")
    private String SUPER_FRETE_TOKEN;

    public List<QuoteResponseDTO> cotarFrete(QuoteRequestDTO requestDTO) {
        return webClientBuilder.build()
                .post()
                .uri(SUPER_FRETE_URL)
                .header("accept", "application/json")
                .header("User-Agent", "CKWeb REST API - Estágio de desenvolvimento (email@email.com)")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + SUPER_FRETE_TOKEN)
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToFlux(QuoteResponseDTO.class)
                .collectList()
                .block();
    }
}
