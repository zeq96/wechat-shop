package com.andrew.wechetshop.order.service;

import com.andrew.wechetshop.api.entity.*;
import com.andrew.wechetshop.api.exception.HttpException;
import com.andrew.wechetshop.api.generate.Order;
import com.andrew.wechetshop.order.generate.OrderGoodsMapper;
import com.andrew.wechetshop.order.generate.OrderMapper;
import com.andrew.wechetshop.order.mapper.OtherOrderMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderRpcServiceImplTest {
    String databaseUrl = "jdbc:mysql://localhost:3307/order?serverTimezone=UTC";
    String username = "root";
    String password = "root";

    SqlSession sqlSession;
    OrderRpcServiceImpl orderRpcService;

    @BeforeEach
    public void setUp() throws IOException {
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(databaseUrl, username, password);
        Flyway flyway = new Flyway(conf);
        flyway.clean();
        flyway.migrate();

        InputStream resourceAsStream = Resources.getResourceAsStream("test-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        sqlSession = sqlSessionFactory.openSession(true);

        orderRpcService = new OrderRpcServiceImpl(
                sqlSession.getMapper(OrderMapper.class),
                sqlSession.getMapper(OtherOrderMapper.class),
                sqlSession.getMapper(OrderGoodsMapper.class)
        );
    }

    @AfterEach
    public void cleanUp() {
        sqlSession.close();
    }

    @Test
    public void createOrderTest() {
        OrderInfo orderInfo = new OrderInfo();
        GoodsInfo goods1 = new GoodsInfo(1L, 2);
        GoodsInfo goods2 = new GoodsInfo(2L, 10);
        orderInfo.setGoods(Arrays.asList(goods1, goods2));

        Order order = new Order();
        order.setUserId(1L);
        order.setShopId(1L);
        order.setAddress("火星");
        order.setTotalPrice(new BigDecimal(1000));

        Order orderWithId = orderRpcService.createOrder(orderInfo, order);
        Assertions.assertNotNull(orderWithId.getId());

        Order orderInDB = orderRpcService.selectOrderByPrimaryKey(orderWithId.getId());
        Assertions.assertEquals(1L, orderInDB.getUserId());
        Assertions.assertEquals(1L, orderInDB.getShopId());
        Assertions.assertEquals("火星", orderInDB.getAddress());
        Assertions.assertEquals(new BigDecimal(1000), orderInDB.getTotalPrice());
        Assertions.assertEquals(DataStatus.PENDING.getName(), orderInDB.getStatus());
    }

    @Test
    public void getPagedOrderTest() {
        PagedResponse<RpcOrderGoods> response =
                orderRpcService.getOrder(2, 1, null, 1);

        Assertions.assertEquals(2, response.getPageNum());
        Assertions.assertEquals(1, response.getPageSize());
        Assertions.assertEquals(2, response.getTotalPage());
        Assertions.assertEquals(1, response.getData().size());

        Order order = response.getData().get(0).getOrder();
        Assertions.assertEquals(2, order.getId());
        Assertions.assertEquals(1, order.getUserId());
        Assertions.assertEquals(2, order.getShopId());
        Assertions.assertEquals(new BigDecimal(700), order.getTotalPrice());
        Assertions.assertEquals("火星", order.getAddress());
        Assertions.assertEquals(DataStatus.PENDING.getName(), order.getStatus());

        List<GoodsInfo> goods = response.getData().get(0).getGoods();
        Assertions.assertEquals(Arrays.asList(1L, 2L),
                goods.stream().map(GoodsInfo::getId).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(3, 4),
                goods.stream().map(GoodsInfo::getNumber).collect(Collectors.toList()));
    }

    @Test
    public void updateOrderTest() {
        Order originOrder = orderRpcService.selectOrderByPrimaryKey(2L);
        originOrder.setExpressCompany("中通");
        originOrder.setExpressId("ZT12345");
        originOrder.setStatus(DataStatus.DELIVERED.getName());

        RpcOrderGoods rpcOrderGoods = orderRpcService.updateOrder(originOrder);
        Order updatedOrder = rpcOrderGoods.getOrder();
        List<GoodsInfo> goods = rpcOrderGoods.getGoods();
        Assertions.assertEquals(2, updatedOrder.getId());
        Assertions.assertEquals(1, updatedOrder.getUserId());
        Assertions.assertEquals(2, updatedOrder.getShopId());
        Assertions.assertEquals(new BigDecimal(700), updatedOrder.getTotalPrice());
        Assertions.assertEquals("火星", updatedOrder.getAddress());
        Assertions.assertEquals(DataStatus.DELIVERED.getName(), updatedOrder.getStatus());
        Assertions.assertEquals("中通", updatedOrder.getExpressCompany());
        Assertions.assertEquals("ZT12345", updatedOrder.getExpressId());

        Assertions.assertEquals(Arrays.asList(1L, 2L),
                goods.stream().map(GoodsInfo::getId).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(3, 4),
                goods.stream().map(GoodsInfo::getNumber).collect(Collectors.toList()));
    }

    @Test
    public void deleteOrderTest() {
        RpcOrderGoods rpcOrderGoods = orderRpcService.deleteOrder(2L, 1L);
        Order deletedOrder = rpcOrderGoods.getOrder();

        Assertions.assertEquals(2, deletedOrder.getId());
        Assertions.assertEquals(1, deletedOrder.getUserId());
        Assertions.assertEquals(2, deletedOrder.getShopId());
        Assertions.assertEquals(new BigDecimal(700), deletedOrder.getTotalPrice());
        Assertions.assertEquals("火星", deletedOrder.getAddress());
        Assertions.assertEquals(DataStatus.DELETED.getName(), deletedOrder.getStatus());

        List<GoodsInfo> goods = rpcOrderGoods.getGoods();
        Assertions.assertEquals(Arrays.asList(1L, 2L),
                goods.stream().map(GoodsInfo::getId).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(3, 4),
                goods.stream().map(GoodsInfo::getNumber).collect(Collectors.toList()));
    }

    @Test
    public void throwExceptionIfNotAuthorized() {
        HttpException httpException = Assertions.assertThrows(HttpException.class,
                () -> orderRpcService.deleteOrder(2L, 0));
        Assertions.assertEquals(HttpServletResponse.SC_FORBIDDEN, httpException.getStatusCode());

    }
}
