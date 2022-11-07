package com.yejin.exam.wbook.domain.cart.controller;

import com.yejin.exam.wbook.domain.cart.entity.CartItem;
import com.yejin.exam.wbook.domain.cart.service.CartService;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.product.entity.Product;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.request.Rq;
import com.yejin.exam.wbook.util.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final Rq rq;

    @GetMapping("/items")
    @PreAuthorize("isAuthenticated()")
    public String showItems(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member buyer = memberContext.getMember();

        List<CartItem> items = cartService.getItemsByBuyer(buyer);

        model.addAttribute("items", items);

        return "cart/items";
    }

    @PostMapping("/addItem/{productId}")
    @PreAuthorize("isAuthenticated()")
    public String addItem(@PathVariable long productId) {
        cartService.addItem(rq.getMember(), new Product((productId)));

        return rq.redirectToBackWithMsg("장바구니에 추가되었습니다.");
    }

    @PostMapping("/removeItem/{productId}")
    @PreAuthorize("isAuthenticated()")
    public String removeItem(@PathVariable long productId) {
        cartService.removeItem(rq.getMember(), new Product((productId)));

        return rq.redirectToBackWithMsg("장바구니에서 삭제되었습니다.");
    }

    @PostMapping("/removeItems")
    @PreAuthorize("isAuthenticated()")
    public String removeItems(String ids) {
        Member buyer = rq.getMember();

        String[] idsArr = ids.split(",");

        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    CartItem cartItem = cartService.findItemById(id).orElse(null);

                    if (cartService.actorCanDelete(buyer, cartItem)) {
                        cartService.removeItem(cartItem);
                    }
                });

        return "redirect:/cart/items?msg=" + Util.url.encode("%d건의 품목을 삭제하였습니다.".formatted(idsArr.length));
    }
}