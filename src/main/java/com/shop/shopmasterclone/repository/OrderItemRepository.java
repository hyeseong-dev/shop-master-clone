package com.shop.shopmasterclone.repository;

import com.shop.shopmasterclone.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
