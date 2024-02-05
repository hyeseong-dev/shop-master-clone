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
import java.util.ArrayList;
import java.util.List;

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
    @DisplayName("새로운 주문이 생성되면, 주문에 포함된 모든 항목의 총 가격이 정확히 계산되어야 한다")
    void givenNewOrderWithOrderItems_whenCalculateTotalPrice_thenTotalPriceShouldBeAccuratelyCalculated() {
        // Given: 새로운 주문과 주문 항목들을 생성한다
        Order order = createOrderWithItems(3, 10000, 2);

        // When: 주문의 총 가격을 계산한다
        int totalPrice = order.getTotalPrice();

        // Then: 주문에 포함된 모든 항목의 총 가격이 정확히 계산되어야 한다
        assertEquals(60000, totalPrice, "주문의 총 가격이 주문 항목들의 가격 합계와 일치해야 한다");
    }

    @Test
    @DisplayName("주문 항목이 주문에 추가될 때, 해당 항목은 주문 객체에 정확히 연결되어야 한다")
    void givenOrderAndOrderItem_whenAddOrderItem_thenOrderItemShouldBeProperlyLinkedToOrder() {
        // Given: 새로운 주문 객체를 생성한다
        Order order = new Order();
        Item item = createItem();
        OrderItem orderItem = OrderItem.createOrderItem(item, 10);

        // When: 주문 항목을 주문에 추가한다
        order.addOrderItem(orderItem);

        // Then: 추가된 주문 항목은 주문 객체에 정확히 연결되어야 한다
        assertTrue(order.getOrderItems().contains(orderItem), "주문 항목이 주문에 정확히 추가되어야 한다");
        assertEquals(order, orderItem.getOrder(), "주문 항목의 주문 객체가 올바르게 설정되어야 한다");
    }

    @Test
    @DisplayName("회원과 주문 항목 리스트를 사용하여 새로운 주문이 생성되면, 주문은 주문 항목들을 포함하고 회원과 연결되어야 한다")
    void givenMemberAndOrderItemList_whenCreateOrder_thenOrderShouldContainOrderItemsAndBeLinkedToMember() {
        // Given: 회원과 주문 항목 리스트를 준비한다
        Member member = new Member();
        memberRepository.save(member);
        List<OrderItem> orderItemList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Item item = createItem();
            itemRepository.save(item);
            OrderItem orderItem = OrderItem.createOrderItem(item, 2);
            orderItemList.add(orderItem);
        }

        // When: 새로운 주문을 생성한다
        Order order = Order.createOrder(member, orderItemList);

        // Then: 생성된 주문은 주문 항목들을 포함하고 회원과 연결되어야 한다
        assertFalse(order.getOrderItems().isEmpty(), "주문은 주문 항목들을 포함해야 한다");
        assertEquals(3, order.getOrderItems().size(), "주문 항목의 개수가 정확해야 한다");
        assertEquals(member, order.getMember(), "주문은 주문을 생성한 회원과 연결되어야 한다");
    }

    private Order createOrderWithItems(int itemCount, int price, int count) {
        Order order = new Order();
        Member member = new Member();
        memberRepository.save(member);
        order.setMember(member);

        for (int i = 0; i < itemCount; i++) {
            Item item = createItem();
            item.setPrice(price);
            itemRepository.save(item);
            OrderItem orderItem = OrderItem.createOrderItem(item, count);
            order.addOrderItem(orderItem);
        }
        orderRepository.save(order);
        return order;
    }

    // 테스트 코드 수행: OrderItem 클래스의 order 필드의 ManyToOne어노테이션의 fetch 매개변수의 값을 EARGER로 변경시켜야 함.
//    @Test
//    @DisplayName("주문 항목 조회 시 주문은 즉시 로딩되어야 함")
//    public void givenOrderItemId_whenFindById_thenOrderShouldBeEagerlyLoaded() {
//        // Given
//        Order order = this.createOrder();
//        Long orderItemId = order.getOrderItems().get(0).getId();
//        entityManager.flush();
//        entityManager.clear();
//
//        // When
//        OrderItem orderItem = orderItemRepository.findById(orderItemId)
//                .orElseThrow(EntityNotFoundException::new);
//
//        // Then
//        assertTrue(Hibernate.isInitialized(orderItem.getOrder()));
//    }

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
    @DisplayName("새로운 주문 생성 시 주문 항목이 정상적으로 추가되고, 고아 객체 제거가 작동해야 함")
    void whenCreatingNewOrder_thenOrderItemsShouldBeAddedAndOrphanRemovalShouldWork() {
        // Given: 새로운 주문과 주문 항목을 생성
        Order order = createOrder();

        // When: 주문 항목 중 하나를 제거하고 데이터베이스를 갱신
        int originalSize = order.getOrderItems().size();
        order.getOrderItems().remove(0);
        entityManager.flush();
        entityManager.clear();

        // Then: 제거된 주문 항목은 데이터베이스에서도 삭제되어야 함
        Order updatedOrder = orderRepository.findById(order.getId()).orElseThrow(EntityNotFoundException::new);
        assertEquals(originalSize - 1, updatedOrder.getOrderItems().size());
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
    @DisplayName("주문과 주문 항목 사이의 영속성 전이가 정상 작동해야 함")
    void whenSavingOrder_thenOrderItemsShouldBePersistedThroughCascade() {
        // Given: 새로운 주문과 주문 항목을 생성
        Order order = createOrder();

        // When: 주문을 저장하고 영속성 컨텍스트를 초기화
        orderRepository.saveAndFlush(order);
        entityManager.clear();

        // Then: 주문과 함께 주문 항목들도 데이터베이스에 저장되어야 함
        Order savedOrder = orderRepository.findById(order.getId()).orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size());
    }
}