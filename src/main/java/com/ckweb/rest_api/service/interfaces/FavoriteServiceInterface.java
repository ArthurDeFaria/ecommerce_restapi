package com.ckweb.rest_api.service.interfaces;

import java.util.List;

import com.ckweb.rest_api.dto.favorite.FavoritePostRequestDTO;
import com.ckweb.rest_api.dto.favorite.FavoriteResponseDTO;

public interface FavoriteServiceInterface {
    public List<FavoriteResponseDTO> findAllByUsuarioId(String token);
    public FavoriteResponseDTO save(String token, FavoritePostRequestDTO request);
    public void delete(String token, Long id);
}
