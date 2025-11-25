package com.ckweb.rest_api.service.interfaces;

import com.ckweb.rest_api.dto.coupon.*;
import com.ckweb.rest_api.model.CartItem;
import com.ckweb.rest_api.model.Order;

import java.util.List;

public interface CouponServiceInterface {
    CouponResponseDTO createCoupon(CouponPostRequestDTO dto);
    List<CouponResponseDTO> listCoupons();
    ValidateCouponResponseDTO validateCoupon(String codigo, Long userId, List<CartItem> cartItems);
    void deleteCoupon(Long id);
    void registerCouponUsage(Order order);
}