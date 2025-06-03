package com.ckweb.rest_api.dto.address;

public record AddressPutDTO(Long id, String cep, String rua, String numero, String bairro, String complemento) {
}
