package com.hmlr123.gmall.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局获取Http上下文工具类.
 *
 * @author liwei
 * @date 2019/9/6 8:36
 */
public class HttpContextUtils {

    /**
     * 全局获取request.
     *
     * @return  HttpServletRequest
     */
    public static HttpServletRequest getHttpServletRequest() {
        if (null == RequestContextHolder.getRequestAttributes()) {
            return null;
        } else {
            return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        }
    }

    /**
     * 获取域名.
     *
     * @return  域名
     */
    public static String getDomain() {
        HttpServletRequest request = getHttpServletRequest();
        StringBuffer requestURL = request.getRequestURL();
        return requestURL.delete(requestURL.length() - request.getRequestURI().length(), requestURL.length()).toString();
    }

    /**
     * 获取请求源.
     *
     * @return Origin
     */
    public static String getOrigin() {
        HttpServletRequest request = getHttpServletRequest();
        if (null == request) {
            return "*";
        } else {
            return request.getHeader("Origin");
        }
    }

    /**
     * 获取请求IP.
     *
     * @return IP地址
     */
    public static String getIP() {
        HttpServletRequest request = getHttpServletRequest();
        if (null == request) {
            return null;
        } else {
            return request.getRemoteAddr();
        }
    }
}
