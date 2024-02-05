package com.shop.shopmasterclone.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 주문 데이터 전송 객체(DTO)를 나타내는 클래스입니다.
 * 주문 생성 및 수정 요청 시 클라이언트로부터 받은 데이터를 담기 위해 사용됩니다.
 * 이 클래스는 주문에 필요한 상품 아이디와 주문 수량 정보를 포함합니다.
 *
 * <p>Bean Validation을 통해 필드의 유효성 검사를 수행하여,
 * 상품 아이디가 null이 아니며, 주문 수량이 1개로 제한되도록 합니다.</p>
 */
@Getter
@Setter
public class OrderDto {

    /**
     * 주문할 상품의 아이디입니다.
     * null이 아니어야 하며, 이를 통해 주문할 상품을 식별합니다.
     */
    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long itemId;

    /**
     * 주문할 상품의 수량입니다.
     * 최소 1개, 최대 1개를 주문할 수 있습니다.
     * 이 제약사항은 주문 시스템의 비즈니스 규칙에 따라 설정됩니다.
     */
    @Min(value=1, message="최소 주문 수량은 1개입니다.")
    @Max(value=1, message="최대 주문 수량은 1개입니다.")
    private int count;
}
