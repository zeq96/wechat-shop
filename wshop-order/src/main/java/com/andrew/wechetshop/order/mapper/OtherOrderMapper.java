package com.andrew.wechetshop.order.mapper;

import com.andrew.wechetshop.api.entity.GoodsInfo;
import com.andrew.wechetshop.api.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OtherOrderMapper {
    void insertOrders(OrderInfo orderInfo);

    List<GoodsInfo> getGoodsInfoOfOrder(long orderId);


}
