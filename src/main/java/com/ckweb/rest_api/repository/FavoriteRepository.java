package com.ckweb.rest_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ckweb.rest_api.model.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    public List<Favorite> findAllByUsuarioId(Long id);
    public boolean existsByUsuarioIdAndProdutoId(Long usuarioId, Long produtoId);
}
