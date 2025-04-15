package com.ckweb.rest_api.dto;

import com.ckweb.rest_api.model.Cargo;

public record RegisterRequestAdm(String nome, String email, String senha, String cpf, String dataNascimento, String telefone, Cargo cargo) {

}
