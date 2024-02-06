package com.shop.shopmasterclone.dto;

import com.shop.shopmasterclone.constant.OrderStatus;
import com.shop.shopmasterclone.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 주문 이력에 대한 데이터 전송 객체(DTO).
 * 주문의 기본 정보와 주문 항목의 목록을 포함하여 주문 이력 페이지에 표시하기 위한 데이터를 제공합니다.
 */
@Getter
@Setter
public class OrderHistDto {

    /**
     * 주문 식별자.
     */
    private Long orderId;

    /**
     * 주문 상태.
     */
    private OrderStatus orderStatus;

    /**
     * 주문 날짜를 "yyyy-MM-dd HH:mm" 형식의 문자열로 표현.
     */
    private String orderDate;

    /**
     * 이 주문에 포함된 주문 항목 목록.
     */
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    /**
     * OrderHistDto 생성자.
     * 주문 엔티티를 받아 주문의 기본 정보를 초기화합니다.
     *
     * @param order 주문 엔티티. 주문의 상세 정보를 포함하고 있습니다.
     */
    public OrderHistDto(Order order){
        this.orderId = order.getId();
        this.orderStatus = order.getOrderStatus();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    /**
     * 주문 항목 DTO를 주문 항목 목록에 추가합니다.
     *
     * @param orderItemDto 주문 항목 DTO. 주문 항목의 상세 정보를 포함하고 있습니다.
     */
    public void addOrderItemDto(OrderItemDto orderItemDto){
        orderItemDtoList.add(orderItemDto);
    }
}

