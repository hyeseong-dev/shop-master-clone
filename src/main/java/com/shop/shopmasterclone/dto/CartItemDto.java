package com.shop.shopmasterclone.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 장바구니 항목에 대한 데이터 전송 객체(DTO).
 * 사용자가 장바구니에 담을 상품의 정보와 수량을 관리합니다.
 * 이 DTO는 상품 아이디와 상품 수량에 대한 입력 검증을 포함합니다.
 */
@Getter
@Setter
public class CartItemDto {

    /**
     * 상품의 고유 식별자.
     * 이 값은 null이 될 수 없으며, 상품을 식별하기 위해 필수적입니다.
     */
    @NotNull(message="상품 아이디는 필수 입력 값입니다.")
    private Long itemId;

    /**
     * 사용자가 선택한 상품의 수량.
     * 이 값은 최소 1 이상이어야 합니다. 즉, 사용자는 최소한 1개 이상의 상품을 장바구니에 담아야 합니다.
     */
    @Min(value = 1, message="최소 1개 이상 담아주세요.")
    private int count;
}