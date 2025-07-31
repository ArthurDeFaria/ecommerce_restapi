package com.ckweb.rest_api.model;

import com.ckweb.rest_api.model.enumeration.PaymentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pagamento")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Double valor;

    private String formaPagamento;

    @Enumerated(EnumType.STRING)
    private PaymentStatus statusPagamento;

    private String dataPagamento;

    // private String codTransacao;
    
    @OneToOne(mappedBy = "pagamento")
    private Order pedido;

}
