package com.ckweb.rest_api.model.enumeration;

public enum ShipmentStatus {
    WAITING_PAYMENT("Aguardando pagamento"),
    WAITING_PICKUP("Aguardando retirada"),
    IN_TRANSIT("Em trânsito para o destino"),
    OUT_FOR_DELIVERY("Saiu para entrega"),
    DELIVERED("Entregue"),
    CANCELLED("Cancelado");

    private final String descricao;

    ShipmentStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
    
}
