package com.ckweb.rest_api.dto.user;

public record UserGetDTO(Long id, String nome, String email, String cpf, String dataNascimento, String telefone) {

}
