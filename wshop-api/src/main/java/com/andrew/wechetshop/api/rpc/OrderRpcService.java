package com.andrew.wechetshop.api.rpc;

import com.andrew.wechetshop.api.entity.DataStatus;
import com.andrew.wechetshop.api.entity.OrderInfo;
import com.andrew.wechetshop.api.entity.PagedResponse;
import com.andrew.wechetshop.api.entity.RpcOrderGoods;
import com.andrew.wechetshop.api.generate.Order;

public interface OrderRpcService {
    Order createOrder(OrderInfo orderInfo, Order order);

    RpcOrderGoods deleteOrder(long id, long userId);

    PagedResponse<RpcOrderGoods> getOrder(int pageNum, int pageSize, DataStatus status, long userId);

    Order selectOrderByPrimaryKey(Long id);

    RpcOrderGoods updateOrder(Order order);
}
