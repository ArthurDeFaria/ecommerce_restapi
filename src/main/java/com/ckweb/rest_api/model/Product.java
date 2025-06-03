package com.ckweb.rest_api.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "produto")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String nome;

    private String categoria;

    private String descricao;

    private double preco;

    private int quantidade_estoque;

    private String imagem_url;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<Review> avaliacoes;

    @OneToOne(mappedBy = "produto", cascade = CascadeType.ALL)
    private CartItem item_carrinho;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<Favorite> favoritos;

    @OneToOne(mappedBy = "produto", cascade = CascadeType.ALL)
    private OrderItem item_pedido;

}
