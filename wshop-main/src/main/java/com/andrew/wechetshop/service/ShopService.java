package com.andrew.wechetshop.service;

import com.andrew.wechetshop.api.entity.DataStatus;
import com.andrew.wechetshop.api.entity.PagedResponse;
import com.andrew.wechetshop.api.exception.HttpException;
import com.andrew.wechetshop.generate.Shop;
import com.andrew.wechetshop.generate.ShopExample;
import com.andrew.wechetshop.generate.ShopMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class ShopService {
    private final ShopMapper shopMapper;

    public ShopService(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }


    public Shop updateShop(Shop shop) {
        Shop shopInDB = shopMapper.selectByPrimaryKey(shop.getId());
        if (shopInDB == null) {
            throw HttpException.notFound("店铺未找到!");
        }
        if (!Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            throw HttpException.notAuthorized("无权访问!");
        }
        shopMapper.updateByPrimaryKey(shop);
        return shop;
    }

    public Shop createShop(Shop shop) {
        shop.setOwnerUserId(UserContext.getCurrentUser().getId());
        shop.setStatus(DataStatus.OK.getName());
        shop.setCreatedAt(new Date());
        shop.setUpdatedAt(new Date());
        long primaryKey = shopMapper.insert(shop);
        shop.setId(primaryKey);
        return shop;
    }

    public Shop deleteShop(Long id) {
        Shop shop = shopMapper.selectByPrimaryKey(id);
        if (shop == null) {
            throw HttpException.notFound("店铺未找到!");
        }
        if (!Objects.equals(shop.getOwnerUserId(), UserContext.getCurrentUser().getId())) {
            throw HttpException.notAuthorized("无权访问!");
        }
        shop.setStatus(DataStatus.DELETED.getName());
        shopMapper.updateByPrimaryKey(shop);
        return shop;
    }

    public PagedResponse<Shop> getCurrentUsersShop(Integer pageNum, Integer pageSize) {
        Long ownerUserId = UserContext.getCurrentUser().getId();
        long count = countShop(ownerUserId);
        int totalPage = (int) (count % pageSize == 0 ? count / pageSize : count / pageSize + 1);
        ShopExample pageCondition = new ShopExample();
        pageCondition.createCriteria()
                .andOwnerUserIdEqualTo(ownerUserId)
                .andStatusEqualTo(DataStatus.OK.getName());
        pageCondition.setOffset((pageNum - 1) * pageSize);
        pageCondition.setLimit(pageSize);
        List<Shop> shops = shopMapper.selectByExample(pageCondition);
        return PagedResponse.pagedDataByParam(pageNum, pageSize, totalPage, shops);
    }

    private long countShop(Long ownerUserId) {
        ShopExample shopExample = new ShopExample();
        shopExample.createCriteria()
                .andStatusEqualTo(DataStatus.OK.getName())
                .andOwnerUserIdEqualTo(ownerUserId);
        return shopMapper.countByExample(shopExample);
    }
}
