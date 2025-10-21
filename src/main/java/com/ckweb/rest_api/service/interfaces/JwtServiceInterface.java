package com.ckweb.rest_api.service.interfaces;

import com.ckweb.rest_api.model.User;

public interface JwtServiceInterface {
    public String generateToken(User user);
    public String validateToken(String token);
    public String extractEmailFromToken(String token);
}