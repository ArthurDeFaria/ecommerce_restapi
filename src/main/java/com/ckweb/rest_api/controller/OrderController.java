package com.ckweb.rest_api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ckweb.rest_api.dto.order.OrderPostRequestDTO;
import com.ckweb.rest_api.dto.order.OrderResponseDTO;
import com.ckweb.rest_api.service.impl.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/pedidos")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Endereços", description = "Operações com os endereços dos usuários")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "Busca todos os pedidos", description = "Retorna uma lista de todos os pedidos feitos.")
    public ResponseEntity<List<OrderResponseDTO>> findAll() {
        List<OrderResponseDTO> response = orderService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um pedido pelo id", description = "Retorna o pedido do id fornecido.")
    public ResponseEntity<OrderResponseDTO> findOrderById(@PathVariable Long id) {
        OrderResponseDTO response = orderService.findOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/usuario/{id}")
    @Operation(summary = "Busca os pedidos de um usuário pelo id do usuário", description = "Retorna os pedidos do usuário de id informado.")
    public ResponseEntity<List<OrderResponseDTO>> findOrdersByUserId(@PathVariable Long id) {
        List<OrderResponseDTO> response = orderService.findOrdersByUserId(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/finalizar")
    @Operation(summary = "Cria um novo pedido", description = "Cria um novo pedido no banco de dados a partir dos dados no body.")
    public ResponseEntity<OrderResponseDTO> save(@RequestHeader("Authorization") String token, @RequestBody OrderPostRequestDTO orderPostDTO) {
        OrderResponseDTO response = orderService.save(token, orderPostDTO);
        return ResponseEntity.ok(response);
    }
    
}
