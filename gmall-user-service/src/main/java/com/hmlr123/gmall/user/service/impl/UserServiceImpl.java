package com.hmlr123.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hmlr123.gmall.bean.UmsMember;
import com.hmlr123.gmall.bean.UmsMemberReceiveAddress;
import com.hmlr123.gmall.service.UserService;
import com.hmlr123.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.hmlr123.gmall.user.mapper.UserMapper;
import com.hmlr123.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @ClassName: UserServiceImpl
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/7/23 23:00
 * @Version: 1.0
 */
@Service(
        timeout = 1200000
)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;

    @Autowired
    private RedisUtil redisUtil;


    @Override
    public List<UmsMember> selectUsers() {
        return userMapper.selectAll();
    }

    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByMemeberId(String memberId) {
        Example example = new Example(UmsMemberReceiveAddress.class);
        example.createCriteria().andEqualTo("memberId", memberId);
        List<UmsMemberReceiveAddress> receiveAddresses = umsMemberReceiveAddressMapper.selectByExample(example);

        //封装参数对象
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(memberId);


//        List<UmsMemberReceiveAddress> receiveAddresses = umsMemberReceiveAddressMapper
//                .selectByExample(umsMemberReceiveAddress);
//        List<UmsMemberReceiveAddress> receiveAddresses = umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);
        return receiveAddresses;
    }


    /**
     * 获取用户信息.
     *
     * @param umsMember username, password
     * @return          用户实体数据
     */
    @Override
    public UmsMember login(UmsMember umsMember) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            if (null != jedis) {
                String user = jedis.get("user:" + umsMember.getUsername() + ":info");
                if (StringUtils.isNotBlank(user)) {
                    UmsMember member = JSONObject.parseObject(user, UmsMember.class);
                    if (member.getPassword().equals(umsMember.getPassword())) {
                        return member;
                    }
                }
            }
            //从数据库获取数据
            UmsMember selectOne = loginFromDb(umsMember);
            if (null != selectOne) {
                jedis.setex("user:" + selectOne.getUsername() + ":info", 60 * 60 * 24 ,JSON.toJSONString(selectOne));
                return selectOne;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * 将token存储到redis
     *
     * @param token
     * @param memberId
     */
    @Override
    public void addToken(String token, String memberId) {
        Jedis jedis = redisUtil.getJedis();
        jedis.setex("user:" + memberId + ":token", 60 * 60 * 2, token);
        jedis.close();
    }

    /**
     * 检查用户信息.
     *
     * @param uid
     * @return
     */
    @Override
    public UmsMember checkUser(Long uid) {
        if (null != uid) {
            UmsMember user = new UmsMember();
            user.setSourceId(uid);
            return userMapper.selectOne(user);
        }
        return null;

    }

    @Override
    public void addUser(UmsMember user) {
        userMapper.insert(user);
    }

    @Override
    public UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress = new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setId(receiveAddressId);
        return umsMemberReceiveAddressMapper.selectOne(umsMemberReceiveAddress);
    }

    private UmsMember loginFromDb(UmsMember umsMember) {
        return userMapper.selectOne(umsMember);
    }
}
