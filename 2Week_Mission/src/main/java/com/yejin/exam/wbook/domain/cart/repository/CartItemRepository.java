package com.yejin.exam.wbook.domain.cart.repository;

import com.yejin.exam.wbook.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}