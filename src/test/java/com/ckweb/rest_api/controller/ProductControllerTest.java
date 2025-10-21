package com.ckweb.rest_api.controller;

import com.ckweb.rest_api.dto.product.ProductPostRequestDTO;
import com.ckweb.rest_api.exception.ResourceNotFoundException;
import com.ckweb.rest_api.service.interfaces.ProductServiceInterface;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductServiceInterface productService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminDeveConseguirApagarProduto() throws Exception {
        // Simula o comportamento do serviço: quando delete() for chamado, retorne OK.
        when(productService.delete(anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/produtos/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userNaoDeveConseguirApagarProduto() throws Exception {
        mockMvc.perform(delete("/produtos/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerNaoDeveConseguirApagarProduto() throws Exception {
        mockMvc.perform(delete("/produtos/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void managerDeveConseguirCriarProduto() throws Exception {
        String productJson = """
        {
            "nome": "Produto Teste",
            "categoria": "Testes",
            "descricao": "Desc",
            "preco": 10.0,
            "quantidadeEstoque": 1,
            "imagemUrl": "img.png"
        }
        """;

        // Simula o comportamento do serviço: quando save() for chamado, retorne OK.
        when(productService.save(any(ProductPostRequestDTO.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productJson))
                .andExpect(status().isOk());
    }

    @Test
    void deveRetornar404AoBuscarProdutoInexistente() throws Exception {
        // Simula o serviço a lançar a exceção que o GlobalExceptionHandler vai apanhar
        when(productService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Produto não encontrado"));

        mockMvc.perform(get("/produtos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Produto não encontrado"))
                .andExpect(jsonPath("$.status").value(404));
    }
}