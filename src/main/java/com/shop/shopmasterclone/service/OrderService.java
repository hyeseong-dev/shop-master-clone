package com.shop.shopmasterclone.service;

import com.shop.shopmasterclone.dto.OrderDto;
import com.shop.shopmasterclone.entity.Item;
import com.shop.shopmasterclone.entity.Member;
import com.shop.shopmasterclone.entity.Order;
import com.shop.shopmasterclone.entity.OrderItem;
import com.shop.shopmasterclone.exception.OutofStockException;
import com.shop.shopmasterclone.repository.ItemRepository;
import com.shop.shopmasterclone.repository.MemberRepository;
import com.shop.shopmasterclone.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

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
}
