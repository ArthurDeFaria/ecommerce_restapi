package com.ckweb.rest_api.service.impl;

import com.ckweb.rest_api.dto.coupon.CouponPostRequestDTO;
import com.ckweb.rest_api.dto.coupon.CouponResponseDTO;
import com.ckweb.rest_api.dto.coupon.ValidateCouponResponseDTO;
import com.ckweb.rest_api.model.*;
import com.ckweb.rest_api.model.enumeration.CouponType;
import com.ckweb.rest_api.repository.CouponRepository;
import com.ckweb.rest_api.repository.UserCouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private CouponService couponService;

    private User testUser;
    private List<CartItem> testCartItems;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).email("test@user.com").build();
        testProduct = Product.builder().id(1L).nome("Prod Teste").preco(100.0).build();
        testCartItems = List.of(
                CartItem.builder().produto(testProduct).quantidade(2).build()
        );
    }

    @Test
    void deveCriarCupomComSucesso() {
        // Given
        CouponPostRequestDTO request = new CouponPostRequestDTO(
                "TESTE10", CouponType.PERCENTAGE, 10.0, LocalDate.now().plusDays(10), 100, 1);
        Coupon savedCoupon = Coupon.builder()
                .id(1L)
                .codigo(request.codigo().toUpperCase())
                .tipo(request.tipo())
                .valor(request.valor())
                .dataValidade(request.dataValidade().toString())
                .usoMaximo(request.usoMaximo())
                .usoMaximoIndividual(request.usoMaximoIndividual())
                .ativo(true)
                .build();

        when(couponRepository.findByCodigoIgnoreCase(request.codigo())).thenReturn(Optional.empty());
        when(couponRepository.save(any(Coupon.class))).thenReturn(savedCoupon);

        // When
        CouponResponseDTO result = couponService.createCoupon(request);

        // Then
        assertNotNull(result);
        assertEquals(savedCoupon.getId(), result.id());
        assertEquals(savedCoupon.getCodigo(), result.codigo());
        assertEquals(savedCoupon.getTipo(), result.tipo());
        assertTrue(result.ativo());
        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    void naoDeveCriarCupomComCodigoDuplicado() {
        // Given
        CouponPostRequestDTO request = new CouponPostRequestDTO(
                "DUPLICADO", CouponType.FIXED_AMOUNT, 50.0, LocalDate.now().plusDays(5), null, 1);
        // Simula que o código já existe
        when(couponRepository.findByCodigoIgnoreCase(request.codigo())).thenReturn(Optional.of(new Coupon()));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            couponService.createCoupon(request);
        });
        assertEquals("Código do cupom já existe.", exception.getMessage());
        verify(couponRepository, never()).save(any(Coupon.class));
    }

    @Test
    void deveValidarCupomPercentualCorretamente() {
        // Given
        Coupon validCoupon = Coupon.builder()
                .id(1L).codigo("VALIDO10").tipo(CouponType.PERCENTAGE).valor(10.0)
                .dataValidade(LocalDate.now().plusDays(1).toString()).ativo(true)
                .usoMaximo(10).usoMaximoIndividual(1).build();

        when(couponRepository.findByCodigoIgnoreCase("VALIDO10")).thenReturn(Optional.of(validCoupon));
        when(userCouponRepository.countByCupomId(validCoupon.getId())).thenReturn(5L); // Uso total < usoMaximo
        when(userCouponRepository.findByUsuarioIdAndCupomId(testUser.getId(), validCoupon.getId())).thenReturn(Optional.empty()); // Usuário nunca usou

        // When
        ValidateCouponResponseDTO result = couponService.validateCoupon("VALIDO10", testUser.getId(), testCartItems);

        // Then
        assertTrue(result.valido());
        assertEquals("Cupom válido!", result.mensagem());
        assertEquals(validCoupon.getId(), result.couponId());
        // Desconto = 10% de 200.0 (2 * 100.0)
        assertEquals(0, BigDecimal.valueOf(20.00).compareTo(result.valorDesconto()), "Desconto percentual incorreto");
    }

    @Test
    void deveValidarCupomValorFixoCorretamente() {
        // Given
        Coupon validCoupon = Coupon.builder()
                .id(2L).codigo("VALIDO50").tipo(CouponType.FIXED_AMOUNT).valor(50.0)
                .dataValidade(LocalDate.now().plusDays(1).toString()).ativo(true)
                .usoMaximo(null).usoMaximoIndividual(1).build(); // Sem limite total

        when(couponRepository.findByCodigoIgnoreCase("VALIDO50")).thenReturn(Optional.of(validCoupon));
        // Não precisa mockar countByCupomId pois usoMaximo é null
        when(userCouponRepository.findByUsuarioIdAndCupomId(testUser.getId(), validCoupon.getId())).thenReturn(Optional.empty());

        // When
        ValidateCouponResponseDTO result = couponService.validateCoupon("VALIDO50", testUser.getId(), testCartItems);

        // Then
        assertTrue(result.valido());
        assertEquals("Cupom válido!", result.mensagem());
        assertEquals(validCoupon.getId(), result.couponId());
        assertEquals(0, BigDecimal.valueOf(50.00).compareTo(result.valorDesconto()), "Desconto de valor fixo incorreto");
    }

    @Test
    void naoDeveValidarCupomInexistente() {
        // Given
        when(couponRepository.findByCodigoIgnoreCase("INEXISTENTE")).thenReturn(Optional.empty());

        // When
        ValidateCouponResponseDTO result = couponService.validateCoupon("INEXISTENTE", testUser.getId(), testCartItems);

        // Then
        assertFalse(result.valido());
        assertEquals("Cupom inválido.", result.mensagem());
        assertNull(result.couponId());
        assertEquals(0, BigDecimal.ZERO.compareTo(result.valorDesconto()));
    }

     @Test
     void naoDeveValidarCupomExpirado() {
         // Given
         Coupon expiredCoupon = Coupon.builder()
                 .id(3L).codigo("EXPIRADO").tipo(CouponType.PERCENTAGE).valor(10.0)
                 .dataValidade(LocalDate.now().minusDays(1).toString()) // Data no passado
                 .ativo(true).build();

         when(couponRepository.findByCodigoIgnoreCase("EXPIRADO")).thenReturn(Optional.of(expiredCoupon));

         // When
         ValidateCouponResponseDTO result = couponService.validateCoupon("EXPIRADO", testUser.getId(), testCartItems);

         // Then
         assertFalse(result.valido());
         assertEquals("Cupom expirado.", result.mensagem());
         assertNull(result.couponId());
         verify(couponRepository, times(1)).save(expiredCoupon); // Verifica se tentou desativar
         assertFalse(expiredCoupon.getAtivo(), "Cupom expirado deveria ser desativado");
     }

     @Test
     void naoDeveValidarCupomComLimiteTotalAtingido() {
         // Given
         Coupon limitCoupon = Coupon.builder()
                 .id(4L).codigo("ESGOTADO").tipo(CouponType.FIXED_AMOUNT).valor(10.0)
                 .dataValidade(LocalDate.now().plusDays(1).toString()).ativo(true)
                 .usoMaximo(5).usoMaximoIndividual(1).build(); // Limite total de 5

         when(couponRepository.findByCodigoIgnoreCase("ESGOTADO")).thenReturn(Optional.of(limitCoupon));
         // Simula que já foi usado 5 vezes no total
         when(userCouponRepository.countByCupomId(limitCoupon.getId())).thenReturn(5L);

         // When
         ValidateCouponResponseDTO result = couponService.validateCoupon("ESGOTADO", testUser.getId(), testCartItems);

         // Then
         assertFalse(result.valido());
         assertEquals("Limite de uso total do cupom atingido.", result.mensagem());
     }

    @Test
    void naoDeveValidarCupomComLimiteIndividualAtingido() {
        // Given
        Coupon limitCoupon = Coupon.builder()
                .id(5L).codigo("INDIVIDUAL").tipo(CouponType.FIXED_AMOUNT).valor(10.0)
                .dataValidade(LocalDate.now().plusDays(1).toString()).ativo(true)
                .usoMaximo(null).usoMaximoIndividual(2).build(); // Limite individual de 2

        // Simula que este usuário já usou 2 vezes
        UserCoupon usage = UserCoupon.builder().usuario(testUser).cupom(limitCoupon).usoIndividual(2).build();

        when(couponRepository.findByCodigoIgnoreCase("INDIVIDUAL")).thenReturn(Optional.of(limitCoupon));
        when(userCouponRepository.findByUsuarioIdAndCupomId(testUser.getId(), limitCoupon.getId())).thenReturn(Optional.of(usage));

        // When
        ValidateCouponResponseDTO result = couponService.validateCoupon("INDIVIDUAL", testUser.getId(), testCartItems);

        // Then
        assertFalse(result.valido());
        assertEquals("Você já atingiu o limite de uso para este cupom.", result.mensagem());
    }
}