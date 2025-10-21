package com.ckweb.rest_api.service.impl;

import com.ckweb.rest_api.dto.favorite.FavoritePostRequestDTO;
import com.ckweb.rest_api.dto.favorite.FavoriteResponseDTO;
import com.ckweb.rest_api.model.Favorite;
import com.ckweb.rest_api.model.Product;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.repository.FavoriteRepository;
import com.ckweb.rest_api.repository.ProductRepository;
import com.ckweb.rest_api.repository.UserRepository;
import com.ckweb.rest_api.service.interfaces.JwtServiceInterface;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// Usa a extensão do Mockito para JUnit 5
@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    // Cria mocks para todas as dependências do FavoriteService
    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private JwtServiceInterface jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    // Injesta os mocks criados na instância do serviço
    @InjectMocks
    private FavoriteService favoriteService;

    private User mockUser;
    private Product mockProduct;
    private String mockToken;

    @BeforeEach
    void setUp() {
        // Configuração padrão para os testes
        mockUser = User.builder().id(1L).email("user@test.com").nome("Test User").build();
        mockProduct = Product.builder().id(10L).nome("Test Product").preco(100.0).imagem_url("img.png").build();
        mockToken = "Bearer test_token";
    }

    @Test
    void deveLancarExcecaoAoSalvarFavoritoJaExistente() {
        // 1. Given (Arrange)
        FavoritePostRequestDTO request = new FavoritePostRequestDTO(mockProduct.getId());

        // Configura os mocks
        when(jwtService.extractEmailFromToken(anyString())).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(anyString())).thenReturn(mockUser);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(mockProduct));
        
        // Simula que o favorito JÁ EXISTE
        when(favoriteRepository.existsByUsuarioIdAndProdutoId(mockUser.getId(), mockProduct.getId())).thenReturn(true);

        // 2. When (Act) & 3. Then (Assert)
        
        // Verifica se a exceção correta é lançada
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            favoriteService.save(mockToken, request);
        });

        // Verifica a mensagem da exceção
        assertEquals("Este produto já está nos favoritos do usuário.", exception.getMessage());

        // Garante que o método save NUNCA foi chamado
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    void deveSalvarNovoFavoritoComSucesso() {
        // 1. Given (Arrange)
        FavoritePostRequestDTO request = new FavoritePostRequestDTO(mockProduct.getId());

        String dataFormatada = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        Favorite mockSavedFavorite = Favorite.builder()
                .id(1L)
                .usuario(mockUser)
                .produto(mockProduct)
                .data_adicionado(dataFormatada)
                .build();

        // Configura os mocks
        when(jwtService.extractEmailFromToken(anyString())).thenReturn(mockUser.getEmail());
        when(userRepository.findByEmail(anyString())).thenReturn(mockUser);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(mockProduct));
        
        // Simula que o favorito NÃO EXISTE
        when(favoriteRepository.existsByUsuarioIdAndProdutoId(mockUser.getId(), mockProduct.getId())).thenReturn(false);
        
        // Simula a ação de salvar
        when(favoriteRepository.save(any(Favorite.class))).thenReturn(mockSavedFavorite);

        // 2. When (Act)
        FavoriteResponseDTO response = favoriteService.save(mockToken, request);

        // 3. Then (Assert)
        assertNotNull(response);
        assertEquals(mockProduct.getId(), response.produtoId());
        assertEquals(mockProduct.getNome(), response.nome());
        assertEquals(dataFormatada, response.data_adicionado());

        // Verifica se o método save foi chamado exatamente 1 vez
        verify(favoriteRepository, times(1)).save(any(Favorite.class));
    }
}