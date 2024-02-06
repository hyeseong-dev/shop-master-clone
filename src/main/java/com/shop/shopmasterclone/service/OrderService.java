package com.shop.shopmasterclone.service;

import com.shop.shopmasterclone.dto.OrderDto;
import com.shop.shopmasterclone.dto.OrderHistDto;
import com.shop.shopmasterclone.dto.OrderItemDto;
import com.shop.shopmasterclone.entity.*;
import com.shop.shopmasterclone.exception.OutofStockException;
import com.shop.shopmasterclone.repository.ItemImgRepository;
import com.shop.shopmasterclone.repository.ItemRepository;
import com.shop.shopmasterclone.repository.MemberRepository;
import com.shop.shopmasterclone.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 주문 관련 서비스를 제공하는 클래스입니다.
 * 주문 생성, 주문 내역 조회, 주문 취소 등 주문과 관련된 비즈니스 로직을 처리합니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    /**
     * 주문이 현재 로그인한 사용자에 의해 생성되었는지 검증합니다.
     *
     * @param orderId 검증할 주문의 식별자입니다.
     * @param email 현재 로그인한 사용자의 이메일 주소입니다.
     * @return 주문이 사용자에 의해 생성되었으면 true, 그렇지 않으면 false를 반환합니다.
     * @throws EntityNotFoundException 주어진 식별자로 주문을 찾을 수 없을 때 발생합니다.
     */
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }
        return true;
    }

    /**
     * 주문을 취소합니다. 주문의 상태를 취소로 변경하고, 관련 주문 항목의 재고를 복구합니다.
     *
     * @param orderId 취소할 주문의 식별자입니다.
     * @throws EntityNotFoundException 주어진 식별자로 주문을 찾을 수 없을 때 발생합니다.
     */
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    /**
     * 새로운 주문을 생성합니다.
     *
     * @param orderDto 주문 생성에 필요한 데이터를 담고 있는 DTO 객체입니다.
     * @param email 주문을 생성하는 사용자의 이메일 주소입니다.
     * @return 생성된 주문의 식별자를 반환합니다.
     * @throws EntityNotFoundException 주어진 식별자로 상품을 찾을 수 없을 때 발생합니다.
     * @throws OutofStockException 주문하려는 상품의 재고가 부족할 때 발생합니다.
     */
    public Long order(OrderDto orderDto, String email){
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        if (item.getStockNumber() < orderDto.getCount()) {
            throw new OutofStockException("Out of stock");
        }

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 사용자의 주문 내역을 페이지 단위로 조회합니다.
     *
     * @param email 주문 내역을 조회할 사용자의 이메일 주소입니다.
     * @param pageable 페이징 처리 정보를 담고 있는 Pageable 객체입니다.
     * @return 주문 내역의 페이지 정보를 담고 있는 Page<OrderHistDto> 객체를 반환합니다.
     */
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable){
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for(Order order : orders){
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for(OrderItem orderItem : orderItems){
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }
}
