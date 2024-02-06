package com.shop.shopmasterclone.dto;

import com.shop.shopmasterclone.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

/**
 * 주문 항목에 대한 데이터 전송 객체(DTO).
 * 주문 항목의 상세 정보를 전달하기 위해 사용됩니다.
 * 이 DTO는 주문 항목의 이름, 수량, 주문 가격, 상품 이미지 URL을 포함합니다.
 */
@Getter
@Setter
public class OrderItemDto {
    private String itemNm;  // 상품명
    private int count;      // 주문 수량
    private int orderPrice; // 주문 가격
    private String imgUrl;  // 이미지 URL

    /**
     * OrderItemDto 생성자.
     * OrderItem 엔티티와 상품 이미지 URL을 받아 DTO 인스턴스를 초기화합니다.
     *
     * @param orderItem 주문 항목 엔티티. 주문된 상품의 정보를 포함하고 있습니다.
     * @param imgUrl 상품 이미지의 URL. 상품 이미지를 외부에서 접근할 수 있는 경로입니다.
     */
    public OrderItemDto(OrderItem orderItem, String imgUrl){
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
    }
}
