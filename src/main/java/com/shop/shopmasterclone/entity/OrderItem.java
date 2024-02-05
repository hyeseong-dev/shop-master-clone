package com.shop.shopmasterclone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


/**
 * 주문 항목을 나타내는 엔티티 클래스입니다.
 * 각 주문 항목은 하나의 상품(Item)과 연관되어 있으며, 주문(Order)의 일부로 관리됩니다.
 */
@Entity
@Getter
@Setter
public class OrderItem extends BaseEntity {

    /**
     * 주문 항목의 고유 식별자입니다. 데이터베이스에서 자동으로 생성됩니다.
     */
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    /**
     * 이 주문 항목과 연관된 상품을 나타냅니다.
     * 상품과 주문 항목 사이에는 다대일 관계가 설정되어 있습니다.
     */
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    /**
     * 이 주문 항목이 속한 주문을 나타냅니다.
     * 주문과 주문 항목 사이에는 다대일 관계가 설정되어 있으며, 지연 로딩(LAZY) 전략이 사용됩니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * 주문 시의 상품 가격입니다. 상품 가격은 주문이 생성될 때 결정됩니다.
     */
    private int orderPrice;

    /**
     * 주문된 상품의 수량입니다.
     */
    private int count;

    /**
     * 주문 항목을 생성하고 초기화하는 팩토리 메서드입니다.
     * 지정된 상품과 수량을 사용하여 새로운 주문 항목을 생성합니다.
     *
     * @param item 주문할 상품입니다.
     * @param count 주문할 상품의 수량입니다.
     * @return 초기화된 주문 항목을 반환합니다.
     */
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice());

        item.removeStock(count);
        return orderItem;
    }

    /**
     * 주문 항목의 총 가격을 계산합니다.
     * 주문 시의 상품 가격과 주문된 상품의 수량을 곱하여 총 가격을 계산합니다.
     *
     * @return 이 주문 항목의 총 가격을 반환합니다.
     */
    public int getTotalPrice(){
        return orderPrice * count;
    }
}
