package com.ckweb.rest_api.repository;

import com.ckweb.rest_api.model.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    Optional<UserCoupon> findByUsuarioIdAndCupomId(Long usuarioId, Long cupomId);
    long countByCupomId(Long cupomId); // Para verificar uso total
}