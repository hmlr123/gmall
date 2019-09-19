package com.hmlr123.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.hmlr123.gmall.bean.OmsCartItem;
import com.hmlr123.gmall.cart.mapper.OmsCartItemMapper;
import com.hmlr123.gmall.service.CartService;
import com.hmlr123.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liwei
 * @date 2019/9/11 17:07
 */
@Service(
        timeout = 1200000
)
public class CartServiceImpl implements CartService {


    /**
     * 购物车持久类
     */
    @Autowired
    private OmsCartItemMapper cartItemMapper;

    /**
     * Redis工具类
     */
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 添加到购物车.
     *
     * @param omsCartItemFromDb 购物车商品实例
     */
    @Override
    public void addCart(OmsCartItem omsCartItemFromDb) {
        if (StringUtils.isNotBlank(omsCartItemFromDb.getMemberId())) {
            //避免添加空值
            cartItemMapper.insertSelective(omsCartItemFromDb);
        }
    }

    /**
     * 更新购物车.
     *
     * @param omsCartItemFromDb 购物车商品实例
     */
    @Override
    public void updateCart(OmsCartItem omsCartItemFromDb) {
        Example example = new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("id", omsCartItemFromDb.getId());
        cartItemMapper.updateByExample(omsCartItemFromDb, example);
    }

    /**
     * 刷新缓存.
     *
     * @param memberId 用户id
     */
    @Override
    public List<OmsCartItem> flushCartCache(String memberId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memberId);
        List<OmsCartItem> omsCartItems = cartItemMapper.select(omsCartItem);
        Jedis jedis = redisUtil.getJedis();

        Map<String, String> map = new HashMap<>();
        for (OmsCartItem dto : omsCartItems) {
            map.put(dto.getProductSkuId(), JSON.toJSONString(dto));
        }
        //先清空缓存，再添加
        jedis.del("user:" + memberId + ":cart");
        jedis.hmset("user:" + memberId + ":cart", map);
        jedis.close();
        return omsCartItems;
    }

    /**
     * 获取购物车单个商品数据.
     *
     * @param memeberId 用户id
     * @param skuId     skuid
     * @return
     */
    @Override
    public OmsCartItem getCartByUserSkuId(String memeberId, String skuId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setMemberId(memeberId);
        omsCartItem.setProductSkuId(skuId);
        return cartItemMapper.selectOne(omsCartItem);
    }

    /**
     * 获取用户购物车数据.
     * 涉及到分布式锁内容
     *
     * @param userId 用户id
     * @return
     */
    @Override
    public List<OmsCartItem> cartList(String userId) {

        Jedis jedis = null;
        List<OmsCartItem> omsCartItemList = new ArrayList<>();

        try {
            jedis = redisUtil.getJedis();
            List<String> stringList = jedis.hvals("user:" + userId + ":cart");
            if (null == stringList || 0 == stringList.size()) {
                //从数据库中读取数据
                omsCartItemList = flushCartCache(userId);
            } else {
                //从反序列化数据
                for (String string : stringList) {
                    if (StringUtils.isNotBlank(string)) {
                        OmsCartItem omsCartItem = JSON.parseObject(string, OmsCartItem.class);
                        omsCartItem.setTotalPrice(omsCartItem.getPrice().multiply(new BigDecimal(omsCartItem.getQuantity())));
                        omsCartItemList.add(omsCartItem);
                    }
                }
            }
        } catch (Exception e) {
            //处理异常，记录日志
            e.printStackTrace();
//            String message = e.getMessage();
//            logServer.addError(message);
            return null;
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }

        return omsCartItemList;
    }

    /**
     * 修改用户购物车数据,
     *
     * @param omsCartItem   修改的购物车数据.
     */
    @Override
    public void updateCartByProductSkuId(OmsCartItem omsCartItem) {
        Example example = new Example(OmsCartItem.class);
        //这里使用属性名称
        example.createCriteria().andEqualTo("memberId", omsCartItem.getMemberId())
                .andEqualTo("productSkuId", omsCartItem.getProductSkuId());
        cartItemMapper.updateByExampleSelective(omsCartItem, example);
        //刷新数据.
        flushCartCache(omsCartItem.getMemberId());
    }

    /**
     * 删除购物车数据.
     *
     * @param productId
     */
    @Override
    public void deleteCart(String productId) {
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setProductId(productId);
        cartItemMapper.delete(omsCartItem);
    }

    /**
     * 异步修改购物车数据
     *
     * @param omsCartItem
     */
    @Override
    public void checkCart(OmsCartItem omsCartItem) {
        Example e = new Example(OmsCartItem.class);
        e.createCriteria().andEqualTo("memberId",omsCartItem.getMemberId()).andEqualTo("productSkuId",omsCartItem.getProductSkuId());
        cartItemMapper.updateByExampleSelective(omsCartItem,e);
        // 缓存同步
        flushCartCache(omsCartItem.getMemberId());
    }
}
