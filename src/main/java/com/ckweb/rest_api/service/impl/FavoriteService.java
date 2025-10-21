package com.ckweb.rest_api.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ckweb.rest_api.dto.favorite.FavoritePostRequestDTO;
import com.ckweb.rest_api.dto.favorite.FavoriteResponseDTO;
import com.ckweb.rest_api.exception.ResourceNotFoundException;
import com.ckweb.rest_api.model.Favorite;
import com.ckweb.rest_api.model.Product;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.repository.FavoriteRepository;
import com.ckweb.rest_api.repository.ProductRepository;
import com.ckweb.rest_api.repository.UserRepository;
import com.ckweb.rest_api.service.interfaces.FavoriteServiceInterface;
import com.ckweb.rest_api.service.interfaces.JwtServiceInterface;

@Service
public class FavoriteService implements FavoriteServiceInterface {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private JwtServiceInterface jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<FavoriteResponseDTO> findAllByUsuarioId(String token) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Usuário não encontrado!");
        }

        return favoriteRepository.findAllByUsuarioId(user.getId()).stream()
                .map(fav -> new FavoriteResponseDTO(
                        fav.getId(),
                        fav.getProduto().getId(),
                        fav.getProduto().getNome(),
                        fav.getProduto().getPreco(),
                        fav.getProduto().getImagem_url(),
                        fav.getData_adicionado()
                ))
                .toList();
    }

    @Override
    public FavoriteResponseDTO save(String token, FavoritePostRequestDTO request) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Usuário não encontrado!");
        }

        Product produto = productRepository.findById(request.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado!"));


        boolean jaExiste = favoriteRepository.existsByUsuarioIdAndProdutoId(user.getId(), produto.getId());
        if (jaExiste) {
            throw new IllegalArgumentException("Este produto já está nos favoritos do usuário.");
        }

        Favorite favorite = Favorite.builder()
                .usuario(user)
                .produto(produto)
                .data_adicionado(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .build();

        Favorite saved = favoriteRepository.save(favorite);

        return new FavoriteResponseDTO(
                saved.getId(),
                saved.getProduto().getId(),
                saved.getProduto().getNome(),
                saved.getProduto().getPreco(),
                saved.getProduto().getImagem_url(),
                saved.getData_adicionado()
        );
    }

    @Override
    public void delete(String token, Long id) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Usuário não encontrado!");
        }

        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Favorito não encontrado!"));

        if (!favorite.getUsuario().getId().equals(user.getId())) {
            throw new SecurityException("Você não tem permissão para excluir este favorito.");
        }

        favoriteRepository.delete(favorite);
    }
}
