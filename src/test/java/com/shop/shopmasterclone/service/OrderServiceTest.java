package com.shop.shopmasterclone.service;

import com.shop.shopmasterclone.constant.ItemSellStatus;
import com.shop.shopmasterclone.constant.OrderStatus;
import com.shop.shopmasterclone.dto.ItemFormDto;
import com.shop.shopmasterclone.dto.MemberFormDto;
import com.shop.shopmasterclone.dto.OrderDto;
import com.shop.shopmasterclone.entity.Item;
import com.shop.shopmasterclone.entity.Member;
import com.shop.shopmasterclone.entity.Order;
import com.shop.shopmasterclone.exception.OutofStockException;
import com.shop.shopmasterclone.repository.ItemRepository;

import com.shop.shopmasterclone.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class OrderServiceTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("아이템의 재고가 주문 취소 후 복구되는지 검증")
    public void shouldRestoreItemStockWhenOrderIsCancelled() throws Exception{
        // Given: 주문을 위한 환경 준비
        String email = "test@example.com";
        Member member = createTestMember(email);
        memberService.saveMember(member);
        Long itemId = createTestItem("테스트 아이템", 10);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found for id: " + itemId));

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());
        Long orderId = orderService.order(orderDto, email);

        // When: 주문 취소 실행
        orderService.cancelOrder(orderId);

        // Then: 주문 상태와 아이템 재고 수량 검증
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(OrderStatus.CANCEL, order.getOrderStatus(), "주문 상태는 취소되어야 합니다.");
        assertEquals(10, item.getStockNumber(), "주문 취소 후 아이템의 재고 수량은 복구되어야 합니다.");
    }

    private Member createTestMember(String email) {
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setName("테스트 멤버");
        memberFormDto.setEmail(email);
        memberFormDto.setPassword("testPassword");
        memberFormDto.setAddress("testAddress");
        return Member.createMember(memberFormDto, new BCryptPasswordEncoder());
    }

    private Long createTestItem(String itemName, int stockNumber) throws Exception {
        ItemFormDto itemFormDto = new ItemFormDto();
        itemFormDto.setItemNm(itemName);
        itemFormDto.setItemDetail(itemName + " 상세 설명");
        itemFormDto.setPrice(10000);
        itemFormDto.setStockNumber(stockNumber);
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
        MultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());
        List<MultipartFile> itemImgFileList = new ArrayList<>();
        itemImgFileList.add(file);
        return itemService.saveItem(itemFormDto, itemImgFileList);
    }

    /**
     * 주문 생성 테스트 - 주문이 정상적으로 생성될 때.
     *
     * <p>Given: 충분한 재고를 가진 상품과 유효한 회원이 주어졌을 때,
     * <p>When: 주문 생성 요청이 들어오면,
     * <p>Then: 주문이 성공적으로 생성되어야 하며, 주문된 상품의 재고가 줄어들어야 한다.
     */
    @Test
    @DisplayName("주문 생성 테스트 - 주문이 정상적으로 생성될 때")
    void testOrderCreationWithSufficientStock() throws Exception {
        // Given
        String email = "test@email.com";
        Member member = createTestMember(email);
        memberService.saveMember(member);
        Long itemId = createTestItem("테스트 아이템", 100);

        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(itemId);
        orderDto.setCount(2);

        // When
        Long orderId = orderService.order(orderDto, email);

        // Then
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문을 찾을 수 없습니다."));
        Item orderedItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        assertAll(
                () -> assertNotNull(order, "주문은 null이 아니어야 합니다."),
                () -> assertEquals(98, orderedItem.getStockNumber(), "주문된 수량만큼 재고가 감소해야 합니다."),
                () -> assertEquals(email, order.getMember().getEmail(), "주문은 올바른 멤버에게 속해야 합니다."),
                () -> assertTrue(order.getOrderItems().stream()
                                .anyMatch(oi -> oi.getItem().getId().equals(itemId) && oi.getCount() == 2),
                        "주문에는 올바른 상품과 수량이 포함되어야 합니다.")
        );
    }

    /**
     * 상품 재고가 부족할 때 주문 생성 실패 테스트.
     *
     * <p>Given: 재고보다 많은 수량을 주문하려는 상황,
     * <p>When: 주문 생성 요청을 하면,
     * <p>Then: OutofStockException 예외가 발생해야 한다.
     */
    @Test
    @DisplayName("상품 재고가 부족할 때 주문 생성 실패")
    void shouldFailToCreateOrderWithInsufficientStock() throws Exception {
        // Given
        String email = "test@example.com";
        memberService.saveMember(createTestMember(email));
        Long itemId = createTestItem("재고 부족 상품", 1); // 재고 설정

        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(itemId);
        orderDto.setCount(2); // 요청 수량이 재고보다 많음

        // When & Then
        assertThrows(OutofStockException.class,
                () -> orderService.order(orderDto, email),
                "재고가 부족할 때는 OutofStockException이 발생해야 합니다."
        );
    }


    /**
     * 주문 생성 시 상품을 찾을 수 없을 때 예외 발생 테스트.
     *
     * <p>Given: 존재하지 않는 상품 ID로 주문을 시도하는 상황,
     * <p>When: 주문 생성 요청을 하면,
     * <p>Then: EntityNotFoundException 예외가 발생해야 한다.
     */
    @Test
    @DisplayName("주문 생성 시 상품을 찾을 수 없을 때 예외 발생")
    void shouldThrowExceptionWhenItemNotFoundWhileCreatingOrder() throws Exception {
        // Given
        String email = "test@example.com";
        memberService.saveMember(createTestMember(email));

        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(9999L); // 존재하지 않는 상품 ID
        orderDto.setCount(1);

        // When & Then
        assertThrows(EntityNotFoundException.class,
                () -> orderService.order(orderDto, email),
                "존재하지 않는 상품으로 주문을 시도할 때는 EntityNotFoundException이 발생해야 합니다."
        );
    }

}
