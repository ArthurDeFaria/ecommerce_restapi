package com.ckweb.rest_api.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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

    private Double preco;

    private Integer quantidadeEstoque;

    private String imagem_url;

    private Double peso;

    private Double altura;

    private Double largura;

    private Double comprimento;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<Review> avaliacoes;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<Favorite> favoritos;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL)
    private List<OrderItem> item_pedido;

    public Product(String nome, String categoria, String descricao, Double preco, Integer quantidadeEstoque,
            String imagem_url, Double peso, Double altura, Double largura, Double comprimento) {
        this.nome = nome;
        this.categoria = categoria;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
        this.imagem_url = imagem_url;
        this.peso = peso;
        this.altura = altura;
        this.largura = largura;
        this.comprimento = comprimento;
    }

}
