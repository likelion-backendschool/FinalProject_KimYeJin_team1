package com.yejin.exam.wbook.domain.cart.controller;

import com.yejin.exam.wbook.domain.cart.entity.CartItem;
import com.yejin.exam.wbook.domain.cart.service.CartService;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.product.entity.Product;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.request.Rq;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.*;
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
@Api(tags = "장바구니 API")
@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final Rq rq;

    @GetMapping("/items")
    public String showItems(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member buyer = memberContext.getMember();

        List<CartItem> items = cartService.getItemsByBuyer(buyer);

        model.addAttribute("items", items);

        return "cart/items";
    }
    @ApiOperation(value = "장바구니 추가")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 장바구니에 추가되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 이미 장바구니에 존재하는 항목입니다. 수량을 추가하세요."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "productId", value = "도서 PK", example = "1", required = true)
    @PostMapping("/addItem/{productId}")
    public String addItem(@PathVariable long productId) {
        cartService.addItem(rq.getMember(), new Product((productId)));

        return rq.redirectToBackWithMsg("장바구니에 추가되었습니다.");
    }
    @ApiOperation(value = "장바구니 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 장바구니에서 삭제되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 존재하지 않는 상품입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "productId", value = "도서 PK", example = "1", required = true)
    @PostMapping("/removeItem/{productId}")
    public String removeItem(@PathVariable long productId) {
        cartService.removeItem(rq.getMember(), new Product((productId)));

        return rq.redirectToBackWithMsg("장바구니에서 삭제되었습니다.");
    }
    @ApiOperation(value = "장바구니 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d건의 품목을 삭제하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 존재하지 않는 상품입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "ids", value = "도서 ids", example = "12", required = true)
    @PostMapping("/removeItems")
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