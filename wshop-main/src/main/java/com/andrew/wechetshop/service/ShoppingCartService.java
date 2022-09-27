package com.andrew.wechetshop.service;

import com.andrew.wechetshop.api.entity.DataStatus;
import com.andrew.wechetshop.api.entity.PagedResponse;
import com.andrew.wechetshop.dao.ShoppingCartQueryMapper;
import com.andrew.wechetshop.entity.ShoppingCartData;
import com.andrew.wechetshop.entity.*;
import com.andrew.wechetshop.api.exception.HttpException;
import com.andrew.wechetshop.entity.ShoppingCartRequestItem;
import com.andrew.wechetshop.generate.*;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShoppingCartService {
    private final ShoppingCartQueryMapper shoppingCartQueryMapper;
    private final GoodsService goodsService;
    private final SqlSessionFactory sqlSessionFactory;
    private final GoodsMapper goodsMapper;

    public ShoppingCartService(ShoppingCartQueryMapper shoppingCartQueryMapper, GoodsService goodsService, SqlSessionFactory sqlSessionFactory, GoodsMapper goodsMapper) {
        this.shoppingCartQueryMapper = shoppingCartQueryMapper;
        this.goodsService = goodsService;
        this.sqlSessionFactory = sqlSessionFactory;
        this.goodsMapper = goodsMapper;
    }

    public PagedResponse<ShoppingCartData> getShoppingCartByCurrentUser(Integer pageNum, Integer pageSize) {
        Long currentUserId = UserContext.getCurrentUser().getId();
        int totalNum = shoppingCartQueryMapper.countShopByCurrentUser(currentUserId);
        int totalPage = totalNum % pageSize == 0 ? totalNum / pageSize : totalNum / pageSize + 1;
        int limit = (pageNum - 1) * pageSize;
        List<ShoppingCartData> shoppingCartData = shoppingCartQueryMapper
                .selectShoppingCartDataByCurrentUser(currentUserId, limit, pageSize);
        List<ShoppingCartData> mergedShoppingCartData = shoppingCartData
                .stream()
                .collect(Collectors.groupingBy(item -> item.getShop().getId()))
                .values()
                .stream()
                .map(this::merge)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return PagedResponse.pagedDataByParam(pageNum, pageSize, totalPage, mergedShoppingCartData);
    }

    public ShoppingCartData addShoppingCart(ShoppingCartRequest shoppingCartRequest) {
        Long currentUserId = UserContext.getCurrentUser().getId();
        List<ShoppingCartRequestItem> goods = shoppingCartRequest.getGoods();
        List<Long> goodsIds = goods.stream()
                .map(ShoppingCartRequestItem::getId).collect(Collectors.toList());
        if (goodsIds.isEmpty()) {
            throw HttpException.badRequest("商品为空!");
        }

        Map<Long, Goods> idToGoods = goodsService.mapGoodsFromId(goodsIds);

        if (!isGoodsInSameShop(new ArrayList<>(idToGoods.values()))) {
            throw HttpException.badRequest("商品ID非法!");
        }

        List<ShoppingCart> shoppingCartRows = goods.stream()
                .map(item -> toShoppingCartRow(item, idToGoods, currentUserId))
                .collect(Collectors.toList());

        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            ShoppingCartMapper mapper = sqlSession.getMapper(ShoppingCartMapper.class);
            shoppingCartRows.forEach(row -> insertGoodsToShoppingCart(currentUserId, row, mapper));
            sqlSession.commit();
        }

        List<ShoppingCartData> shoppingCartData = shoppingCartQueryMapper.selectShoppingCartDataByCurrentUserAndShopId(currentUserId,
                shoppingCartRows.get(0).getShopId());
        return merge(shoppingCartData);
    }

    public ShoppingCartData deleteGoodsInShoppingCart(Long goodsId) {
        Long userId = UserContext.getCurrentUser().getId();
        Goods goods = goodsMapper.selectByPrimaryKey(goodsId);
        if (goods == null) {
            throw HttpException.notFound("商品未找到:" + goodsId);
        }
        Long shopId = goods.getShopId();
        shoppingCartQueryMapper.deleteGoodsInShoppingCart(userId, goodsId);
        List<ShoppingCartData> shoppingCartData = shoppingCartQueryMapper.selectShoppingCartDataByCurrentUserAndShopId(userId, shopId);
        return merge(shoppingCartData);
    }

    private ShoppingCartData merge(List<ShoppingCartData> goodsOfSameShop) {
        if (goodsOfSameShop.isEmpty()) {
            return null;
        }

        ShoppingCartData result = new ShoppingCartData();
        result.setShop(goodsOfSameShop.get(0).getShop());
        List<GoodsWithNumber> goods = goodsOfSameShop.stream()
                .map(ShoppingCartData::getGoods)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        result.setGoods(goods);
        return result;
    }

    /**
     * 加购物车:
     * 如果当前用户购物车中已存在相应商品 先删除再重新加到购物车中
     *
     * @param currentUserId 当前登录用户ID
     * @param row           需要添加的购物车记录
     * @param mapper        购物车mapper
     */
    private void insertGoodsToShoppingCart(Long currentUserId, ShoppingCart row, ShoppingCartMapper mapper) {
        ShoppingCartExample example = new ShoppingCartExample();
        example.createCriteria().andUserIdEqualTo(currentUserId).andGoodsIdEqualTo(row.getGoodsId());
        mapper.deleteByExample(example);
        mapper.insert(row);
    }

    private ShoppingCart toShoppingCartRow(ShoppingCartRequestItem item, Map<Long, Goods> idToGoods, Long userId) {
        Goods goods = idToGoods.get(item.getId());
        if (goods == null) {
            throw HttpException.notFound("商品未找到!");
        }
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setGoodsId(goods.getId());
        shoppingCart.setNumber(item.getNumber());
        shoppingCart.setCreatedAt(new Date());
        shoppingCart.setUpdatedAt(new Date());
        shoppingCart.setShopId(goods.getShopId());
        shoppingCart.setStatus(DataStatus.OK.getName());
        shoppingCart.setUserId(userId);
        return shoppingCart;
    }

    private boolean isGoodsInSameShop(List<Goods> goods) {
        return goods.stream().map(Goods::getShopId).collect(Collectors.toSet()).size() == 1;
    }

}
