package com.ckweb.rest_api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/carrinho")
@SecurityRequirement(name = "bearAuth")
@Tag(name = "Carrinho", description = "Operações com o carrinho de compras")
public class CartController {

}