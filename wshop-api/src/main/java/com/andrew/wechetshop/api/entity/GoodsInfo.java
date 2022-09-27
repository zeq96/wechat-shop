package com.andrew.wechetshop.api.entity;

import java.io.Serializable;

public class GoodsInfo implements Serializable {
    private Long id;
    private Integer number;

    public GoodsInfo() {}

    public GoodsInfo(Long id, Integer number) {
        this.id = id;
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
