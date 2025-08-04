package com.ckweb.rest_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ckweb.rest_api.dto.shipment.ShipmentQuoteRequest;
import com.ckweb.rest_api.dto.shipment.quote.QuoteResponseDTO;
import com.ckweb.rest_api.service.impl.ShipmentService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/envio")
@Tag(name = "Envio", description = "Operações relacionadas ao envio de produtos")
public class ShipmentController {
    
    @Autowired
    ShipmentService shipmentService;

    @PostMapping("/cotarfrete")
    public ResponseEntity<List<QuoteResponseDTO>> cotarFrete(@RequestBody ShipmentQuoteRequest request) {
        List<QuoteResponseDTO> response = shipmentService.cotarFrete(request.cep(), request.produtosIds());
        return ResponseEntity.ok(response);
    }

}
