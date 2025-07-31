package com.ckweb.rest_api.model;

import java.util.List;

import com.ckweb.rest_api.model.enumeration.CouponType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cupom")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String codigo;

    @Enumerated(EnumType.STRING)
    private CouponType tipo;

    private Double valor;

    private String dataValidade;

    private Integer usoMaximo;

    private Integer usoMaximoIndividual;

    private Boolean ativo;

    @OneToMany(mappedBy = "cupom")
    private List<User_Coupon> userCoupon;

    @OneToMany(mappedBy = "cupom")
    private List<Order> pedidos;

}
