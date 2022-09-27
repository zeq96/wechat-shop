package com.andrew.wechetshop.controller;

import com.andrew.wechetshop.api.entity.PagedResponse;
import com.andrew.wechetshop.entity.Response;
import com.andrew.wechetshop.entity.ShoppingCartData;
import com.andrew.wechetshop.entity.ShoppingCartRequest;
import com.andrew.wechetshop.service.ShoppingCartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping("/shoppingCart")
    public PagedResponse<ShoppingCartData> getShoppingCartByCurrentUser(Integer pageNum, Integer pageSize) {
        return shoppingCartService.getShoppingCartByCurrentUser(pageNum, pageSize);
    }

    @PostMapping("/shoppingCart")
    public Response<ShoppingCartData> addShoppingCart(@RequestBody ShoppingCartRequest shoppingCartRequest) {
        return Response.of(shoppingCartService.addShoppingCart(shoppingCartRequest));
    }

    @DeleteMapping("/shoppingCart/{goodsId}")
    public Response<ShoppingCartData> deleteGoodsInShoppingCart(@PathVariable Long goodsId) {
        return Response.of(shoppingCartService.deleteGoodsInShoppingCart(goodsId));
    }
}
