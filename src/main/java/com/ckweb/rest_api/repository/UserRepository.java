package com.ckweb.rest_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ckweb.rest_api.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String email);
}