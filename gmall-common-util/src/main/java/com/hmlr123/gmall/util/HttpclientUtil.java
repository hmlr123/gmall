package com.hmlr123.gmall.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Http请求工具类.
 *
 * @author liwei
 * @date 2019/9/12 18:15
 */
public class HttpclientUtil {

    public static String doGet(String url) {
        //创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建htp Get请求
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "UTF8");
                EntityUtils.consume(entity);
                httpClient.close();
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public static String doPost(String url, Map<String, String> paramMap) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;

        try {
            //处理数据
            List<BasicNameValuePair> basicNameValuePairList = new ArrayList<>();
            for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                basicNameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            HttpEntity httpEntity = new UrlEncodedFormEntity(basicNameValuePairList, "UTF-8");
            httpPost.setEntity(httpEntity);

            //执行
            response = httpClient.execute(httpPost);
            //判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "UTF8");
                EntityUtils.consume(entity);
                httpClient.close();
                return result;
            }
            httpClient.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;

    }

}
