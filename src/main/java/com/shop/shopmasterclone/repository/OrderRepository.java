package com.shop.shopmasterclone.repository;

import com.shop.shopmasterclone.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
