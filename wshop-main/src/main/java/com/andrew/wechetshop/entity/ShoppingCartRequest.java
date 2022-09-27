package com.andrew.wechetshop.entity;

import java.util.List;

public class ShoppingCartRequest {
    List<ShoppingCartRequestItem> goods;

    public List<ShoppingCartRequestItem> getGoods() {
        return goods;
    }

    public void setGoods(List<ShoppingCartRequestItem> goods) {
        this.goods = goods;
    }
}
