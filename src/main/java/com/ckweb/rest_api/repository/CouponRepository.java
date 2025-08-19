package com.ckweb.rest_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ckweb.rest_api.model.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    
}
