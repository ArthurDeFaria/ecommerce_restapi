package com.ckweb.rest_api.model.enumeration;

public enum CouponType {
    PERCENTAGE("Porcentagem"),
    FIXED_AMOUNT("Valor Fixo");

    private final String descricao;

    CouponType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
