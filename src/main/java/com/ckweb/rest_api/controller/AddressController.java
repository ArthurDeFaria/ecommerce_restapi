package com.ckweb.rest_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ckweb.rest_api.dto.address.AddressPostDTO;
import com.ckweb.rest_api.dto.address.AddressPutDTO;
import com.ckweb.rest_api.dto.address.AddressUserDTO;
import com.ckweb.rest_api.service.impl.AddressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping("/enderecos")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Endereços", description = "Operações com os endereços dos usuários")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping
    @Operation(summary = "Cria um novo endereço", description = "Cria um novo endereço no banco de dados a partir dos dados no body.")
    public ResponseEntity<?> save(@RequestBody AddressPostDTO address) {
        return addressService.save(address);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um endereço pelo id", description = "Retorna o endereço do id fornecido.")
    public ResponseEntity<?> findById(@PathVariable Long id){
        return addressService.findById(id);
    }

    @GetMapping("/usuarios/{id}")
    @Operation(summary = "Busca os endereços de um usuário pelo id do usuário", description = "Retorna os endereços do usuário de id informado.")
    public ResponseEntity<?> findByUserId(@PathVariable Long id){
        return addressService.findByUserId(id);
    }

    @PutMapping
    @Operation(summary = "Atualiza as informações de um endereço qualquer", description = "Atualiza o endereço no banco de dados a partir dos dados no body.")
    public ResponseEntity<?> update(@RequestBody AddressPutDTO addressPutDTO){
        return addressService.update(addressPutDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deleta um endereço qualquer pelo id", description = "Deleta o endereço do id fornecido.")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return addressService.delete(id);
    }

    // Rever DTOs

    @PostMapping("/info")
    @Operation(summary = "Cria um novo endereço para o usuário logado", description = "Cria um novo endereço para o usuário logado. O usuário deve estar autenticado.")
    public ResponseEntity<?> saveUserAddress(@RequestHeader("Authorization") String token, @RequestBody AddressUserDTO address) {
        return addressService.saveUserAddress(token, address);
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "Busca um endereço pelo id, dentro dos endereços do usuário logado", description = "Retorna o endereço do id fornecido.")
    public ResponseEntity<?> findByIdInUserAddresses(@RequestHeader("Authorization") String token, @PathVariable Long id){
        return addressService.findByIdInUserAddresses(token, id);
    }

    @GetMapping("/info")
    @Operation(summary = "Lista os endereços do usuário logado", description = "Retorna os endereços do usuário logado pelo id dentro do JWT Token.")
    public ResponseEntity<?> listUserAddresses(@RequestHeader("Authorization") String token){
        return addressService.listUserAddresses(token);
    }

    @PutMapping("/info")
    @Operation(summary = "Atualiza as informações de um endereço do usuário logado", description = "Atualiza o endereço no banco de dados a partir do id no JWT token.")
    public ResponseEntity<?> updateUserAddress(@RequestHeader("Authorization") String token, @RequestBody AddressPutDTO addressPutDTO){
        return addressService.updateUserAddress(token, addressPutDTO);
    }

    @DeleteMapping("/info/{id}")
    @Operation(summary = "Deleta um endereço do usuário logado pelo id do endereço", description = "Deleta o endereço do usuário logado a partir do id fornecido.")
    public ResponseEntity<?> deleteUserAddress(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        return addressService.deleteUserAddress(token, id);
    }
}
