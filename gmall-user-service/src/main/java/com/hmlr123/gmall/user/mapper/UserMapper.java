package com.hmlr123.gmall.user.mapper;


import com.hmlr123.gmall.bean.UmsMember;

import java.util.List;

/**
 * @ClassName: UserMapper
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/7/23 23:01
 * @Version: 1.0
 */
public interface UserMapper extends tk.mybatis.mapper.common.Mapper<UmsMember> {

    /**
     * 获取用户列表
     * @return
     */
    List<UmsMember> selectUmsMemeber();
}
