package com.shop.shopmasterclone.repository;

import com.shop.shopmasterclone.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CartItem 데이터에 접근하기 위한 Spring Data JPA 리포지토리 인터페이스.
 * 이 인터페이스는 {@link CartItem} 엔티티에 대한 영속성 관리와 기본 CRUD 작업을 제공합니다.
 * 추가적으로, 특정 장바구니 ID와 상품 ID에 대한 장바구니 항목을 조회하는 사용자 정의 메서드를 포함합니다.
 */
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * 주어진 장바구니 ID와 상품 ID에 해당하는 장바구니 항목을 조회합니다.
     *
     * @param cartId 조회할 장바구니의 고유 식별자.
     * @param itemId 조회할 상품의 고유 식별자.
     * @return 해당 조건에 맞는 {@link CartItem} 인스턴스. 만약 찾을 수 없으면 null을 반환할 수 있습니다.
     */
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);
}
