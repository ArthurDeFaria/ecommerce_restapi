package com.ckweb.rest_api.service.interfaces;

import org.springframework.http.ResponseEntity;

import com.ckweb.rest_api.dto.user.AuthRequest;
import com.ckweb.rest_api.dto.user.AuthResponse;
import com.ckweb.rest_api.dto.user.RegisterRequestAdm;
import com.ckweb.rest_api.dto.user.RegisterRequestUser;

public interface AuthServiceInterface {
    public AuthResponse login(AuthRequest authRequest);
    public ResponseEntity<?> register(RegisterRequestUser registerRequest);
    public ResponseEntity<?> registerAdm(RegisterRequestAdm registerRequestAdm);
}
