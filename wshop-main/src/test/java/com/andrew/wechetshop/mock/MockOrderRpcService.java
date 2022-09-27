package com.andrew.wechetshop.mock;

import com.andrew.wechetshop.api.entity.DataStatus;
import com.andrew.wechetshop.api.entity.OrderInfo;
import com.andrew.wechetshop.api.entity.PagedResponse;
import com.andrew.wechetshop.api.entity.RpcOrderGoods;
import com.andrew.wechetshop.api.generate.Order;
import com.andrew.wechetshop.api.rpc.OrderRpcService;
import org.apache.dubbo.config.annotation.Service;
import org.mockito.Mock;

@Service(version = "${wechetshop.orderservice.version}")
public class MockOrderRpcService implements OrderRpcService {
    @Mock
    public OrderRpcService orderRpcService;



    @Override
    public Order createOrder(OrderInfo orderInfo, Order order) {
        return orderRpcService.createOrder(orderInfo, order);
    }

    @Override
    public RpcOrderGoods deleteOrder(long id, long userId) {
        return orderRpcService.deleteOrder(id, userId);
    }

    @Override
    public PagedResponse<RpcOrderGoods> getOrder(int pageNum, int pageSize, DataStatus status, long userId) {
        return orderRpcService.getOrder(pageNum, pageSize, status, userId);
    }

    @Override
    public Order selectOrderByPrimaryKey(Long id) {
        return orderRpcService.selectOrderByPrimaryKey(id);
    }

    @Override
    public RpcOrderGoods updateOrder(Order order) {
        return orderRpcService.updateOrder(order);
    }

}
