package com.andrew.wechetshop.dao;

import com.andrew.wechetshop.entity.ShoppingCartData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ShoppingCartQueryMapper {

    int countShopByCurrentUser(Long userId);

    List<ShoppingCartData> selectShoppingCartDataByCurrentUser(@Param("userId") Long userId,
                                                               @Param("limit") int limit, @Param("offset") int offset);

    List<ShoppingCartData> selectShoppingCartDataByCurrentUserAndShopId(@Param("userId") Long userId,
                                                                        @Param("shopId") Long shopId);

    void deleteGoodsInShoppingCart(@Param("userId") Long userId, @Param("goodsId") Long goodsId);
}
