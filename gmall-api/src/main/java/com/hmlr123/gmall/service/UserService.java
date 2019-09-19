package com.hmlr123.gmall.service;

import com.hmlr123.gmall.bean.UmsMember;
import com.hmlr123.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

/**
 * @ClassName: UserService
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/7/23 23:00
 * @Version: 1.0
 */
public interface UserService {

    /**
     * 获取用户列表
     * @return
     */
    List<UmsMember> selectUsers();

    List<UmsMemberReceiveAddress> getReceiveAddressByMemeberId(String memberId);

    /**
     * 用户登录验证.
     *
     * @param umsMember
     * @return
     */
    UmsMember login(UmsMember umsMember);


    /**
     * 将token存储到redis
     *
     * @param token
     * @param memberId
     */
    void addToken(String token, String memberId);

    /**
     * 检查用户信息.
     *
     * @param uid
     * @return
     */
    UmsMember checkUser(Long uid);

    /**
     * 添加用户信息
     *
     * @param user
     */
    void addUser(UmsMember user);

    /**
     * 获取收获地址
     *
     * @param receiveAddressId 地址id
     * @return
     */
    UmsMemberReceiveAddress getReceiveAddressById(String receiveAddressId);
}
