package com.ckweb.rest_api.service.interfaces;

import java.util.List;

import com.ckweb.rest_api.dto.order.OrderPostRequestDTO;
import com.ckweb.rest_api.dto.order.OrderResponseDTO;

public interface OrderServiceInterface {
    List<OrderResponseDTO> findAll();
    OrderResponseDTO findOrderById(Long id);
    List<OrderResponseDTO> findOrdersByUserId(Long userId);
    OrderResponseDTO save(String token, OrderPostRequestDTO order);
}
