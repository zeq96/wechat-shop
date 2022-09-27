package com.andrew.wechetshop.service;

import com.andrew.wechetshop.api.entity.PagedResponse;
import com.andrew.wechetshop.api.exception.HttpException;
import com.andrew.wechetshop.generate.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class GoodsServiceTest {
    @Mock
    private GoodsMapper goodsMapper;
    @Mock
    private ShopMapper shopMapper;
    @InjectMocks
    private GoodsService goodsService;

    @Mock
    private Shop shop;
    @Mock
    private Goods goods;

    @BeforeEach
    public void initThreadLocal() {
        User user = new User();
        user.setId(1L);
        UserContext.setCurrentUser(user);
    }

    @AfterEach
    public void clearThreadLocal() {
        UserContext.clearCurrentUser();
    }

    @Test
    public void createGoodsSuccessIfUserIsOwner() {
        Mockito.when(shopMapper.selectByPrimaryKey(Mockito.anyLong()))
                .thenReturn(shop);
        Mockito.when(shop.getOwnerUserId()).thenReturn(1L);
        Mockito.when(goodsMapper.insert(goods)).thenReturn(123);

        Assertions.assertEquals(goods, goodsService.createGoods(goods));
        Mockito.verify(goods).setId(123L);
    }

    @Test
    public void createGoodsFailureIfUserIsNotOwner() {
        Mockito.when(shopMapper.selectByPrimaryKey(Mockito.anyLong()))
                .thenReturn(shop);
        Mockito.when(shop.getOwnerUserId()).thenReturn(3L);

        HttpException httpException = Assertions.assertThrows(HttpException.class,
                () -> goodsService.createGoods(goods));

        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), httpException.getStatusCode());
    }

    @Test
    public void getGoodsWithNullShopId() {
        int pageNumber = 5;
        int pageSize = 10;

        List<Goods> mockData = Mockito.mock(List.class);
        Mockito.when(goodsMapper.countByExample(Mockito.any())).thenReturn(55L);
        Mockito.when(goodsMapper.selectByExample(Mockito.any())).thenReturn(mockData);
        PagedResponse<Goods> response = goodsService.getGoodsLimit(pageNumber, pageSize, null);

        Assertions.assertEquals(6, response.getTotalPage());
        Assertions.assertEquals(5, response.getPageNum());
        Assertions.assertEquals(10, response.getPageSize());
        Assertions.assertEquals(mockData, response.getData());
    }


}
