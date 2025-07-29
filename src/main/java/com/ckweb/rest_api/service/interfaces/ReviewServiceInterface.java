package com.ckweb.rest_api.service.interfaces;

import java.util.List;

import com.ckweb.rest_api.dto.review.ReviewPostRequestDTO;
import com.ckweb.rest_api.dto.review.ReviewResponseDTO;

public interface ReviewServiceInterface {

    public List<ReviewResponseDTO> findAllById(Long id);
    public ReviewResponseDTO save(String token, ReviewPostRequestDTO request);
    
}
