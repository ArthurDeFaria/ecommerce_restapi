package com.ckweb.rest_api.service.impl;

import com.ckweb.rest_api.dto.coupon.*;
import com.ckweb.rest_api.exception.ResourceNotFoundException;
import com.ckweb.rest_api.model.*;
import com.ckweb.rest_api.model.enumeration.CouponType;
import com.ckweb.rest_api.repository.*;
import com.ckweb.rest_api.service.interfaces.CouponServiceInterface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CouponService implements CouponServiceInterface {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Override
    @Transactional
    public CouponResponseDTO createCoupon(CouponPostRequestDTO dto) {
        if (couponRepository.findByCodigoIgnoreCase(dto.codigo()).isPresent()) {
            throw new IllegalArgumentException("Código do cupom já existe.");
        }
        Coupon coupon = Coupon.builder()
                .codigo(dto.codigo().toUpperCase())
                .tipo(dto.tipo())
                .valor(dto.valor())
                .dataValidade(dto.dataValidade().toString())
                .usoMaximo(dto.usoMaximo())
                .usoMaximoIndividual(dto.usoMaximoIndividual())
                .ativo(true)
                .build();
        coupon = couponRepository.save(coupon);
        return convertToDto(coupon);
    }

    @Override
    public List<CouponResponseDTO> listCoupons() {
        return couponRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ValidateCouponResponseDTO validateCoupon(String codigo, Long userId, List<CartItem> cartItems) {
        Optional<Coupon> couponOpt = couponRepository.findByCodigoIgnoreCase(codigo);

        if (couponOpt.isEmpty()) {
            return new ValidateCouponResponseDTO(false, "Cupom inválido.", BigDecimal.ZERO, null);
        }

        Coupon coupon = couponOpt.get();

        if (!coupon.getAtivo()) {
            return new ValidateCouponResponseDTO(false, "Cupom inativo.", BigDecimal.ZERO, null);
        }

        if (LocalDate.parse(coupon.getDataValidade()).isBefore(LocalDate.now())) {
             coupon.setAtivo(false);
             couponRepository.save(coupon);
            return new ValidateCouponResponseDTO(false, "Cupom expirado.", BigDecimal.ZERO, null);
        }

        if (coupon.getUsoMaximo() != null && coupon.getUsoMaximo() > 0) {
            long totalUses = userCouponRepository.countByCupomId(coupon.getId()); // Ou contar Pedidos
            if (totalUses >= coupon.getUsoMaximo()) {
                return new ValidateCouponResponseDTO(false, "Limite de uso total do cupom atingido.", BigDecimal.ZERO, null);
            }
        }

        if (coupon.getUsoMaximoIndividual() != null && coupon.getUsoMaximoIndividual() > 0) {
            Optional<UserCoupon> userCouponOpt = userCouponRepository.findByUsuarioIdAndCupomId(userId, coupon.getId());
            if (userCouponOpt.isPresent() && userCouponOpt.get().getUsoIndividual() >= coupon.getUsoMaximoIndividual()) {
                return new ValidateCouponResponseDTO(false, "Você já atingiu o limite de uso para este cupom.", BigDecimal.ZERO, null);
            }
        }

        BigDecimal totalProdutos = cartItems.stream()
            .map(item -> BigDecimal.valueOf(item.getProduto().getPreco()).multiply(BigDecimal.valueOf(item.getQuantidade())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getTipo() == CouponType.FIXED_AMOUNT) {
            discount = BigDecimal.valueOf(coupon.getValor());
            if(discount.compareTo(totalProdutos) > 0) {
                discount = totalProdutos;
            }
        } else if (coupon.getTipo() == CouponType.PERCENTAGE) {
            discount = totalProdutos.multiply(BigDecimal.valueOf(coupon.getValor() / 100.0));
        }

        return new ValidateCouponResponseDTO(true, "Cupom válido!", discount.setScale(2, BigDecimal.ROUND_HALF_UP), coupon.getId());
    }

    @Override
    @Transactional
    public void registerCouponUsage(Order order) {
        Coupon coupon = order.getCupom();
        User user = order.getUsuario();

        if (coupon == null || user == null) {
            return;
        }

        UserCoupon userCoupon = userCouponRepository.findByUsuarioIdAndCupomId(user.getId(), coupon.getId())
                .orElseGet(() -> UserCoupon.builder()
                        .usuario(user)
                        .cupom(coupon)
                        .usoIndividual(0)
                        .build());

        userCoupon.setUsoIndividual(userCoupon.getUsoIndividual() + 1);
        userCouponRepository.save(userCoupon);

        // TODO: Opcional - Desativar cupom se uso máximo total foi atingido
        if (coupon.getUsoMaximo() != null && coupon.getUsoMaximo() > 0) {
            long totalUses = userCouponRepository.countByCupomId(coupon.getId()); // Recontar após salvar
            if (totalUses >= coupon.getUsoMaximo()) {
                coupon.setAtivo(false);
                couponRepository.save(coupon);
            }
        }
    }

    @Override
    @Transactional
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cupom não encontrado.");
        }
        // TODO: Considerar desativar (`setAtivo(false)`) em vez de deletar para manter histórico
        couponRepository.deleteById(id);
    }

    private CouponResponseDTO convertToDto(Coupon coupon) {
        return new CouponResponseDTO(
                coupon.getId(),
                coupon.getCodigo(),
                coupon.getTipo(),
                coupon.getValor(),
                LocalDate.parse(coupon.getDataValidade()),
                coupon.getUsoMaximo(),
                coupon.getUsoMaximoIndividual(),
                coupon.getAtivo()
        );
    }
}