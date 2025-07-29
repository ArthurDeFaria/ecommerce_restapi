package com.ckweb.rest_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ckweb.rest_api.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    public List<Review> findAllByProdutoId(Long productId);
}
