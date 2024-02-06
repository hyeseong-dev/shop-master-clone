package com.shop.shopmasterclone.service;

import com.shop.shopmasterclone.constant.ItemSellStatus;
import com.shop.shopmasterclone.dto.CartItemDto;
import com.shop.shopmasterclone.entity.CartItem;
import com.shop.shopmasterclone.entity.Item;
import com.shop.shopmasterclone.entity.Member;
import com.shop.shopmasterclone.repository.CartItemRepository;
import com.shop.shopmasterclone.repository.ItemRepository;
import com.shop.shopmasterclone.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link CartService}에 대한 테스트 클래스입니다.
 * 장바구니 서비스의 주요 기능을 검증하기 위한 단위 테스트를 포함합니다.
 */
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CartServiceTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemRepository cartItemRepository;

    private Item createTestItem() {
        Item item = new Item();
        item.setItemNm("Test Product");
        item.setPrice(10000);
        item.setItemDetail("Test Product Description");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    private Member createTestMember() {
        Member member = new Member();
        member.setEmail("test@example.com");
        return memberRepository.save(member);
    }

    /**
     * 장바구니에 아이템을 성공적으로 추가하는 시나리오를 테스트합니다.
     *
     * <p>시나리오:</p>
     * <ul>
     * <li>Given: 테스트용 상품과 회원이 생성되어 있음</li>
     * <li>When: 특정 상품을 장바구니에 추가하려고 함</li>
     * <li>Then: 해당 상품이 장바구니에 성공적으로 추가되어야 함</li>
     * </ul>
     */
    @Test
    @DisplayName("장바구니에 아이템 담기")
    void shouldAddItemToCart() {
        // 테스트 환경 설정 (Given)
        Item item = createTestItem();
        Member member = createTestMember();

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCount(5);
        cartItemDto.setItemId(item.getId());

        // 테스트 대상 실행 (When)
        Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());

        // 결과 검증 (Then)
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found with id: " + cartItemId));

        assertEquals(item.getId(), cartItem.getItem().getId(), "장바구니에 추가된 상품 ID가 일치해야 합니다.");
        assertEquals(cartItemDto.getCount(), cartItem.getCount(), "장바구니에 추가된 상품의 수량이 요청된 수량과 일치해야 합니다.");
    }
}
