package com.ckweb.rest_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ckweb.rest_api.model.Address;
import com.ckweb.rest_api.model.User;

public interface AddressRepository extends JpaRepository<Address, Long> {
    public Optional<Address> findById(Long id);
    public List<Address> findByUsuario(User usuario);
    public Address findByIdAndUsuario(Long id, User user);
}
