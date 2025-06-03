package com.ckweb.rest_api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ckweb.rest_api.dto.address.AddressGetDTO;
import com.ckweb.rest_api.dto.address.AddressPostDTO;
import com.ckweb.rest_api.dto.address.AddressPutDTO;
import com.ckweb.rest_api.dto.address.AddressUserDTO;
import com.ckweb.rest_api.model.Address;
import com.ckweb.rest_api.model.User;
import com.ckweb.rest_api.repository.AddressRepository;
import com.ckweb.rest_api.repository.UserRepository;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

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

        // try {
        addressRepository.save(address);
        return ResponseEntity.ok().build();
        // } catch (Exception e) {
        //     return ResponseEntity.badRequest().body("Erro ao salvar o endereço: " + e.getMessage());
        // }
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
        Address old_address = addressRepository.findById(addressDTO.id()).get();
        try {
            var address = new Address(
                addressDTO.id(),
                addressDTO.cep(),
                addressDTO.rua(),
                addressDTO.numero(),
                addressDTO.bairro(),
                addressDTO.complemento(),
                old_address.getUsuario()
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

    //

    public ResponseEntity<?> saveUserAddress(String token, AddressUserDTO address) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }
        try {
            Address newAddress = new Address(
            address.cep(),
            address.rua(),
            address.numero(),
            address.bairro(),
            address.complemento(),
            user
            );
            addressRepository.save(newAddress);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar o endereço do usuário");
        }
    }

    public ResponseEntity<?> findByIdInUserAddresses(String token, Long id) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }
        Address address = addressRepository.findByIdAndUsuario(id, user);
        if (address == null || !address.getUsuario().equals(user)) {
            return ResponseEntity.badRequest().body("Endereço não encontrado para o usuário");
        }
        AddressGetDTO addressDTO = new AddressGetDTO(
            address.getId(),
            address.getCep(),
            address.getRua(),
            address.getNumero(),
            address.getBairro(),
            address.getComplemento()
        );
        return ResponseEntity.ok().body(addressDTO);
    }
    
    public ResponseEntity<?> listUserAddresses(String token) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }
        List<Address> addresses = addressRepository.findByUsuario(user);
        if (addresses.isEmpty()) {
            return ResponseEntity.ok().body("Nenhum endereço encontrado para o usuário");
        }
        try {
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
            return ResponseEntity.ok().body(addressDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar endereços do usuário");
        }
    }
    
    public ResponseEntity<?> updateUserAddress(String token, AddressPutDTO addressPutDTO) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }
        if (addressPutDTO.id() == null) {
            return ResponseEntity.badRequest().body("ID do endereço não pode ser nulo");
        }
        Address address = addressRepository.findByIdAndUsuario(addressPutDTO.id(), user);
        if (address == null) {
            return ResponseEntity.badRequest().body("Endereço não encontrado para o usuário");
        }
        try {
            address.setCep(addressPutDTO.cep());
            address.setRua(addressPutDTO.rua());
            address.setNumero(addressPutDTO.numero());
            address.setBairro(addressPutDTO.bairro());
            address.setComplemento(addressPutDTO.complemento());
            addressRepository.save(address);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao atualizar o endereço do usuário");
        }   
    }
    
    public ResponseEntity<?> deleteUserAddress(String token, Long id) {
        String cleanedToken = token.replace("Bearer ", "");
        String email = jwtService.extractEmailFromToken(cleanedToken);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Usuário não encontrado");
        }
        if (id == null) {
            return ResponseEntity.badRequest().body("ID do endereço não pode ser nulo");
        }
        Address address = addressRepository.findByIdAndUsuario(id, user);
        if (address == null) {
            return ResponseEntity.badRequest().body("Endereço não encontrado para o usuário");
        }
        try {
            addressRepository.delete(address);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao deletar o endereço do usuário");
        }
    }
}
