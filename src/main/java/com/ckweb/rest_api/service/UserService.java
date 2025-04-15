package com.ckweb.rest_api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ckweb.rest_api.dto.UserDTO;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserDTO getUsuarioLogadoInfo(String token) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);
        User user = userRepository.findByEmail(email);
        return new UserDTO(user.getId(), user.getNome(), user.getEmail(), user.getCpf(), user.getDataNascimento(), user.getTelefone());
    }

    
    public User save(User user) {
        return userRepository.save(user);
    }
}
