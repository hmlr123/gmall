package com.hmlr123.gmall.user.service.impl;

import com.hmlr123.gmall.bean.UmsMember;
import com.hmlr123.gmall.bean.UmsMemberReceiveAddress;
import com.hmlr123.gmall.service.UserService;
import com.hmlr123.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.hmlr123.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @ClassName: UserServiceImpl
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/7/23 23:00
 * @Version: 1.0
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;


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
}
