package com.andrew.wechetshop.service;

import com.andrew.wechetshop.WechetShopApplication;
import com.andrew.wechetshop.api.entity.*;
import com.andrew.wechetshop.api.generate.Order;
import com.andrew.wechetshop.entity.GoodsWithNumber;
import com.andrew.wechetshop.entity.OrderResponse;
import com.andrew.wechetshop.entity.Response;
import com.andrew.wechetshop.generate.Goods;
import com.andrew.wechetshop.generate.Shop;
import com.andrew.wechetshop.mock.MockOrderRpcService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WechetShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class OrderIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    MockOrderRpcService mockOrderRpcService;

    @BeforeEach
    public void setUp() {
        super.setUp();
        MockitoAnnotations.openMocks(mockOrderRpcService);
    }

    @Test
    public void canCreateOrder() throws JsonProcessingException {
        OrderInfo orderInfo = new OrderInfo();

        GoodsInfo goodsInfo1 = new GoodsInfo();
        goodsInfo1.setId(4L);
        goodsInfo1.setNumber(3);
        GoodsInfo goodsInfo2 = new GoodsInfo();
        goodsInfo2.setId(5L);
        goodsInfo2.setNumber(5);
        orderInfo.setGoods(Arrays.asList(goodsInfo1, goodsInfo2));

        Order order = new Order();
        order.setId(1234L);
        order.setStatus(DataStatus.PENDING.getName());
        Mockito.when(mockOrderRpcService.orderRpcService.createOrder(Mockito.any(), Mockito.any()))
                .thenReturn(order);

        UserLoginResponse userLoginResponse = loginThenGetCookie();
        HttpResponse httpResponse =
                doHttpRequest("/api/v1/order", "POST", orderInfo, userLoginResponse.cookie);

        Response<OrderResponse> response = httpResponse.asJsonObject(new TypeReference<Response<OrderResponse>>() {
        });

        Assertions.assertEquals(1234L, response.getData().getId());
        Assertions.assertEquals(DataStatus.PENDING.getName(), response.getData().getStatus());

        Assertions.assertEquals(2L, response.getData().getShop().getId());
        Assertions.assertEquals("shop2", response.getData().getShop().getName());
        Assertions.assertEquals("desc2", response.getData().getShop().getDescription());

        Assertions.assertEquals(Arrays.asList(4L, 5L),
                response.getData().getGoods().stream().map(Goods::getId).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(3, 5),
                response.getData().getGoods().stream().map(GoodsWithNumber::getNumber).collect(Collectors.toList()));
    }

    @Test
    public void canRollBackWhenStockOut() throws JsonProcessingException {
        OrderInfo orderInfo = new OrderInfo();
        GoodsInfo goodsInfo1 = new GoodsInfo();
        goodsInfo1.setId(4L);
        goodsInfo1.setNumber(3);
        GoodsInfo goodsInfo2 = new GoodsInfo();
        goodsInfo2.setId(5L);
        goodsInfo2.setNumber(6);
        orderInfo.setGoods(Arrays.asList(goodsInfo1, goodsInfo2));

        UserLoginResponse userLoginResponse = loginThenGetCookie();
        HttpResponse httpResponse =
                doHttpRequest("/api/v1/order", "POST", orderInfo, userLoginResponse.cookie);

        Assertions.assertEquals(HttpStatus.GONE.value(), httpResponse.code);

        // 确保库存不足时成功回滚
        canCreateOrder();
    }

    @Test
    public void canDeleteOrder() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginThenGetCookie();

        Mockito
                .when(mockOrderRpcService.orderRpcService.getOrder(Mockito.anyInt(), Mockito.anyInt(),
                        Mockito.any(), Mockito.anyLong()))
                .thenReturn(mockResponse());

        PagedResponse<OrderResponse> orders = doHttpRequest("/api/v1/order?pageNum=3&pageSize=2",
                "GET", null, userLoginResponse.cookie)
                .asJsonObject(new TypeReference<PagedResponse<OrderResponse>>() {
                });
        Assertions.assertEquals(3, orders.getPageNum());
        Assertions.assertEquals(2, orders.getPageSize());
        Assertions.assertEquals(10, orders.getTotalPage());
        Assertions.assertEquals(Arrays.asList(100L, 101L),
                orders.getData().stream().map(OrderResponse::getId).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList("shop2", "shop2"),
                orders.getData().stream().map(OrderResponse::getShop).map(Shop::getName).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList("goods3", "goods4"),
                orders.getData().stream().map(OrderResponse::getGoods)
                        .flatMap(List::stream).map(GoodsWithNumber::getName).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(5, 3),
                orders.getData().stream().map(OrderResponse::getGoods)
                        .flatMap(List::stream).map(GoodsWithNumber::getNumber).collect(Collectors.toList()));

        Mockito
                .when(mockOrderRpcService.orderRpcService.deleteOrder(100L, 1L))
                .thenReturn(mockRpcOrderGoods(100L, 1L, 2L,
                        DataStatus.DELETED, 3L, 5));
        Response<OrderResponse> deletedOrder = doHttpRequest("/api/v1/order/100",
                "DELETE", null, userLoginResponse.cookie)
                .assertOkStatusCode()
                .asJsonObject(new TypeReference<Response<OrderResponse>>() {
                });
        Assertions.assertEquals(2L, deletedOrder.getData().getShop().getId());
        Assertions.assertEquals("shop2", deletedOrder.getData().getShop().getName());
        Assertions.assertEquals(DataStatus.DELETED.getName(), deletedOrder.getData().getStatus());
        Assertions.assertEquals(100L, deletedOrder.getData().getId());
        Assertions.assertEquals(3L, deletedOrder.getData().getGoods().get(0).getId());
        Assertions.assertEquals(5, deletedOrder.getData().getGoods().get(0).getNumber());
    }

    @Test
    public void return404IfOrderNotFound() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginThenGetCookie();

        Assertions.assertEquals(HttpStatus.NOT_FOUND.value(),
                doHttpRequest("/api/v1/order/12345", "PATCH",
                        new Order(), userLoginResponse.cookie).code);
    }

    @Test
    public void canUpdateOrderExpressInformation() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginThenGetCookie();

        Order orderUpdatedRequest = new Order();
        orderUpdatedRequest.setId(12345L);
        orderUpdatedRequest.setExpressId("SF12345");
        orderUpdatedRequest.setExpressCompany("顺丰");

        Order orderInDB = new Order();
        orderInDB.setId(12345L);
        orderInDB.setShopId(2L);

        Mockito.when(mockOrderRpcService.orderRpcService.selectOrderByPrimaryKey(12345L))
                .thenReturn(orderInDB);
        Mockito.when(mockOrderRpcService.orderRpcService.updateOrder(Mockito.any()))
                .thenReturn(mockRpcOrderGoods(12345L, 1L, 2L, DataStatus.DELIVERED, 3L, 10));

        Response<OrderResponse> response = doHttpRequest("/api/v1/order/12345", "PATCH",
                orderUpdatedRequest, userLoginResponse.cookie)
                .assertOkStatusCode()
                .asJsonObject(new TypeReference<Response<OrderResponse>>() {
                });

        Assertions.assertEquals(2L, response.getData().getShop().getId());
        Assertions.assertEquals("shop2", response.getData().getShop().getName());
        Assertions.assertEquals(DataStatus.DELIVERED.getName(), response.getData().getStatus());
        Assertions.assertEquals(1, response.getData().getGoods().size());
        Assertions.assertEquals(3, response.getData().getGoods().get(0).getId());
        Assertions.assertEquals(10, response.getData().getGoods().get(0).getNumber());
    }

    @Test
    public void canUpdateOrderStatus() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginThenGetCookie();

        Order orderUpdatedRequest = new Order();
        orderUpdatedRequest.setId(12345L);
        orderUpdatedRequest.setStatus(DataStatus.RECEIVED.getName());

        Order orderInDB = new Order();
        orderInDB.setId(12345L);
        orderInDB.setShopId(2L);

        Mockito.when(mockOrderRpcService.orderRpcService.selectOrderByPrimaryKey(12345L))
                .thenReturn(orderInDB);
        Mockito.when(mockOrderRpcService.orderRpcService.updateOrder(Mockito.any()))
                .thenReturn(mockRpcOrderGoods(12345L, 1L, 2L, DataStatus.DELIVERED, 3L, 10));

        Response<OrderResponse> response = doHttpRequest("/api/v1/order/12345", "PATCH",
                orderUpdatedRequest, userLoginResponse.cookie)
                .assertOkStatusCode()
                .asJsonObject(new TypeReference<Response<OrderResponse>>() {
                });

        Assertions.assertEquals(2L, response.getData().getShop().getId());
        Assertions.assertEquals("shop2", response.getData().getShop().getName());
        Assertions.assertEquals(DataStatus.DELIVERED.getName(), response.getData().getStatus());
        Assertions.assertEquals(1, response.getData().getGoods().size());
        Assertions.assertEquals(3, response.getData().getGoods().get(0).getId());
        Assertions.assertEquals(10, response.getData().getGoods().get(0).getNumber());
    }

    private PagedResponse<RpcOrderGoods> mockResponse() {
        RpcOrderGoods rpcOrderGoods1 = mockRpcOrderGoods(100, 1, 2,
                DataStatus.DELIVERED, 3, 5);
        RpcOrderGoods rpcOrderGoods2 = mockRpcOrderGoods(101, 1, 2,
                DataStatus.RECEIVED, 4, 3);
        return PagedResponse.pagedDataByParam(3, 2, 10,
                Arrays.asList(rpcOrderGoods1, rpcOrderGoods2));
    }

    private RpcOrderGoods mockRpcOrderGoods(long orderId, long userId,
                                            long shopId, DataStatus status,
                                            long goodsId, int number) {
        RpcOrderGoods rpcOrderGoods = new RpcOrderGoods();
        Order order = new Order();
        order.setId(orderId);
        order.setUserId(userId);
        order.setShopId(shopId);
        order.setStatus(status.getName());

        GoodsInfo goodsInfo = new GoodsInfo();
        goodsInfo.setId(goodsId);
        goodsInfo.setNumber(number);

        rpcOrderGoods.setOrder(order);
        rpcOrderGoods.setGoods(Arrays.asList(goodsInfo));
        return rpcOrderGoods;
    }

}
