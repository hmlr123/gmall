package com.hmlr123.gmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.hmlr123.gmall.bean.PmsSearchParam;
import com.hmlr123.gmall.bean.PmsSearchSkuInfo;
import com.hmlr123.gmall.bean.PmsSkuAttrValue;
import com.hmlr123.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liwei
 * @date 2019/9/8 16:38
 */
@Service(
        timeout = 1200000
)
public class SearchServiceImpl implements SearchService {


    /**
     * es客户端.
     */
    @Autowired
    private JestClient jestClient;

    /**
     * 搜索服务,根据搜索条件从es获取数据.
     *
     * @param pmsSearchParam 搜索条件 三级分类id 关键字 平台属性值id集合
     * @return
     */
    @Override
    public List<PmsSearchSkuInfo> list(PmsSearchParam pmsSearchParam) {
        String searchDsl = getSearchDsl(pmsSearchParam);

        List<PmsSearchSkuInfo> pmsSearchSkuInfoList = new ArrayList<>();
        //api执行复杂查询
        Search build = new Search.Builder(searchDsl).addIndex("gmall").addType("PmsSkuInfo").build();

        SearchResult searchResult = null;
        try {
            //执行dsl语句
            searchResult = jestClient.execute(build);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取dsl执行结果
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
        //执行结果映射
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo pmsSearchSkuInfo = hit.source;
            // 覆盖高亮数据
            Map<String, List<String>> highlight = hit.highlight;
            if (null != highlight) {
                String skuName = highlight.get("skuName").get(0);
                pmsSearchSkuInfo.setSkuName(skuName);
            }
            pmsSearchSkuInfoList.add(pmsSearchSkuInfo);
        }

        return pmsSearchSkuInfoList;
    }


    /**
     * 获取搜素语句，f封装工具类.
     *
     * @param pmsSearchParam 搜索条件.
     * @return 搜索语句
     */
    private String getSearchDsl(PmsSearchParam pmsSearchParam) {
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] valueIds = pmsSearchParam.getValueId();

        //jest的dsl工具类
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //bool
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        //过滤条件
        if (StringUtils.isNotBlank(catalog3Id)) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termQueryBuilder);
        }

        //模糊查询关键字
        if (StringUtils.isNotBlank(keyword)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
        }

        //过滤平台属性值
        if (null != valueIds) {
            for (String valueId : valueIds) {
                TermQueryBuilder termQueryBuilder = new TermQueryBuilder("skuAttrValueList.valueId", valueId);
                boolQueryBuilder.filter(termQueryBuilder);
            }
        }

        //查询语句
        searchSourceBuilder.query(boolQueryBuilder);
        //开始
        searchSourceBuilder.from(0);
        //数量
        searchSourceBuilder.size(20);
        //高亮
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置样式
        highlightBuilder.preTags("<span style='color:red;'>");
        highlightBuilder.field("skuName");
        highlightBuilder.postTags("<span/>");
        searchSourceBuilder.highlight(highlightBuilder);
        //排序
//        searchSourceBuilder.sort("id",SortOrder.DESC);

        return searchSourceBuilder.toString();
    }
}
