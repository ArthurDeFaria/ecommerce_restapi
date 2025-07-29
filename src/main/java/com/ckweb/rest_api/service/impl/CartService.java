package com.ckweb.rest_api.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ckweb.rest_api.dto.cart.CartAddItemRequestDTO;
import com.ckweb.rest_api.dto.cart.CartGetResponseDTO;
import com.ckweb.rest_api.dto.cart.item.CartItemGetDTO;
import com.ckweb.rest_api.exception.ResourceNotFoundException;
import com.ckweb.rest_api.model.Cart;
import com.ckweb.rest_api.model.CartItem;
import com.ckweb.rest_api.model.Product;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.repository.CartItemRepository;
import com.ckweb.rest_api.repository.CartRepository;
import com.ckweb.rest_api.repository.ProductRepository;
import com.ckweb.rest_api.repository.UserRepository;
import com.ckweb.rest_api.service.JwtService;
import com.ckweb.rest_api.service.interfaces.CartServiceInterface;

@Service
public class CartService implements CartServiceInterface {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private List<CartItemGetDTO> mapCartItemsToDto(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            return List.of();
        }
        return cartItems.stream()
                .map(item -> new CartItemGetDTO(
                    item.getId(),
                    item.getProduto().getNome(),
                    item.getProduto().getPreco(),
                    item.getProduto().getImagem_url(),
                    item.getQuantidade()))
                .collect(Collectors.toList());
    }

    @Override
    public CartGetResponseDTO findById(Long id) {
        Cart cart = cartRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cart not found with id " + id));
        return new CartGetResponseDTO(cart.getId(), mapCartItemsToDto(cart.getItens()));
    }

    @Override
    @Transactional(readOnly = true)
    public CartGetResponseDTO getCartInfo(String token) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        Cart cart = cartRepository.findByUsuario(user);
        if (cart == null) {
            throw new ResourceNotFoundException("Carrinho não encontrado para o usuário");
        }

        return new CartGetResponseDTO(cart.getId(), mapCartItemsToDto(cart.getItens()));
    }

    @Transactional
    @Override
    public CartGetResponseDTO addItemToCart(String token, CartAddItemRequestDTO request) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        Cart carrinho = cartRepository.findByUsuario(user);
        if (carrinho == null) {
            carrinho = Cart.builder().usuario(user).build();
            carrinho = cartRepository.save(carrinho); // Salva o novo carrinho
        }

        Product produto = productRepository.findById(request.idProduto())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

        CartItem itemExistente = carrinho.getItens().stream()
                .filter(item -> item.getProduto().getId().equals(produto.getId()))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            itemExistente.setQuantidade(itemExistente.getQuantidade() + request.quantidade());
            cartItemRepository.save(itemExistente);
        } else {
            CartItem novoItem = CartItem.builder()
                    .produto(produto)
                    .quantidade(request.quantidade())
                    .carrinho(carrinho)
                    .build();
            carrinho.getItens().add(novoItem);
        }

        cartRepository.save(carrinho);

        return new CartGetResponseDTO(carrinho.getId(), mapCartItemsToDto(carrinho.getItens()));
    }

    @Transactional
    @Override
    public CartGetResponseDTO removeItemFromCart(String token, Long idItem) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        Cart carrinho = cartRepository.findByUsuario(user);
        if (carrinho == null) {
            throw new ResourceNotFoundException("Carrinho não encontrado para o usuário");
        }

        CartItem itemToRemove = cartItemRepository.findById(idItem)
                .orElseThrow(() -> new ResourceNotFoundException("Item do carrinho não encontrado"));

        if (!itemToRemove.getCarrinho().getId().equals(carrinho.getId())) {
            throw new ResourceNotFoundException("Item não pertence ao carrinho do usuário");
        }

        carrinho.getItens().remove(itemToRemove);

        cartItemRepository.delete(itemToRemove);

        cartRepository.save(carrinho);

        Cart updatedCarrinho = cartRepository.findById(carrinho.getId())
                                            .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado após atualização"));

        return new CartGetResponseDTO(updatedCarrinho.getId(), mapCartItemsToDto(updatedCarrinho.getItens()));
    }

    @Transactional
    @Override
    public CartGetResponseDTO updateItemQuantity(String token, Long itemId, int quantity) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        Cart carrinho = user.getCarrinho();
        if (carrinho == null) {
            throw new ResourceNotFoundException("Carrinho não encontrado para o usuário");
        }

        CartItem itemToUpdate = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item do carrinho não encontrado com ID: " + itemId));

        if (!itemToUpdate.getCarrinho().getId().equals(carrinho.getId())) {
            throw new ResourceNotFoundException("Item não pertence ao carrinho do usuário.");
        }

        if (quantity <= 0) {
            carrinho.getItens().remove(itemToUpdate);
            cartItemRepository.delete(itemToUpdate);
        } else {
            itemToUpdate.setQuantidade(quantity);
            cartItemRepository.save(itemToUpdate);
        }

        cartRepository.save(carrinho);

        Cart updatedCarrinho = cartRepository.findById(carrinho.getId())
                                            .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado após atualização de item."));

        return new CartGetResponseDTO(updatedCarrinho.getId(), mapCartItemsToDto(updatedCarrinho.getItens()));
    }

    @Transactional
    @Override
    public CartGetResponseDTO clearCart(String token) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        Cart carrinho = user.getCarrinho();
        if (carrinho == null) {
            throw new ResourceNotFoundException("Carrinho não encontrado para o usuário");
        }

        cartItemRepository.deleteByCarrinho(carrinho);

        carrinho.getItens().clear();

        cartRepository.save(carrinho);

        Cart updatedCarrinho = cartRepository.findById(carrinho.getId())
                                            .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado após limpeza."));

        return new CartGetResponseDTO(updatedCarrinho.getId(), mapCartItemsToDto(updatedCarrinho.getItens()));
    }    

}