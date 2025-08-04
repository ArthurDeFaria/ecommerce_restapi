package com.ckweb.rest_api.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ckweb.rest_api.component.SuperFreteClient;
import com.ckweb.rest_api.dto.shipment.QuoteRequestDTO;
import com.ckweb.rest_api.dto.shipment.QuoteResponseDTO;
import com.ckweb.rest_api.dto.shipment.quote.AddressDTO;
import com.ckweb.rest_api.dto.shipment.quote.OptionsDTO;
import com.ckweb.rest_api.dto.shipment.quote.PackageDTO;
import com.ckweb.rest_api.model.Product;
import com.ckweb.rest_api.repository.ProductRepository;
import com.ckweb.rest_api.service.interfaces.ShipmentServiceInterface;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ShipmentService implements ShipmentServiceInterface {
    
    @Autowired
    ProductRepository productRepository;
    
    @Autowired
    SuperFreteClient superFreteClient;

    @Override
    public List<QuoteResponseDTO> cotarFrete(String cep, List<Long> produtosIds) {
        List<Product> produtos = productRepository.findAllById(produtosIds);

        if (produtos.isEmpty()) {
            throw new IllegalArgumentException("Nenhum produto encontrado.");
        }

        double pesoTotal = produtos.stream()
                .mapToDouble(p -> p.getPeso())
                .sum();

        double altura = produtos.stream()
                .mapToDouble(Product::getAltura)
                .max()
                .orElse(5);

        double largura = produtos.stream()
                .mapToDouble(Product::getLargura)
                .max()
                .orElse(5);

        double comprimento = produtos.stream()
                .mapToDouble(Product::getComprimento)
                .sum();

        double valorTotal = produtos.stream()
                .map(p -> p.getPreco().doubleValue())
                .reduce(0.0, Double::sum);

        QuoteRequestDTO request = new QuoteRequestDTO(
                new AddressDTO("01153000"),
                new AddressDTO(cep),
                "1,2,17",
                new OptionsDTO(false, false, valorTotal, false),
                new PackageDTO(altura, largura, comprimento, pesoTotal)
        );

        try {
            System.out.println(new ObjectMapper().writeValueAsString(request));
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
        }

        return superFreteClient.cotarFrete(request);
    }
}
