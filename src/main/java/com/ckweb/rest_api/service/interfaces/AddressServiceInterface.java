package com.ckweb.rest_api.service.interfaces;

import org.springframework.http.ResponseEntity;

import com.ckweb.rest_api.dto.address.AddressPostDTO;
import com.ckweb.rest_api.dto.address.AddressPutDTO;
import com.ckweb.rest_api.dto.address.AddressUserDTO;

public interface AddressServiceInterface {
    public ResponseEntity<?> save(AddressPostDTO addressDTO);
    public ResponseEntity<?> findById(Long id);
    public ResponseEntity<?> findByUserId(Long id);
    public ResponseEntity<?> update(AddressPutDTO addressDTO);
    public ResponseEntity<?> delete(Long id);
    public ResponseEntity<?> saveUserAddress(String token, AddressUserDTO address);
    public ResponseEntity<?> findByIdInUserAddresses(String token, Long id);
    public ResponseEntity<?> listUserAddresses(String token);
    public ResponseEntity<?> updateUserAddress(String token, AddressPutDTO addressPutDTO);
    public ResponseEntity<?> deleteUserAddress(String token, Long id);
}
