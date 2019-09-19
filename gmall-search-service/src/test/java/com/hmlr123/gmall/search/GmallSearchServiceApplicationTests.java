package com.hmlr123.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.hmlr123.gmall.bean.PmsSearchSkuInfo;
import com.hmlr123.gmall.bean.PmsSkuInfo;
import com.hmlr123.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchServiceApplicationTests {

    @Reference
    SkuService skuService;

    @Autowired
    JestClient jestClient;

    @Test
    public void contextLoads() throws IOException {
        // 查询数据库
        List<PmsSkuInfo> pmsSkuInfoList = skuService.getAllSkuInfo(null);
        // 转化成es数据结构
        List<PmsSearchSkuInfo> pmsSearchSkuInfoList = new ArrayList<>();

        // 数据字段属性映射
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(pmsSkuInfo, pmsSearchSkuInfo);
            pmsSearchSkuInfoList.add(pmsSearchSkuInfo);
        }

        //导入es
        for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfoList) {
            //数据 索引 类型 主键
            Index build = new Index.Builder(pmsSearchSkuInfo).index("gmall").type("PmsSkuInfo")
                    .id(pmsSearchSkuInfo.getId()).build();
            jestClient.execute(build);
        }

    }

}
