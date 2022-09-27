package com.andrew.wechetshop.controller;

import com.andrew.wechetshop.api.entity.PagedResponse;
import com.andrew.wechetshop.entity.Response;
import com.andrew.wechetshop.generate.Goods;
import com.andrew.wechetshop.service.GoodsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
@RequestMapping("/api/v1")
public class GoodsController {
    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @GetMapping("/goods")
    public PagedResponse<Goods> getGoods(Integer pageNum, Integer pageSize, Integer shopId) {
        return goodsService.getGoodsLimit(pageNum, pageSize, shopId);
    }

    @PostMapping("/goods")
    public Response<Goods> createGoods(@RequestBody Goods goods, HttpServletResponse httpServletResponse) {
        cleanGoods(goods);
        httpServletResponse.setStatus(HttpServletResponse.SC_CREATED);
        return Response.of(goodsService.createGoods(goods));
    }

    @DeleteMapping("/goods/{id}")
    public Response<Goods> deleteGoods(@PathVariable Long id, HttpServletResponse httpServletResponse) {

        httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
        return Response.of(goodsService.deleteGoodsById(id));
    }

    @PatchMapping("/goods/{id}")
    public Response<Goods> updateGoods(@PathVariable Long id, @RequestBody Goods goods) {

        cleanGoods(goods, id);
        return Response.of(goodsService.updateGoodsById(goods));
    }

    private void cleanGoods(Goods goods) {
        goods.setCreatedAt(new Date());
        goods.setUpdatedAt(new Date());
        goods.setId(null);
    }

    private void cleanGoods(Goods goods, Long id) {
        goods.setCreatedAt(null);
        goods.setUpdatedAt(new Date());
        goods.setId(id);
    }
}
