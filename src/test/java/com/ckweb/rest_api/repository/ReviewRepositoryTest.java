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

@DataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;

    private Product product1;
    private Product product2;
    private User user;

    @BeforeEach
    void setUp() {
        
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

        Review review1 = Review.builder().produto(product1).usuario(user).nota(5).build();
        Review review2 = Review.builder().produto(product1).usuario(user).nota(4).build();
        
        Review review3 = Review.builder().produto(product2).usuario(user).nota(3).build();

        reviewRepository.saveAll(List.of(review1, review2, review3));
    }

    @Test
    void deveEncontrarAvaliacoesPorIdDoProduto() {
        List<Review> reviews = reviewRepository.findAllByProdutoId(product1.getId());

        assertNotNull(reviews);
        assertEquals(2, reviews.size());
        assertEquals(product1.getId(), reviews.get(0).getProduto().getId());
        assertEquals(product1.getId(), reviews.get(1).getProduto().getId());
    }

    @Test
    void deveRetornarListaVaziaSeProdutoNaoTemAvaliacoes() {
        Product product3 = Product.builder().nome("Produto 3").preco(30.0).build();
        productRepository.save(product3);

        List<Review> reviews = reviewRepository.findAllByProdutoId(product3.getId());

        assertNotNull(reviews);
        assertTrue(reviews.isEmpty());
    }
}