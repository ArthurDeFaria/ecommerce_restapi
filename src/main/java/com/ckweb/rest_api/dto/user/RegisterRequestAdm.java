package com.ckweb.rest_api.dto.user;

import com.ckweb.rest_api.model.enumeration.Cargo;

public record RegisterRequestAdm(String nome, String email, String senha, String cpf, String dataNascimento, String telefone, Cargo cargo) {

}
