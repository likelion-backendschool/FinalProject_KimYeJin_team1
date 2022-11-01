package com.yejin.exam.wbook.domain.order.repository;

import com.yejin.exam.wbook.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByBuyerIdOrderByIdDesc(long buyerId);
}