package com.andrew.wechetshop.service;

import com.andrew.wechetshop.WechetShopApplication;
import com.andrew.wechetshop.api.entity.PagedResponse;
import com.andrew.wechetshop.entity.ShoppingCartData;
import com.andrew.wechetshop.entity.*;
import com.andrew.wechetshop.entity.ShoppingCartRequestItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WechetShopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:test-application.yml"})
public class ShoppingCartIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void canQueryShoppingCartData() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginThenGetCookie();
        PagedResponse<ShoppingCartData> response = doHttpRequest("/api/v1/shoppingCart?pageNum=2&pageSize=1",
                "GET", null, userLoginResponse.cookie).asJsonObject(new TypeReference<PagedResponse<ShoppingCartData>>() {
        });
        Assertions.assertEquals(2, response.getPageNum());
        Assertions.assertEquals(1, response.getPageSize());
        Assertions.assertEquals(2, response.getTotalPage());
        Assertions.assertEquals(1, response.getData().size());
        Assertions.assertEquals(2, response.getData().get(0).getShop().getId());
        Assertions.assertEquals(Arrays.asList(4L, 5L),
                response.getData().get(0).getGoods().stream().map(GoodsWithNumber::getId).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(new BigDecimal(100), new BigDecimal(200)),
                response.getData().get(0).getGoods().stream().map(GoodsWithNumber::getPrice).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(200, 300),
                response.getData().get(0).getGoods().stream().map(GoodsWithNumber::getNumber).collect(Collectors.toList()));
    }

    @Test
    public void canAddShoppingCartData() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginThenGetCookie();
        ShoppingCartRequest shoppingCartRequest = new ShoppingCartRequest();
        ShoppingCartRequestItem item = new ShoppingCartRequestItem();
        item.setId(2L);
        item.setNumber(2);
        shoppingCartRequest.setGoods(Collections.singletonList(item));

        Response<ShoppingCartData> response = doHttpRequest("/api/v1/shoppingCart", "POST",
                shoppingCartRequest, userLoginResponse.cookie)
                .asJsonObject(new TypeReference<Response<ShoppingCartData>>() {
                });
        Assertions.assertEquals(1L, response.getData().getShop().getId());
        Assertions.assertEquals(Arrays.asList(1L, 2L),
                response.getData().getGoods().stream().map(GoodsWithNumber::getId).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(100, 2),
                response.getData().getGoods().stream().map(GoodsWithNumber::getNumber).collect(Collectors.toList()));
        Assertions.assertTrue(response.getData().getGoods().stream().allMatch(
                goodsWithNumber -> goodsWithNumber.getShopId() == 1L
        ));

    }

    @Test
    public void canDeleteShoppingCartData() throws JsonProcessingException {
        UserLoginResponse userLoginResponse = loginThenGetCookie();

        Response<ShoppingCartData> response = doHttpRequest("/api/v1/shoppingCart/5", "DELETE",
                null, userLoginResponse.cookie)
                .asJsonObject(new TypeReference<Response<ShoppingCartData>>() {
                });
        Assertions.assertEquals(2L, response.getData().getShop().getId());
        Assertions.assertEquals(Arrays.asList(4L),
                response.getData().getGoods().stream().map(GoodsWithNumber::getId).collect(Collectors.toList()));
        Assertions.assertEquals(Arrays.asList(200),
                response.getData().getGoods().stream().map(GoodsWithNumber::getNumber).collect(Collectors.toList()));
        Assertions.assertTrue(response.getData().getGoods().stream().allMatch(
                goodsWithNumber -> goodsWithNumber.getShopId() == 2L
        ));
    }
}
