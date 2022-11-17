package com.yejin.exam.wbook.domain.cart.controller;

import com.yejin.exam.wbook.domain.cart.entity.CartItem;
import com.yejin.exam.wbook.domain.cart.service.CartService;
import com.yejin.exam.wbook.domain.member.entity.Member;
import com.yejin.exam.wbook.domain.product.entity.Product;
import com.yejin.exam.wbook.global.base.dto.MemberContext;
import com.yejin.exam.wbook.global.request.Rq;
import com.yejin.exam.wbook.global.result.ResultResponse;
import com.yejin.exam.wbook.util.Util;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Api(tags = "장바구니 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cart")
public class CartController {
    private final CartService cartService;
    private final Rq rq;
    @ApiOperation(value = "장바구니 리스트")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 장바구니 조회에 성공하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 장바구니 조회 할 수 없습니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @GetMapping("/items")
    public ResponseEntity<ResultResponse> showItems(@AuthenticationPrincipal MemberContext memberContext) {
        Member buyer = memberContext.getMember();
        List<CartItem> items = cartService.getItemsByBuyer(buyer);
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","장바구니 조회에 성공하였습니다", items));
    }
    @ApiOperation(value = "장바구니 추가")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 장바구니에 추가되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 이미 장바구니에 존재하는 항목입니다. 수량을 추가하세요."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "productId", value = "도서 PK", example = "1", required = true)
    @PostMapping("/addItem/{productId}")
    public ResponseEntity<ResultResponse> addItem(@PathVariable long productId) {
        cartService.addItem(rq.getMember(), new Product((productId)));
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","장바구니에 추가되었습니다.", productId));
    }
    @ApiOperation(value = "장바구니 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - 장바구니에서 삭제되었습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 존재하지 않는 상품입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "productId", value = "도서 PK", example = "1", required = true)
    @PostMapping("/removeItem/{productId}")
    public ResponseEntity<ResultResponse> removeItem(@PathVariable long productId) {
        boolean removed = cartService.removeItem(rq.getMember(), new Product((productId)));
        if(!removed){
            return Util.spring.responseEntityOf(ResultResponse.failOf("F001","존재하지 않는 상품입니다.", productId));
        }
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","장바구니에서 삭제되었습니다.", productId));
    }

    @ApiOperation(value = "장바구니 삭제")
    @ApiResponses({
            @ApiResponse(code = 200, message = "S001 - %d건의 품목을 삭제하였습니다."),
            @ApiResponse(code = 400, message = "FOO1 - 존재하지 않는 상품입니다."),
            @ApiResponse(code = 401, message = "M003 - 로그인이 필요한 화면입니다."),
    })
    @ApiImplicitParam(name = "ids", value = "도서 ids", example = "12", required = true)
    @PostMapping("/removeItems")
    public ResponseEntity<ResultResponse> removeItems(String ids) {
        Member buyer = rq.getMember();

        String[] idsArr = ids.split(",");
        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    Optional<CartItem> oCartItem = cartService.findItemById(id);
                    if(oCartItem.isPresent()){
                        CartItem cartItem = oCartItem.get();
                        if (cartService.actorCanDelete(buyer, cartItem)) {
                            cartService.removeItem(cartItem);
                        }
                    }
                });
        return Util.spring.responseEntityOf(ResultResponse.successOf("S001","%d건의 품목을 삭제하였습니다.".formatted(idsArr.length), idsArr.length));
    }
}