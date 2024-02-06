package com.shop.shopmasterclone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 장바구니 항목 엔티티 클래스.
 * 각 장바구니 항목은 특정 장바구니에 속하며, 하나의 상품과 연결됩니다.
 * 이 클래스는 장바구니 항목의 상품, 수량 등의 정보를 관리합니다.
 */
@Entity
@Table(name="cart_item")
@Getter
@Setter
public class CartItem {
    /**
     * 장바구니 항목의 고유 식별자.
     * 데이터베이스에서 자동으로 생성됩니다.
     */
    @Id
    @GeneratedValue
    @Column(name="cart_item_id")
    private Long id;

    /**
     * 이 장바구니 항목이 속한 장바구니.
     * 장바구니와 장바구니 항목은 다대일 관계로 매핑됩니다.
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="cart_id")
    private Cart cart;

    /**
     * 이 장바구니 항목에 연결된 상품.
     * 상품과 장바구니 항목은 다대일 관계로 매핑됩니다.
     */
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    /**
     * 장바구니 항목의 수량.
     */
    private int count;

    /**
     * 장바구니, 상품, 수량을 받아 새로운 장바구니 항목 인스턴스를 생성하고 초기화하는 팩토리 메서드.
     *
     * @param cart 이 항목이 속할 장바구니.
     * @param item 이 항목에 연결된 상품.
     * @param count 이 항목의 수량.
     * @return 초기화된 장바구니 항목 인스턴스.
     */
    public static CartItem createCartItem(Cart cart, Item item, int count){
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    /**
     * 장바구니 항목의 수량을 증가시킵니다.
     *
     * @param count 이 항목의 수량에 추가할 값.
     */
    public void addCount(int count){
        this.count += count;
    }
}
