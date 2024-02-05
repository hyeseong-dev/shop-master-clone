package com.shop.shopmasterclone.entity;

import com.shop.shopmasterclone.constant.ItemSellStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    @DisplayName("주문 항목 생성 시, 주문된 상품과 수량이 정확히 설정되어야 한다")
    void givenItemAndCount_whenCreateOrderItem_thenOrderItemShouldHaveCorrectItemAndCount() {
        // Given: 상품과 주문 수량을 준비한다
        Item item = new Item();
        item.setId(1L);
        item.setPrice(10000);
        item.setStockNumber(100); // 초기 재고 설정
        int orderCount = 2;

        // When: 주문 항목을 생성한다
        OrderItem orderItem = OrderItem.createOrderItem(item, orderCount);

        // Then: 생성된 주문 항목은 주문된 상품과 수량을 정확히 반영해야 한다
        assertEquals(item, orderItem.getItem());
        assertEquals(orderCount, orderItem.getCount());
        assertEquals(10000, orderItem.getOrderPrice());
        assertEquals(98, item.getStockNumber()); // 재고가 주문 수량만큼 감소했는지 확인
    }

    @Test
    @DisplayName("주문 항목의 총 가격 계산은 주문 가격과 수량을 기반으로 정확히 이루어져야 한다")
    void givenOrderPriceAndCount_whenGetTotalPrice_thenTotalPriceShouldBeCorrectlyCalculated() {
        // Given: 주문 가격과 수량을 설정한 주문 항목을 준비한다
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderPrice(5000); // 개별 상품의 주문 가격
        orderItem.setCount(2); // 주문 수량

        // When: 총 가격을 계산한다
        int totalPrice = orderItem.getTotalPrice();

        // Then: 총 가격은 주문 가격과 수량을 기반으로 정확히 계산되어야 한다
        assertEquals(10000, totalPrice); // 총 가격 검증
    }
}