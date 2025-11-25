package com.ckweb.rest_api.repository;

import com.ckweb.rest_api.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCodigoIgnoreCase(String codigo);
}