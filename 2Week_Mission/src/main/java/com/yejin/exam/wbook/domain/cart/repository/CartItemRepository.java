package com.yejin.exam.wbook.domain.cart.repository;

import com.yejin.exam.wbook.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByMemberIdAndProductOptionId(long memberId, long productOptionId);
    List<CartItem> findAllByMemberId(Long memberId);
    boolean existsByMemberIdAndProductId(long memberId, long productId);

    Optional<CartItem> findByMemberIdAndProductId(long buyerId, long productId);
}