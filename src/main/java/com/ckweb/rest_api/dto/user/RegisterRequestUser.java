package com.ckweb.rest_api.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequestUser(
    @NotBlank(message = "Nome não pode ser vazio") 
    String nome, 
    
    @NotBlank(message = "Email não pode ser vazio")
    @Email(message = "Formato de email inválido")
    String email, 
    
    @NotBlank(message = "Senha não pode ser vazia")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    String senha, 
    
    @NotBlank(message = "CPF não pode ser vazio") 
    String cpf, 
    
    String dataNascimento, 
    String telefone
) {}