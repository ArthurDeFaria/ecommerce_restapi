package com.ckweb.rest_api.repository;

import com.ckweb.rest_api.model.Product;
import com.ckweb.rest_api.model.Review;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.model.enumeration.Cargo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// @DataJpaTest configura um ambiente de teste focado apenas na camada JPA.
// Ele usa a base de dados H2 (definida no application.properties de teste)
// e faz rollback das transações após cada teste.
@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    // Injetamos os repositórios necessários para criar os dados de teste
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;

    private Product product1;
    private Product product2;
    private User user;

    @BeforeEach
    void setUp() {
        // 1. Given (Arrange)
        // Precisamos criar as entidades das quais Review depende
        
        user = User.builder()
                .nome("Test User")
                .email("test@user.com")
                .senha("123")
                .cpf("12345678900")
                .telefone("9999999")
                .cargo(Cargo.USER)
                .build();
        userRepository.save(user);

        product1 = Product.builder().nome("Produto 1").preco(10.0).build();
        product2 = Product.builder().nome("Produto 2").preco(20.0).build();
        productRepository.save(product1);
        productRepository.save(product2);

        // Cria 2 avaliações para o produto 1
        Review review1 = Review.builder().produto(product1).usuario(user).nota(5).build();
        Review review2 = Review.builder().produto(product1).usuario(user).nota(4).build();
        
        // Cria 1 avaliação para o produto 2
        Review review3 = Review.builder().produto(product2).usuario(user).nota(3).build();

        reviewRepository.saveAll(List.of(review1, review2, review3));
    }

    @Test
    void deveEncontrarAvaliacoesPorIdDoProduto() {
        // 2. When (Act)
        // Executa a query que queremos testar
        List<Review> reviews = reviewRepository.findAllByProdutoId(product1.getId());

        // 3. Then (Assert)
        assertNotNull(reviews);
        // Esperamos encontrar exatamente 2 avaliações
        assertEquals(2, reviews.size());
        // Confirma que as avaliações são do produto 1
        assertEquals(product1.getId(), reviews.get(0).getProduto().getId());
        assertEquals(product1.getId(), reviews.get(1).getProduto().getId());
    }

    @Test
    void deveRetornarListaVaziaSeProdutoNaoTemAvaliacoes() {
        // 1. Given (Arrange)
        Product product3 = Product.builder().nome("Produto 3").preco(30.0).build();
        productRepository.save(product3);

        // 2. When (Act)
        List<Review> reviews = reviewRepository.findAllByProdutoId(product3.getId());

        // 3. Then (Assert)
        assertNotNull(reviews);
        assertTrue(reviews.isEmpty());
    }
}