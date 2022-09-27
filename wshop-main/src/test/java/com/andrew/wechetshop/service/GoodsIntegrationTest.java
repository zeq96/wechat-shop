package com.andrew.wechetshop.service;

import com.andrew.wechetshop.WechetShopApplication;
import com.andrew.wechetshop.entity.Response;
import com.andrew.wechetshop.generate.Goods;
import com.andrew.wechetshop.generate.Shop;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WechetShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class GoodsIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testCreateGoods() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginThenGetCookie();
        String sessionId = getSessionIdFromSetCookie(userLoginResponse.cookie);

        Shop shop = new Shop();
        shop.setName("我的微信店铺");
        shop.setDescription("我的小店开张啦");
        shop.setImgUrl("http://url");
        AbstractIntegrationTest.HttpResponse shopResponse = doHttpRequest("/api/v1/shop",
                "POST", shop, sessionId);
        Response<Shop> shopInResponse = objectMapper.readValue(shopResponse.body, new TypeReference<Response<Shop>>() {
        });

        Assertions.assertEquals(HttpServletResponse.SC_CREATED, shopResponse.code);
        Assertions.assertEquals("我的微信店铺", shopInResponse.getData().getName());
        Assertions.assertEquals("我的小店开张啦", shopInResponse.getData().getDescription());
        Assertions.assertEquals("http://url", shopInResponse.getData().getImgUrl());
        Assertions.assertEquals("ok", shopInResponse.getData().getStatus());
        Assertions.assertEquals(shopInResponse.getData().getOwnerUserId(), userLoginResponse.user.getId());

        Goods goods = new Goods();
        goods.setName("肥皂");
        goods.setDescription("纯天然无污染肥皂");
        goods.setDetails("这是一块好肥皂");
        goods.setImgUrl("http://url");
        goods.setPrice(new BigDecimal(1000));
        goods.setStock(10);
        goods.setShopId(shopInResponse.getData().getId());

        AbstractIntegrationTest.HttpResponse goodsResponse = doHttpRequest("/api/v1/goods",
                "POST", goods, sessionId);
        Response<Goods> goodsInResponse = objectMapper.readValue(goodsResponse.body, new TypeReference<Response<Goods>>() {
        });
        Assertions.assertEquals(HttpServletResponse.SC_CREATED, goodsResponse.code);
        Assertions.assertEquals(goods.getName(), goodsInResponse.getData().getName());
        Assertions.assertEquals(shopInResponse.getData().getId(), goodsInResponse.getData().getShopId());
    }

}
