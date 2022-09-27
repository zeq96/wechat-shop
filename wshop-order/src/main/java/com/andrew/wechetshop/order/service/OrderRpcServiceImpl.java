package com.andrew.wechetshop.order.service;

import com.andrew.wechetshop.api.entity.*;
import com.andrew.wechetshop.api.exception.HttpException;
import com.andrew.wechetshop.api.generate.Order;
import com.andrew.wechetshop.api.generate.OrderExample;
import com.andrew.wechetshop.api.generate.OrderGoods;
import com.andrew.wechetshop.api.generate.OrderGoodsExample;
import com.andrew.wechetshop.api.rpc.OrderRpcService;
import com.andrew.wechetshop.order.generate.OrderGoodsMapper;
import com.andrew.wechetshop.order.generate.OrderMapper;
import com.andrew.wechetshop.order.mapper.OtherOrderMapper;
import org.apache.dubbo.config.annotation.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

@Service(version = "${wechetshop.orderservice.version}")
public class OrderRpcServiceImpl implements OrderRpcService {

    private final OrderMapper orderMapper;
    private final OtherOrderMapper otherOrderMapper;
    private final OrderGoodsMapper orderGoodsMapper;

    public OrderRpcServiceImpl(OrderMapper orderMapper, OtherOrderMapper otherOrderMapper,
                               OrderGoodsMapper orderGoodsMapper) {
        this.orderMapper = orderMapper;
        this.otherOrderMapper = otherOrderMapper;
        this.orderGoodsMapper = orderGoodsMapper;
    }

    @Override
    public Order createOrder(OrderInfo orderInfo, Order order) {
        insertOrder(order);
        orderInfo.setOrderId(order.getId());
        otherOrderMapper.insertOrders(orderInfo);
        return order;
    }

    @Override
    public RpcOrderGoods deleteOrder(long id, long userId) {
        RpcOrderGoods ret = new RpcOrderGoods();
        Order order = orderMapper.selectByPrimaryKey(id);
        if (order == null) {
            throw HttpException.notFound("订单未找到");
        }
        if (order.getUserId() != userId) {
            throw HttpException.notAuthorized("无权访问");
        }
        order.setStatus(DataStatus.DELETED.getName());
        order.setUpdatedAt(new Date());
        orderMapper.updateByPrimaryKey(order);

        List<GoodsInfo> goodsInfoOfOrder = otherOrderMapper.getGoodsInfoOfOrder(order.getId());
        ret.setOrder(order);
        ret.setGoods(goodsInfoOfOrder);
        return ret;
    }

    @Override
    public PagedResponse<RpcOrderGoods> getOrder(int pageNum, int pageSize, DataStatus status, long userId) {
        OrderExample countByStatus = new OrderExample();
        setCriteriaByStatus(countByStatus, status).andUserIdEqualTo(userId);
        long count = orderMapper.countByExample(countByStatus);

        OrderExample pagedOrder = new OrderExample();
        pagedOrder.setOffset((pageNum - 1) * pageSize);
        pagedOrder.setLimit(pageSize);
        setCriteriaByStatus(pagedOrder, status).andUserIdEqualTo(userId);
        List<Order> orders = orderMapper.selectByExample(pagedOrder);

        List<OrderGoods> orderGoodsOfOrders = getOrderGoodsOfOrders(orders);
        Map<Long, List<OrderGoods>> orderIdToGoodsMap = orderGoodsOfOrders.stream()
                .collect(Collectors.groupingBy(OrderGoods::getOrderId));

        List<RpcOrderGoods> rpcOrderGoods = orders.stream()
                .map(order -> toRpcOrderGoods(order, orderIdToGoodsMap))
                .collect(Collectors.toList());

        int totalPage = (int) (count % pageSize == 0 ? count / pageSize : count / pageSize + 1);
        return PagedResponse.pagedDataByParam(pageNum, pageSize, totalPage, rpcOrderGoods);
    }

    @Override
    public Order selectOrderByPrimaryKey(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    @Override
    public RpcOrderGoods updateOrder(Order order) {
        OrderExample equalsId = new OrderExample();
        equalsId.createCriteria().andIdEqualTo(order.getId());
        orderMapper.updateByExampleSelective(order, equalsId);

        RpcOrderGoods ret = new RpcOrderGoods();
        List<GoodsInfo> goodsInfoOfOrder = otherOrderMapper.getGoodsInfoOfOrder(order.getId());
        ret.setOrder(orderMapper.selectByPrimaryKey(order.getId()));
        ret.setGoods(goodsInfoOfOrder);
        return ret;
    }

    private RpcOrderGoods toRpcOrderGoods(Order order, Map<Long, List<OrderGoods>> orderIdToGoodsMap) {
        RpcOrderGoods ret = new RpcOrderGoods();
        ret.setOrder(order);
        List<GoodsInfo> goods = orderIdToGoodsMap
                .getOrDefault(order.getId(), Collections.emptyList())
                .stream()
                .map(this::toGoodsInfo)
                .collect(Collectors.toList());
        ret.setGoods(goods);
        return ret;
    }

    private GoodsInfo toGoodsInfo(OrderGoods orderGoods) {
        GoodsInfo ret = new GoodsInfo();
        ret.setNumber(orderGoods.getNumber().intValue());
        ret.setId(orderGoods.getGoodsId());
        return ret;
    }

    private List<OrderGoods> getOrderGoodsOfOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> orderIds = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        OrderGoodsExample selectInIds = new OrderGoodsExample();
        selectInIds.createCriteria().andOrderIdIn(orderIds);
        return orderGoodsMapper.selectByExample(selectInIds);
    }

    private OrderExample.Criteria setCriteriaByStatus(OrderExample orderExample, DataStatus dataStatus) {
        if (dataStatus == null) {
            return orderExample.createCriteria().andStatusNotEqualTo(DataStatus.DELETED.getName());
        } else {
            return orderExample.createCriteria().andStatusEqualTo(dataStatus.getName());
        }
    }

    private void insertOrder(Order order) {
        order.setStatus(DataStatus.PENDING.getName());
        verify(() -> order.getUserId() == null, "用户ID不能为空");
        verify(() -> order.getTotalPrice() == null || order.getTotalPrice().doubleValue() < 0, "订单总价非法");
        verify(() -> order.getAddress() == null, "订单地址不能为空");
        order.setExpressId(null);
        order.setExpressCompany(null);
        order.setCreatedAt(new Date());
        order.setUpdatedAt(new Date());
        orderMapper.insert(order);
    }

    private void verify(BooleanSupplier supplier, String message) {
        if (supplier.getAsBoolean()) {
            throw new IllegalArgumentException(message);
        }
    }
}
