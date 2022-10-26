package com.yejin.exam.wbook.domain.cart.service;

import com.yejin.exam.wbook.domain.cart.entity.CartItem;
import com.yejin.exam.wbook.domain.cart.repository.CartItemRepository;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;

    public void addItem(Member member, Product product, int quantity) {
        CartItem cartItem = CartItem.builder()
                .member(member)
                .product(product)
                .quantity(quantity)
                .build();

        cartItemRepository.save(cartItem);
    }
}