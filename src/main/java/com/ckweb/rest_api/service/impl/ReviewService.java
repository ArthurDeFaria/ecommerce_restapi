package com.ckweb.rest_api.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ckweb.rest_api.dto.review.ReviewPostRequestDTO;
import com.ckweb.rest_api.dto.review.ReviewResponseDTO;
import com.ckweb.rest_api.exception.ResourceNotFoundException;
import com.ckweb.rest_api.model.Product;
import com.ckweb.rest_api.model.Review;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.repository.ProductRepository;
import com.ckweb.rest_api.repository.ReviewRepository;
import com.ckweb.rest_api.repository.UserRepository;
import com.ckweb.rest_api.service.interfaces.JwtServiceInterface;
import com.ckweb.rest_api.service.interfaces.ReviewServiceInterface;

@Service
public class ReviewService implements ReviewServiceInterface {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtServiceInterface jwtService;

    @Override
    public List<ReviewResponseDTO> findAllById(Long productId) {
        return reviewRepository.findAllByProdutoId(productId)
                .stream()
                .map(review -> new ReviewResponseDTO(
                        review.getId(),
                        review.getNota(),
                        review.getComentario(),
                        review.getData(),
                        review.getUsuario().getNome()
                ))
                .toList();
    }

    @Override
    public ReviewResponseDTO save(String token, ReviewPostRequestDTO request) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);

        User usuario = userRepository.findByEmail(email);
        if (usuario == null) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        Product produto = productRepository.findById(request.produtoId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Review review = Review.builder()
                .nota(request.nota())
                .comentario(request.comentario())
                .data(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .usuario(usuario)
                .produto(produto)
                .build();

        review = reviewRepository.save(review);

        return new ReviewResponseDTO(
                review.getId(),
                review.getNota(),
                review.getComentario(),
                review.getData(),
                usuario.getNome()
        );
    }
}
