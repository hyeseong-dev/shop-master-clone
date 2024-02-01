package com.shop.shopmasterclone.entity;

import com.shop.shopmasterclone.constant.ItemSellStatus;
import com.shop.shopmasterclone.repository.ItemRepository;
import com.shop.shopmasterclone.repository.MemberRepository;
import com.shop.shopmasterclone.repository.OrderItemRepository;
import com.shop.shopmasterclone.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    @DisplayName("주문 항목 조회 시 주문은 지연 로딩되어야 함")
    public void givenOrderItemId_whenFindById_thenOrderShouldBeLazyLoaded() {
        // Given
        Order order = this.createOrder();   //새로운 Order와 OrderItem을 생성하고 저장
        Long orderItemId = order.getOrderItems().get(0).getId();

        // 영속성 컨텍스트를 초기화합니다.
        // 이는 OrderItem 조회 시 새로운 영속성 컨텍스트 세션에서 수행되도록 함으로써 테스트의 독립성을 보장함
        entityManager.flush();
        entityManager.clear();


        // When
        //  지연 로딩이 작동하고 있음을 확인합니다. 이 시점에서 Order는 초기화되지 않아야 합니다.
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);

        // Then
        assertFalse(Hibernate.isInitialized(orderItem.getOrder()));

        // 실제 Order 데이터에 접근
        orderItem.getOrder().getOrderDate();

        // 이제 Order가 초기화되었는지 확인
        // Order가 이제 초기화되었음을 확인
        assertTrue(Hibernate.isInitialized(orderItem.getOrder()));
    }

    public Order createOrder(){
        Order order = new Order();

        for(int i=0; i<3; i++){
            Item item = createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = this.createOrder();
        order.getOrderItems().remove(0);
        entityManager.flush();
    }

    public Item createItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest(){
        Order order = new Order();

        for(int i=0; i<3; i++){
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setCount(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }
        orderRepository.saveAndFlush(order);
        entityManager.clear();

        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size());
    }
}