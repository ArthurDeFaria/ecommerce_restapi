package com.ckweb.rest_api.dto;

public record RegisterRequestUser(String nome, String email, String senha, String cpf, String dataNascimento, String telefone) {

}
