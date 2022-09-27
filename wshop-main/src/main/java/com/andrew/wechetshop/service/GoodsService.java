package com.andrew.wechetshop.service;

import com.andrew.wechetshop.api.entity.DataStatus;
import com.andrew.wechetshop.api.entity.PagedResponse;
import com.andrew.wechetshop.api.exception.HttpException;
import com.andrew.wechetshop.generate.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GoodsService {

    private final GoodsMapper goodsMapper;
    private final ShopMapper shopMapper;

    public GoodsService(GoodsMapper goodsMapper, ShopMapper shopMapper) {
        this.goodsMapper = goodsMapper;
        this.shopMapper = shopMapper;
    }

    public PagedResponse<Goods> getGoodsLimit(Integer pageNumber, Integer pageSize, Integer shopId) {
        long count = countGoods(shopId);
        int totalPage = (int) (count % pageSize == 0 ? count / pageSize : count / pageSize + 1);
        GoodsExample example = new GoodsExample();
        example.setOffset(pageSize * (pageNumber - 1));
        example.setLimit(pageSize);
        List<Goods> goods = goodsMapper.selectByExample(example);
        return PagedResponse.pagedDataByParam(pageNumber, pageSize, totalPage, goods);
    }

    private long countGoods(Integer shopId) {
        GoodsExample goodsExample = new GoodsExample();
        if (shopId == null) {
            goodsExample.createCriteria()
                    .andStatusEqualTo(DataStatus.OK.getName());
        } else {
            goodsExample.createCriteria()
                    .andStatusEqualTo(DataStatus.OK.getName())
                    .andShopIdEqualTo(shopId.longValue());
        }
        return goodsMapper.countByExample(goodsExample);
    }

    public Goods createGoods(Goods goods) {
        Shop shop = shopMapper.selectByPrimaryKey(goods.getShopId());
        if (!Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            throw HttpException.notAuthorized("无权访问!");
        }
        goods.setStatus(DataStatus.OK.getName());
        long id = goodsMapper.insert(goods);
        goods.setId(id);
        return goods;
    }

    public Goods deleteGoodsById(Long id) {
        Goods goods = goodsMapper.selectByPrimaryKey(id);
        if (goods == null) {
            throw HttpException.notFound("商品未找到!");
        }

        Shop shop = shopMapper.selectByPrimaryKey(goods.getShopId());
        if (!Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            throw HttpException.notAuthorized("无权访问!");
        }
        goods.setStatus(DataStatus.DELETED.getName());
        goodsMapper.updateByPrimaryKey(goods);
        return goods;
    }

    public Goods updateGoodsById(Goods goods) {
        Shop shop = shopMapper.selectByPrimaryKey(goods.getShopId());
        if (Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            int affectedRows = goodsMapper.updateByPrimaryKey(goods);
            if (affectedRows == 0) {
                throw HttpException.notFound("商品未找到!");
            }
            return goods;
        } else {
            throw HttpException.notAuthorized("无权访问!");
        }
    }

    public Map<Long, Goods> mapGoodsFromId(List<Long> goodsIds) {
        GoodsExample goodsExample = new GoodsExample();
        goodsExample.createCriteria().andIdIn(goodsIds);
        List<Goods> goods = goodsMapper.selectByExample(goodsExample);
        return goods.stream().collect(Collectors.toMap(Goods::getId, item -> item));
    }

}
