package com.hmlr123.gmall.interceptors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hmlr123.gmall.annotations.LoginRequired;
import com.hmlr123.gmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import com.hmlr123.gmall.util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录拦截器,组件.
 *
 * @author liwei
 * @date 2019/9/12 15:17
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    /**
     * 拦截的具体操作.
     *
     * @param request
     * @param response
     * @param handler   处理
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        LoginRequired methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
        //不用登录验证也能使用 游客登陆
        if (null == methodAnnotation) {
            return true;
        }

        String token = "";

        //主动登录，保存到cookie的内容
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        if (StringUtils.isNotBlank(oldToken)) {
            token = oldToken;
        }

        //被动登录产生的token
        String newToken = request.getParameter("token");
        if (StringUtils.isNotBlank(newToken)) {
            token = newToken;
        }

        //验证token
        String success = "fail";
        Map<String, String> successMap = new HashMap<>();
        if (StringUtils.isNotBlank(token)) {
            String successJson = HttpclientUtil.doGet("http://passport.gmall.hmlr123.com/verify?token=" + token + "&currentIp=" + getIp(request));
            successMap = JSON.parseObject(successJson, Map.class);
            success = successMap.get("status");
        }

        //判断是否需要登陆验证， 验证是否通过
        boolean loginSuccess = methodAnnotation.loginSuccess();
        if (loginSuccess && !"success".equals(success)) {
            //需要登录成功 才能使用，验证用户信息
            //验证不通过 驳回登录界面 带上当前请求Url
            StringBuffer requestURL = request.getRequestURL();
            //重定向
            response.sendRedirect("http://passport.gmall.hmlr123.com/index?returnUrl=" + requestURL);
            return false;
        }

        //验证通过 逆向思考过程， 代码更简洁， 从小到大，短板原理
        if ("success".equals(success)) {
            // 需要将token携带的用户信息写入
            request.setAttribute("memberId", successMap.get("memberId"));
            request.setAttribute("nickname", successMap.get("nickname"));
            // 验证通过，覆盖cookie中的token
            if(StringUtils.isNotBlank(token)){
                CookieUtil.setCookie(request,response,"oldToken",token,60*60*2,true);
            }
        }

        return true;

    }


    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip)) {
            ip = request.getRemoteAddr();
            if (StringUtils.isBlank(ip)) {
                //这里说明有问题，需要处理
                ip = "127.0.0.1";
            }
        }
        return ip;
    }


}
