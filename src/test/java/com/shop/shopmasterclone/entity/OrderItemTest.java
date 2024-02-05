package com.shop.shopmasterclone.entity;

import com.shop.shopmasterclone.constant.ItemSellStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@code OrderItem} 엔티티의 비즈니스 로직을 검증하는 테스트 클래스입니다.
 * {@code OrderItem}의 주요 기능인 주문 항목 생성과 총 가격 계산 로직을 중심으로 테스트합니다.
 * BDD (Behavior-Driven Development) 방식에 따라 테스트 케이스를 구성하여,
 * 주어진 조건(Given) 하에서 실행(When)할 때 기대되는 결과(Then)를 명확히 기술합니다.
 */
class OrderItemTest {

    /**
     * 주문 항목 생성 기능을 검증합니다.
     * 주어진 상품(Item)과 수량(count)으로 주문 항목(OrderItem)을 생성할 때,
     * 생성된 주문 항목이 정확한 상품과 수량, 그리고 상품 가격으로 초기화되어야 함을 검증합니다.
     * 또한, 상품의 재고가 주문 수량만큼 감소하는지도 확인합니다.
     */
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

    /**
     * 주문 항목의 총 가격 계산 기능을 검증합니다.
     * 주문 항목의 개별 주문 가격(orderPrice)과 주문 수량(count)을 기반으로,
     * 총 가격(getTotalPrice)이 정확히 계산되어야 함을 검증합니다.
     * 이는 주문 항목의 가격 산정 로직이 올바르게 구현되었는지 확인하는 테스트입니다.
     */
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