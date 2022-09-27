package com.andrew.wechetshop.controller;

import com.andrew.wechetshop.api.entity.PagedResponse;
import com.andrew.wechetshop.entity.Response;
import com.andrew.wechetshop.generate.Shop;
import com.andrew.wechetshop.service.ShopService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1")
public class ShopController {
    private final ShopService shopService;

    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PatchMapping("/shop/{id}")
    public Response<Shop> updateShop(@PathVariable Long id, @RequestBody Shop shop) {
        shop.setId(id);
        return Response.of(shopService.updateShop(shop));
    }

    @PostMapping("/shop")
    public Response<Shop> createShop(@RequestBody Shop shop, HttpServletResponse response) {
        response.setStatus(HttpStatus.CREATED.value());
        return Response.of(shopService.createShop(shop));
    }

    @DeleteMapping("/shop/{id}")
    public Response<Shop> deleteShop(@PathVariable Long id, HttpServletResponse response) {
        response.setStatus(HttpStatus.NO_CONTENT.value());
        return Response.of(shopService.deleteShop(id));
    }

    @GetMapping("/shop")
    public PagedResponse<Shop> getCurrentUsersShop(Integer pageNum, Integer pageSize) {
        return shopService.getCurrentUsersShop(pageNum, pageSize);
    }
}
