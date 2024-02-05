package com.shop.shopmasterclone.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link OutofStockException} 클래스의 동작을 검증하는 테스트 클래스입니다.
 */
class OutofStockExceptionTest {

    @Test
    @DisplayName("재고 부족 상황에서 정확한 예외 메시지와 함께 OutofStockException이 발생해야 한다.")
    void whenStockIsInsufficient_thenOutofStockExceptionShouldBeThrownWithCorrectMessage() {
        // Given: 예외 메시지로 "상품의 재고가 부족합니다."를 사용한다.
        String expectedMessage = "상품의 재고가 부족합니다.";

        // When: 재고 부족 상황을 강제하여 OutofStockException을 발생시킨다.
        OutofStockException exception = assertThrows(OutofStockException.class, () -> {
            throw new OutofStockException(expectedMessage);
        }, "재고 부족 상황에서 OutofStockException이 발생해야 한다.");

        // Then: 발생한 예외의 메시지가 기대한 메시지와 일치해야 한다.
        assertEquals(expectedMessage, exception.getMessage(), "발생한 예외의 메시지는 '상품의 재고가 부족합니다.'여야 한다.");
    }
}