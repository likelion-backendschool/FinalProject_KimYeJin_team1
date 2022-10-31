package com.yejin.exam.wbook.domain.order.repository;

import com.yejin.exam.wbook.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}