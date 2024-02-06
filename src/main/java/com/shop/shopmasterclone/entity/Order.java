package com.shop.shopmasterclone.entity;

import com.shop.shopmasterclone.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 고객의 주문을 나타내는 엔티티 클래스입니다.
 * 각 주문은 여러 주문 항목({@link OrderItem})을 포함할 수 있으며, 하나의 회원({@link Member})에 속합니다.
 * 주문은 주문 상태({@link OrderStatus}), 주문 날짜 등의 정보를 관리합니다.
 */
@Entity
@Table(name="orders")
@Getter
@Setter
public class Order extends BaseEntity{

    /**
     * 주문의 고유 식별자입니다. 데이터베이스에서 자동으로 생성됩니다.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 이 주문을 생성한 회원입니다. 회원과 주문 사이에는 다대일 관계가 설정되어 있습니다.
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    /**
     * 주문이 생성된 날짜와 시간입니다.
     */
    private LocalDateTime orderDate;

    /**
     * 주문의 현재 상태를 나타냅니다. (예: 주문됨, 취소됨)
     */
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    /**
     * 이 주문과 관련된 주문 항목의 목록입니다.
     * 주문과 주문 항목 사이에는 일대다 관계가 설정되어 있으며, 주문 항목은 주문에 종속됩니다.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch=FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    /**
     * 주문에 주문 항목을 추가합니다.
     * 추가된 주문 항목은 이 주문에 속하게 됩니다.
     *
     * @param orderItem 이 주문에 추가할 주문 항목입니다.
     */
    public void addOrderItem(OrderItem orderItem){
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    /**
     * 주문을 생성하고 초기화하는 팩토리 메서드입니다.
     * 주문을 생성할 회원과 주문 항목 목록을 인자로 받아, 새로운 주문을 생성합니다.
     *
     * @param member 주문을 생성하는 회원입니다.
     * @param orderItemList 주문에 포함될 주문 항목의 목록입니다.
     * @return 초기화된 주문 객체를 반환합니다.
     */
    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member);
        for(OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    /**
     * 주문에 포함된 모든 주문 항목의 총 가격을 계산합니다.
     * 각 주문 항목의 총 가격을 합산하여, 주문의 총 가격을 구합니다.
     *
     * @return 이 주문의 총 가격을 반환합니다.
     */
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : this.orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    /**
     * 주문을 취소합니다. 주문 상태를 취소로 변경하고, 주문에 포함된 모든 주문 항목도 취소 처리합니다.
     *
     * 이 메서드는 주문 상태를 {@link OrderStatus#CANCEL}로 변경하여 주문이 취소되었음을 나타냅니다.
     * 또한, 주문에 포함된 각 주문 항목에 대해 {@link OrderItem#cancel()} 메서드를 호출하여,
     * 각 항목의 상태 또한 취소로 변경합니다. 이 과정에서 주문 항목에 설정된 재고 수량은
     * 원래대로 복구됩니다.
     */
    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;

        for(OrderItem orderItem: this.orderItems){
            orderItem.cancel();
        }
    }
}
