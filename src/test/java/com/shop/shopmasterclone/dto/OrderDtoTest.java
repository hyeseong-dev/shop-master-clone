package com.shop.shopmasterclone.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


/**
 * OrderDto 클래스의 유효성 검사 로직을 검증하는 테스트 클래스입니다.
 * 주문 데이터 전송 객체(DTO)에 설정된 제약 조건들이 올바르게 적용되는지 확인합니다.
 *
 * <p>이 테스트는 Bean Validation API를 사용하여 OrderDto 인스턴스의 유효성을 검증합니다.
 * 검증 로직은 상품 아이디의 NotNull 조건과 주문 수량의 Min, Max 조건을 포함합니다.</p>
 */
@SpringBootTest
class OrderDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * OrderDto의 상품 아이디 필드가 null일 때 유효성 검사가 실패하는지 검증합니다.
     * 상품 아이디는 필수 입력 값이므로, 이 필드가 null일 경우 유효성 검사에서 실패해야 합니다.
     */
    @Test
    @DisplayName("OrderDto 유효성 검사: 상품 아이디가 null일 경우 실패해야 함")
    void whenItemIdIsNull_thenValidationFails() {
        // 상품 아이디가 null인 OrderDto 객체를 준비하고, 유효성 검사를 수행한 후,
        // 해당 검사에서 제약 조건 위반 사항이 있는지 확인합니다.
        // Given
        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(null);
        orderDto.setCount(1);

        // When
        // var constraintViolations = validator.validate(orderDto);
        Set<ConstraintViolation<OrderDto>> constraintViolations = validator.validate(orderDto);

        // Then
        assertFalse(constraintViolations.isEmpty(), "유효성 검사가 실패해야 합니다.");
        assertTrue(constraintViolations.stream().anyMatch(violation -> "상품 아이디는 필수 입력 값입니다.".equals(violation.getMessage())));
    }

    /**
     * OrderDto의 주문 수량 필드가 설정된 범위를 벗어날 때 유효성 검사가 실패하는지 검증합니다.
     * 주문 수량은 1개로 제한되어 있으므로, 이 범위를 벗어나는 값이 설정된 경우 유효성 검사에서 실패해야 합니다.
     */
    @Test
    @DisplayName("OrderDto 유효성 검사: 주문 수량이 범위를 벗어날 경우 실패해야 함")
    void whenCountIsOutOfRange_thenValidationFails() {
        // 주문 수량이 범위를 벗어난 OrderDto 객체를 준비하고, 유효성 검사를 수행한 후,
        // 해당 검사에서 제약 조건 위반 사항이 있는지 확인합니다.

        // Given
        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(1L);
        orderDto.setCount(0); // 최소값보다 작음

        // When
        // var constraintViolations = validator.validate(orderDto);
        Set<ConstraintViolation<OrderDto>> constraintViolations = validator.validate(orderDto);

        // Then
        assertFalse(constraintViolations.isEmpty(), "유효성 검사가 실패해야 합니다.");
        assertTrue(constraintViolations.stream().anyMatch(violation -> "최소 주문 수량은 1개입니다.".equals(violation.getMessage())));
    }
}