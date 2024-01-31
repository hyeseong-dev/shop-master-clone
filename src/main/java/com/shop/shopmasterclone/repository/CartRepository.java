package com.shop.shopmasterclone.repository;

import com.shop.shopmasterclone.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
