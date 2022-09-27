package com.andrew.wechetshop.dao;

import com.andrew.wechetshop.api.entity.GoodsInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GoodsStockMapper {
    int deductStock(GoodsInfo goodsInfo);
}
