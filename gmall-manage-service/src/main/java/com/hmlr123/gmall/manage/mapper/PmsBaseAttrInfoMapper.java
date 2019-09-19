package com.hmlr123.gmall.manage.mapper;

import com.hmlr123.gmall.bean.PmsBaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName: PmsBaseAttrInfoMapper
 * @Description: TODO
 * @Author: liwei
 * @Date: 2019/8/4 0:35
 * @Version: 1.0
 */
public interface PmsBaseAttrInfoMapper extends Mapper<PmsBaseAttrInfo> {

    /**
     * 获取平台属性集合
     *
     * @param join          平台属性值id集合
     * @return              平台属性集合
     */
    List<PmsBaseAttrInfo> getAttrValueListByValueId(@Param("join") String join);
}
