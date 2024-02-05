package com.shop.shopmasterclone.service;

import com.shop.shopmasterclone.dto.ItemFormDto;
import com.shop.shopmasterclone.dto.OrderDto;
import com.shop.shopmasterclone.entity.Item;
import com.shop.shopmasterclone.entity.Member;
import com.shop.shopmasterclone.entity.Order;
import com.shop.shopmasterclone.exception.OutofStockException;
import com.shop.shopmasterclone.repository.ItemRepository;
import com.shop.shopmasterclone.repository.MemberRepository;
import com.shop.shopmasterclone.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

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
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("주문 생성 테스트 - 주문이 정상적으로 생성될 때")
    void testOrderCreationWithSufficientStock() {
        // Given
        Long itemId = 1L;
        String email = "test@email.com";
        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(itemId);
        orderDto.setCount(2);

        Item item = new Item();
        item.setId(itemId);
        item.setStockNumber(3); // Ensure sufficient stock for the test

        Member member = new Member();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(memberRepository.findByEmail(email)).thenReturn(member);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L); // Assign a dummy ID for the saved order
            return savedOrder;
        });

        // When
        Long orderId = orderService.order(orderDto, email);

        // Then
        assertNotNull(orderId);
        assertEquals(1L, orderId); // Assuming the assigned dummy ID is 1
        verify(itemRepository, times(1)).findById(itemId);
        verify(memberRepository, times(1)).findByEmail(email);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("상품 재고가 부족할 때 주문 생성 실패")
    void shouldFailToCreateOrderWithInsufficientStock() {
        // Given
        Long itemId = 1L;
        String email = "test@email.com";
        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(itemId);
        orderDto.setCount(2);

        // Ensure sufficient stock for the test
        ItemFormDto itemFormDto = new ItemFormDto();
        itemFormDto.setStockNumber(2);

        // Save the item with sufficient stock to the database
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        // Fetch a member from the database or create one as needed
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            member = new Member();
            member.setEmail(email);
            memberRepository.save(member);
        }

        // When and Then
        assertThrows(OutofStockException.class, () -> orderService.order(orderDto, email));
    }

    @Test
    @DisplayName("주문 생성 시 상품을 찾을 수 없을 때 예외 발생")
    void shouldThrowExceptionWhenItemNotFoundWhileCreatingOrder() {
        // Given
        Long itemId = 1L;
        String email = "test@email.com";
        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(itemId);
        orderDto.setCount(2);

        // Mock itemRepository to return empty, simulating item not found
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        // When and Then
        assertThrows(EntityNotFoundException.class, () -> orderService.order(orderDto, email));
        verify(itemRepository, times(1)).findById(itemId);
        verify(memberRepository, never()).findByEmail(email);
        verify(orderRepository, never()).save(any(Order.class));
    }
}
