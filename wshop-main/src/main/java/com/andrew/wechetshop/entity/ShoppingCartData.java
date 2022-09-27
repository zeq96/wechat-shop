package com.andrew.wechetshop.entity;

import com.andrew.wechetshop.generate.Shop;

import java.util.List;

public class ShoppingCartData {
    private Shop shop;

    private List<GoodsWithNumber> goods;

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<GoodsWithNumber> getGoods() {
        return goods;
    }

    public void setGoods(List<GoodsWithNumber> goods) {
        this.goods = goods;
    }
}
