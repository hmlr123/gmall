package com.hmlr123.gmall.user.service;

import com.hmlr123.gmall.user.bean.UmsMember;
import com.hmlr123.gmall.user.bean.UmsMemberReceiveAddress;

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
}
