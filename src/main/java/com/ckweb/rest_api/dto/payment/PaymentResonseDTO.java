package com.ckweb.rest_api.dto.payment;

public record PaymentResonseDTO(
    Long id,
    Double valor,
    Integer parcelas,
    String metodoPagamento,
    String status,
    String dataPagamento,
    String dataExpiracao,
    String descricao
) {
    
}
