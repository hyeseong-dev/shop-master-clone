package com.shop.shopmasterclone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * 장바구니 엔티티 클래스.
 * 각 장바구니는 한 명의 회원에게 속하며, 회원과 1:1 관계를 가집니다.
 * 이 클래스는 장바구니의 기본적 인 정보를 관리합니다.
 */
@Entity
@Table(name="cart")
@Getter
@Setter
public class Cart {
    /**
     * 장바구니의 고유 식별자.
     * 데이터베이스에서 자동으로 생성됩니다.
     */
    @Id
    @Column(name="cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 이 장바구니를 소유한 회원.
     * 장바구니와 회원은 1:1 관계로 매핑됩니다.
     */
    @OneToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    /**
     * 회원을 받아 새 장바구니 인스턴스를 생성하고 초기화하는 팩토리 메서드.
     *
     * @param member 장바구니를 생성할 회원.
     * @return 초기화된 장바구니 인스턴스.
     */
    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }
}
