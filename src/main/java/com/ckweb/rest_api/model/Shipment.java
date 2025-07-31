package com.ckweb.rest_api.model;

import com.ckweb.rest_api.model.enumeration.ShipmentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Entity
@Table(name = "envio")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String transportadora;

    private String tipoEnvio;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus statusEnvio;

    private String codRastreamento;

    private String dataEnvio;

    private String dataEntregaPrevista;

    private String dataEntregaRealizada;

    @OneToOne(mappedBy = "envio")
    private Order pedido;

    @ManyToOne
    @JoinColumn(name = "endereco_id")
    private Address endereco;

}
