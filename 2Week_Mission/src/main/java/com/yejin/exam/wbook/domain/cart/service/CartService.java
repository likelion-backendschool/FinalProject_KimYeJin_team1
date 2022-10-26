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

    public CartItem addItem(Member member, Product product, int quantity) {
        CartItem oldCartItem = cartItemRepository.findByMemberIdAndProductOptionId(member.getId(), product.getId()).orElse(null);

        if ( oldCartItem != null ) {
            oldCartItem.setQuantity(oldCartItem.getQuantity() + quantity);
            cartItemRepository.save(oldCartItem);

            return oldCartItem;
        }
        CartItem cartItem = CartItem.builder()
                .member(member)
                .product(product)
                .quantity(quantity)
                .build();

        cartItemRepository.save(cartItem);
        return cartItem;
    }
}