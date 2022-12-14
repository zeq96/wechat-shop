package com.andrew.wechetshop.service;

import com.andrew.wechetshop.api.entity.*;
import com.andrew.wechetshop.api.exception.HttpException;
import com.andrew.wechetshop.api.generate.Order;
import com.andrew.wechetshop.api.rpc.OrderRpcService;
import com.andrew.wechetshop.dao.GoodsStockMapper;
import com.andrew.wechetshop.entity.GoodsWithNumber;
import com.andrew.wechetshop.entity.OrderResponse;
import com.andrew.wechetshop.generate.Goods;
import com.andrew.wechetshop.generate.Shop;
import com.andrew.wechetshop.generate.ShopMapper;
import com.andrew.wechetshop.generate.UserMapper;
import org.apache.dubbo.config.annotation.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);
    @Reference(version = "${wechetshop.orderservice.version}")
    private OrderRpcService orderRpcService;

    private final UserMapper userMapper;
    private final GoodsService goodsService;
    private final ShopMapper shopMapper;
    private final GoodsStockMapper goodsStockMapper;

    public OrderService(UserMapper userMapper, GoodsService goodsService, ShopMapper shopMapper,
                        GoodsStockMapper goodsStockMapper) {
        this.userMapper = userMapper;
        this.goodsService = goodsService;
        this.shopMapper = shopMapper;
        this.goodsStockMapper = goodsStockMapper;
    }

    public OrderResponse createOrder(OrderInfo orderInfo, Long userId) {

        Map<Long, Goods> idToGoodsMap = getGoodsIdToGoodsMap(orderInfo.getGoods());

        Order createdOrder = createOrderViaRpc(userId, orderInfo, idToGoodsMap);

        return generateOrderResponse(createdOrder, idToGoodsMap, orderInfo.getGoods());
    }

    public OrderResponse deleteOrder(long id, long userId) {
        RpcOrderGoods rpcOrderGoods = orderRpcService.deleteOrder(id, userId);

        Map<Long, Goods> idToGoodsMap = getGoodsIdToGoodsMap(rpcOrderGoods.getGoods());

        return generateOrderResponse(rpcOrderGoods.getOrder(), idToGoodsMap, rpcOrderGoods.getGoods());
    }

    public PagedResponse<OrderResponse> getOrder(int pageNum, int pageSize, DataStatus dataStatus, long userId) {
        PagedResponse<RpcOrderGoods> rpcResponse =
                orderRpcService.getOrder(pageNum, pageSize, dataStatus, userId);

        List<RpcOrderGoods> orderGoodsList = rpcResponse.getData();

        List<GoodsInfo> goods = orderGoodsList
                .stream()
                .map(RpcOrderGoods::getGoods)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        Map<Long, Goods> idToGoodsMap = getGoodsIdToGoodsMap(goods);

        List<OrderResponse> orders = orderGoodsList
                .stream()
                .map(order -> generateOrderResponse(order.getOrder(), idToGoodsMap, order.getGoods()))
                .collect(Collectors.toList());

        return PagedResponse.pagedDataByParam(
                rpcResponse.getPageNum(),
                rpcResponse.getPageSize(),
                rpcResponse.getTotalPage(),
                orders
        );
    }

    public OrderResponse updateOrderStatus(Order order, Long userId) {
        checkModifyAuthority(order, userId);

        Order copy = new Order();
        copy.setId(order.getId());
        order.setStatus(order.getStatus());
        order.setUpdatedAt(new Date());

        return toOrderResponse(orderRpcService.updateOrder(copy));
    }

    public OrderResponse updateExpressInformation(Order order, Long userId) {
        checkModifyAuthority(order, userId);

        Order copy = new Order();
        copy.setId(order.getId());
        copy.setUpdatedAt(new Date());
        copy.setExpressId(order.getExpressId());
        copy.setExpressCompany(order.getExpressCompany());

        return toOrderResponse(orderRpcService.updateOrder(copy));
    }

    private void checkModifyAuthority(Order order, Long userId) {
        Order orderInDB = orderRpcService.selectOrderByPrimaryKey(order.getId());
        if (orderInDB == null) {
            throw HttpException.notFound("???????????????");
        }
        Shop shop = shopMapper.selectByPrimaryKey(orderInDB.getShopId());
        if (shop == null) {
            throw HttpException.notFound("???????????????");
        }
        if (!shop.getOwnerUserId().equals(userId) && !orderInDB.getUserId().equals(userId)) {
            throw HttpException.notAuthorized("????????????");
        }
    }

    private OrderResponse toOrderResponse(RpcOrderGoods rpcOrderGoods) {
        Map<Long, Goods> idToGoodsMap = getGoodsIdToGoodsMap(rpcOrderGoods.getGoods());
        return generateOrderResponse(rpcOrderGoods.getOrder(), idToGoodsMap, rpcOrderGoods.getGoods());
    }

    private OrderResponse generateOrderResponse(Order createdOrder, Map<Long, Goods> idToGoods, List<GoodsInfo> goodsInfo) {
        OrderResponse response = new OrderResponse(createdOrder);
        Long shopId = new ArrayList<>(idToGoods.values()).get(0).getShopId();
        response.setShop(shopMapper.selectByPrimaryKey(shopId));
        response.setGoods(
                goodsInfo.stream()
                        .map(goods -> toGoodsWithNumber(goods, idToGoods))
                        .collect(Collectors.toList())
        );
        return response;
    }

    private Map<Long, Goods> getGoodsIdToGoodsMap(List<GoodsInfo> goods) {
        List<Long> goodsIds = goods
                .stream()
                .map(GoodsInfo::getId)
                .collect(Collectors.toList());
        return goodsService.mapGoodsFromId(goodsIds);
    }

    private Order createOrderViaRpc(Long userId, OrderInfo orderInfo, Map<Long, Goods> idToGoods) {
        Order order = new Order();
        order.setUserId(userId);
        order.setAddress(userMapper.selectByPrimaryKey(userId).getAddress());
        order.setTotalPrice(calculateTotalPrice(orderInfo, idToGoods));
        return orderRpcService.createOrder(orderInfo, order);
    }

    @Transactional
    public void deductStock(OrderInfo orderInfo) {
        for (GoodsInfo goodsInfo : orderInfo.getGoods()) {
            if (goodsStockMapper.deductStock(goodsInfo) <= 0) {
                LOGGER.error("??????????????????, ??????id: {}, ??????: {}", goodsInfo.getId(), goodsInfo.getNumber());
                throw HttpException.gone("??????????????????");
            }
        }
    }

    private GoodsWithNumber toGoodsWithNumber(GoodsInfo goodsInfo, Map<Long, Goods> idToGoods) {
        Goods goods = idToGoods.get(goodsInfo.getId());
        GoodsWithNumber ret = new GoodsWithNumber(goods);
        ret.setNumber(goodsInfo.getNumber());
        return ret;
    }

    private BigDecimal calculateTotalPrice(OrderInfo orderInfo, Map<Long, Goods> idToGoods) {
        BigDecimal ret = BigDecimal.ZERO;
        for (GoodsInfo goodsInfo : orderInfo.getGoods()) {
            Long goodsId = goodsInfo.getId();
            Integer number = goodsInfo.getNumber();
            Goods goods = idToGoods.get(goodsId);
            if (goods == null) {
                throw HttpException.badRequest("??????ID??????: " + goodsId);
            }
            if (number <= 0) {
                throw HttpException.badRequest("??????????????????: " + number);
            }
            ret = ret.add(goods.getPrice().multiply(new BigDecimal(number)));

        }
        return ret;
    }
}
