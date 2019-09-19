package com.hmlr123.gmall.service;

import com.hmlr123.gmall.bean.OmsCartItem;

import java.util.List;

/**
 * 购物车服务.
 *
 * @author liwei
 * @date 2019/9/11 17:06
 */
public interface CartService {
    /**
     * 添加到购物车.
     *
     * @param omsCartItemFromDb 购物车商品实例
     */
    void addCart(OmsCartItem omsCartItemFromDb);

    /**
     * 更新购物车.
     *
     * @param omsCartItemFromDb 购物车商品实例
     */
    void updateCart(OmsCartItem omsCartItemFromDb);

    /**
     * 刷新缓存.
     *
     * @param memeberId 用户id
     */
    List<OmsCartItem> flushCartCache(String memeberId);

    /**
     * 获取购物车单个商品数据.
     *
     * @param memeberId 用户id
     * @param skuId     skuid
     * @return
     */
    OmsCartItem getCartByUserSkuId(String memeberId, String skuId);

    /**
     * 获取用户购物车数据.
     *
     * @param userId 用户id
     * @return
     */
    List<OmsCartItem> cartList(String userId);

    /**
     * 修改用户购物车数据,返回用户的购物车数据.
     *
     * @param omsCartItem   修改的购物车数据.
     */
    void updateCartByProductSkuId(OmsCartItem omsCartItem);

    /**
     * 删除购物车数据.
     *
     * @param productId
     */
    void deleteCart(String productId);

    /**
     * 修改购物车数据.
     *
     * @param omsCartItem
     */
    void checkCart(OmsCartItem omsCartItem);
}
