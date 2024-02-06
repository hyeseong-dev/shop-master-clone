package com.shop.shopmasterclone.repository;

import com.shop.shopmasterclone.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 장바구니 데이터에 접근하기 위한 Spring Data JPA 리포지토리 인터페이스.
 * 이 인터페이스는 {@link Cart} 엔티티의 영속성 관리와 기본 CRUD 작업을 위한 메서드를 제공합니다.
 * 추가적으로, 회원 ID를 기반으로 장바구니를 조회하는 사용자 정의 메서드를 포함합니다.
 */
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * 주어진 회원 ID에 해당하는 장바구니를 조회합니다.
     *
     * @param memberId 조회할 회원의 고유 식별자.
     * @return 해당 회원 ID에 매칭되는 {@link Cart} 인스턴스. 만약 해당 회원 ID로 장바구니를 찾을 수 없으면 null을 반환합니다.
     */
    Cart findByMemberId(Long memberId);
}
