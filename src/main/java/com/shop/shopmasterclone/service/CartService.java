package com.shop.shopmasterclone.service;

import com.shop.shopmasterclone.dto.CartItemDto;
import com.shop.shopmasterclone.entity.Cart;
import com.shop.shopmasterclone.entity.CartItem;
import com.shop.shopmasterclone.entity.Item;
import com.shop.shopmasterclone.entity.Member;
import com.shop.shopmasterclone.repository.CartItemRepository;
import com.shop.shopmasterclone.repository.CartRepository;
import com.shop.shopmasterclone.repository.ItemRepository;
import com.shop.shopmasterclone.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 장바구니 관련 서비스를 제공하는 클래스입니다.
 * 이 서비스는 장바구니 항목 추가, 장바구니 조회, 장바구니 항목 수정 및 삭제 등의 기능을 수행합니다.
 * {@link org.springframework.stereotype.Service} 어노테이션을 사용하여 스프링 컨텍스트에 빈으로 등록됩니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * 사용자의 장바구니에 상품을 추가하거나, 이미 존재하는 상품의 수량을 업데이트하는 메서드입니다.
     *
     * 프로세스는 다음과 같습니다:
     * 1. 전달받은 cartItemDto에서 상품 ID를 사용하여 상품 엔티티를 조회합니다. 해당 상품이 존재하지 않을 경우, EntityNotFoundException을 발생시킵니다.
     * 2. 사용자의 이메일 주소를 기반으로 회원 엔티티를 조회합니다.
     * 3. 해당 회원의 장바구니를 조회합니다. 만약 장바구니가 존재하지 않는 경우, 새로운 장바구니를 생성하고 저장합니다.
     * 4. 장바구니에 이미 동일한 상품이 존재하는지 확인합니다. 존재하는 경우, 해당 장바구니 항목의 수량을 cartItemDto에 지정된 수량만큼 증가시킵니다.
     * 5. 동일한 상품이 장바구니에 존재하지 않는 경우, 새로운 장바구니 항목을 생성하여 지정된 수량과 함께 장바구니에 추가합니다.
     *
     * 이 메서드는 장바구니 항목의 ID를 반환합니다. 이 ID는 추가 또는 업데이트된 장바구니 항목을 식별하는 데 사용됩니다.
     *
     * @param cartItemDto 장바구니에 추가하려는 상품의 정보와 수량을 담고 있는 DTO 객체입니다.
     * @param email 상품을 추가하려는 사용자의 이메일 주소입니다. 이 주소는 사용자를 식별하는 데 사용됩니다.
     * @return 추가 또는 업데이트된 장바구니 항목의 ID를 반환합니다.
     * @throws EntityNotFoundException 상품이나 회원 정보를 데이터베이스에서 찾을 수 없을 경우 발생합니다.
     */
    public Long addCart(CartItemDto cartItemDto, String email){
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());

        if(cart == null){
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        if(savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }
}
