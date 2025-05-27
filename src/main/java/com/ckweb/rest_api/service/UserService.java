package com.ckweb.rest_api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ckweb.rest_api.dto.UserAutoUpdateDTO;
import com.ckweb.rest_api.dto.UserGetDTO;
import com.ckweb.rest_api.dto.UserUpdateDTO;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<UserGetDTO> findAll() {
    return userRepository.findAll().stream()
            .map(user -> new UserGetDTO(
                    user.getId(),
                    user.getNome(),
                    user.getEmail(),
                    user.getCpf(),
                    user.getDataNascimento().toString(),
                    user.getTelefone()
            ))
            .toList();
    }

    public UserGetDTO findById(Long id) {
    return userRepository.findById(id)
            .map(user -> new UserGetDTO(
                user.getId(),
                user.getNome(),
                user.getEmail(),
                user.getCpf(),
                user.getDataNascimento().toString(),
                user.getTelefone()
            ))
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }


    public UserGetDTO getUsuarioLogadoInfo(String token) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);
        User user = userRepository.findByEmail(email);
        return new UserGetDTO(user.getId(), user.getNome(), user.getEmail(), user.getCpf(), user.getDataNascimento(), user.getTelefone());
    }
    
    public User save(User user) {
        return userRepository.save(user);
    }

    public UserGetDTO update(UserUpdateDTO dto) {
        User user = userRepository.findById(dto.id())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        user.setNome(dto.nome());
        if (userRepository.existsByEmailAndIdNot(dto.email(), dto.id())) {
            throw new IllegalArgumentException("Email já em uso");
        }
        user.setEmail(dto.email());
        if (userRepository.existsByCpfAndIdNot(dto.cpf(), dto.id())) {
            throw new IllegalArgumentException("CPF já em uso");
        }
        user.setCpf(dto.cpf());
        if (userRepository.existsByTelefoneAndIdNot(dto.telefone(), dto.id())) {
            throw new IllegalArgumentException("Telefone já em uso");
        }
        user.setTelefone(dto.telefone());

        if (dto.senha() != null && !dto.senha().isBlank()) {
            if (!passwordEncoder.matches(dto.senha(), user.getSenha())) {
                user.setSenha(passwordEncoder.encode(dto.senha()));
            }
        }        

        User updatedUser = userRepository.save(user);

        return new UserGetDTO(
            updatedUser.getId(),
            updatedUser.getNome(),
            updatedUser.getEmail(),
            updatedUser.getCpf(),
            updatedUser.getDataNascimento(),
            updatedUser.getTelefone()
        );
    }

    public UserGetDTO updateUsuarioLogadoInfo(String token, UserAutoUpdateDTO dto) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);
        User user = userRepository.findByEmail(email);

        user.setNome(dto.nome());
        if (userRepository.existsByEmailAndIdNot(dto.email(), user.getId())) {
            throw new IllegalArgumentException("Email já em uso");
        }
        user.setEmail(dto.email());
        if (userRepository.existsByCpfAndIdNot(dto.cpf(), user.getId())) {
            throw new IllegalArgumentException("CPF já em uso");
        }
        user.setCpf(dto.cpf());
        if (userRepository.existsByTelefoneAndIdNot(dto.telefone(), user.getId())) {
            throw new IllegalArgumentException("Telefone já em uso");
        }
        user.setTelefone(dto.telefone());

        if (dto.senha() != null && !dto.senha().isBlank()) {
            if (!passwordEncoder.matches(dto.senha(), user.getSenha())) {
                user.setSenha(passwordEncoder.encode(dto.senha()));
            }
        }        

        User updatedUser = userRepository.save(user);

        return new UserGetDTO(
            updatedUser.getId(),
            updatedUser.getNome(),
            updatedUser.getEmail(),
            updatedUser.getCpf(),
            updatedUser.getDataNascimento(), // supondo que seja String ou você pode converter aqui
            updatedUser.getTelefone()
        );
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    public void deleteUsuarioLogadoInfo(String token) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("Usuário não encontrado");
        }
        userRepository.delete(user);
    }
}
