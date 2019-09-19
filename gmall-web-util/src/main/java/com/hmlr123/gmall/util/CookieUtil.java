package com.hmlr123.gmall.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Cookie工具类
 *
 * @author liwei
 * @date 2019/9/10 15:55
 */
public class CookieUtil {


    /**
     * 获取cookien内容.
     *
     * @param request       请求
     * @param cookieName    cookie名称
     * @param isDecoder     是否解码
     * @return              cookie内容
     */
    public static String getCookieValue(HttpServletRequest request,
                                        String cookieName,
                                        boolean isDecoder) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies || null == cookieName) {
            return null;
        }
        //返回值
        String retValue = null;
        try {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    if (isDecoder) { //中文解码
                        //中文编码
                        retValue = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    } else {
                        retValue = cookie.getValue();
                    }
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return retValue;
    }

    /**
     * 保存Cookie
     *
     * @param request       请求
     * @param response      响应
     * @param cookieName    cookie名称
     * @param cookieValue   cookie内容
     * @param cookieMaxage  cookie最大保存时间
     * @param isEncode      是否编码
     */
    public static void setCookie(HttpServletRequest request,
                                 HttpServletResponse response,
                                 String cookieName,
                                 String cookieValue,
                                 int cookieMaxage,
                                 boolean isEncode) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else if (isEncode) {
                cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            if (cookieMaxage >= 0)
                cookie.setMaxAge(cookieMaxage);
            if (null != request)// 设置域名的cookie
                cookie.setDomain(getDomainName(request));
            // 在域名的根路径下保存
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取域名.
     *
     * @param request 请求
     * @return
     */
    private static final String getDomainName(HttpServletRequest request) {
//        String domainName = null;
//        //获取浏览器URL
//        String serverName = request.getRequestURL().toString();
//        if (null == serverName || "".equals(serverName)) {
//            domainName = "";
//        } else {
//            serverName = serverName.toLowerCase();
//            //http:// 后面的内容
//            serverName = serverName.substring(7);
//            //获取第一个出现/的位置
//            final int end = serverName.indexOf("/");
//            //截取域名
//            serverName = serverName.substring(0, end);
//            //分割域名
//            final String[] domains = serverName.split("\\.");
//            int length = domains.length;
////            去除域名中的www
//            for (int i = 0; i< length; i++) {
//                //去除第一个www
//                if (!"www".equals(domains[i])) {
//                    if (null == domainName || "".equals(domainName)) {
//                        domainName = domains[i];
//                    } else {
//                        domainName = domainName + "." + domains[i];
//                    }
//                }
//            }
//        }
//        //这个情况不太理解 避免拆分失败
//        if (null != domainName && domainName.indexOf(":") > 0) {
//            String[] ary = domainName.split("\\:");
//            domainName = ary[0];
//        }
//        System.out.println("domainName = " + domainName);
//        return domainName;


        String domainName = null;
        String serverName = request.getRequestURL().toString();// 获得浏览器地址栏的url
        if (serverName == null || serverName.equals("")) {
            domainName = "";
        } else {
            serverName = serverName.toLowerCase();
            serverName = serverName.substring(7);
            final int end = serverName.indexOf("/");
            serverName = serverName.substring(0, end);
            final String[] domains = serverName.split("\\.");
            int len = domains.length;
            if (len > 3) {
                // www.xxx.com.cn
                domainName = domains[len - 3] + "." + domains[len - 2] + "." + domains[len - 1];
            } else if (len <= 3 && len > 1) {
                // xxx.com or xxx.cn
                domainName = domains[len - 2] + "." + domains[len - 1];
            } else {
                domainName = serverName;
            }
        }
        if (domainName != null && domainName.indexOf(":") > 0) {
            String[] ary = domainName.split("\\:");
            domainName = ary[0];
        }
        System.out.println("domainName = " + domainName);
        return domainName;
    }


    /**
     * 删除指定域名下的cookie.
     *
     * @param request       请求
     * @param response      响应
     * @param cookieName    cookie名称
     */
    public static void deleteCookie(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String cookieName) {
        setCookie(request, response, cookieName, null, 0, false);
    }
}

