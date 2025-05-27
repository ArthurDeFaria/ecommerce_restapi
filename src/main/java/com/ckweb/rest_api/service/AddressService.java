package com.ckweb.rest_api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ckweb.rest_api.dto.AddressGetDTO;
import com.ckweb.rest_api.dto.AddressPostDTO;
import com.ckweb.rest_api.dto.AddressPutDTO;
import com.ckweb.rest_api.model.Address;
import com.ckweb.rest_api.repository.AddressRepository;
import com.ckweb.rest_api.repository.UserRepository;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<?> save(AddressPostDTO addressDTO) {
        if (addressDTO.usuario_id() == null) {
            return ResponseEntity.badRequest().body("ID do usuário não pode ser nulo");
        }

        var optionalUser = userRepository.findById(addressDTO.usuario_id());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }

        Address address = new Address(
            addressDTO.cep(),
            addressDTO.rua(),
            addressDTO.numero(),
            addressDTO.bairro(),
            addressDTO.complemento(),
            optionalUser.get()
        );

        try {
            addressRepository.save(address);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar o endereço");
        }
    }

    public ResponseEntity<?> findById(Long id){
        try {
            var optionalAddress = addressRepository.findById(id);
            if (optionalAddress.isEmpty()) {
                return ResponseEntity.badRequest().body("Endereço não encontrado");
            }
            Address address = optionalAddress.get();
            AddressGetDTO addressDTO = new AddressGetDTO(
                address.getId(),
                address.getCep(),
                address.getRua(),
                address.getNumero(),
                address.getBairro(),
                address.getComplemento()
            );
            return ResponseEntity.ok(addressDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Nenhum endereço encontrado");
        }
    }

    public ResponseEntity<?> findByUserId(Long id) {
        try {
            if (id == null) {
                return ResponseEntity.badRequest().body("ID do usuário não pode ser nulo");
            }
            var optionalUser = userRepository.findById(id);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.badRequest().body("Usuário não encontrado");
            }

            var addresses = addressRepository.findByUsuario(optionalUser.get());
            List<AddressGetDTO> addressDTOs = addresses.stream()
                .map(address -> new AddressGetDTO(
                    address.getId(),
                    address.getCep(),
                    address.getRua(),
                    address.getNumero(),
                    address.getBairro(),
                    address.getComplemento()
                ))
                .toList();
            
            return ResponseEntity.ok(addressDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar endereços do usuário");
        }
    }
    
    public ResponseEntity<?> update(AddressPutDTO addressDTO){
        if (addressDTO.id() == null) {
            return ResponseEntity.badRequest().body("ID do endereço não pode ser nulo");
        }
        var optionalAddress = addressRepository.findById(addressDTO.id());
        if(optionalAddress.isEmpty()) {
            return ResponseEntity.badRequest().body("Endereço não encontrado");
        }

        try {
            var address = new Address(
            addressDTO.id(),
            addressDTO.cep(),
            addressDTO.rua(),
            addressDTO.numero(),
            addressDTO.bairro(),
            addressDTO.complemento()
            );
            addressRepository.save(address);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar endereço");
        }
    }

    public ResponseEntity<?> delete(Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("ID do endereço não pode ser nulo");
        }
        var optionalAddress = addressRepository.findById(id);
        if (optionalAddress.isEmpty()) {
            return ResponseEntity.badRequest().body("Endereço não encontrado");
        }

        try {
            addressRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao deletar endereço");
        }
    }
}
