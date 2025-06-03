package com.ckweb.rest_api.dto.address;

public record AddressPostDTO(String cep, String rua, String numero, String bairro, String complemento, Long usuario_id) {
}