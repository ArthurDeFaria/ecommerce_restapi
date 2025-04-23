package com.ckweb.rest_api.dto;

public record UserUpdateDTO(Long id, String nome, String email, String senha, String cpf, String telefone) {
    
}
