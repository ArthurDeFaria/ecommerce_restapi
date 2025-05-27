package com.ckweb.rest_api.dto;

public record AddressGetDTO(Long id, String cep, String rua, String numero, String bairro, String complemento) {
}
