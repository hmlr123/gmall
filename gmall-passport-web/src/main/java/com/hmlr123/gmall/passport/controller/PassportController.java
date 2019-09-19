package com.hmlr123.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hmlr123.gmall.annotations.LoginRequired;
import com.hmlr123.gmall.bean.UmsMember;
import com.hmlr123.gmall.passport.utils.Constant;
import com.hmlr123.gmall.service.UserService;
import com.hmlr123.gmall.util.HttpclientUtil;
import com.hmlr123.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liwei
 * @date 2019/9/12 11:18
 */
@Controller
public class PassportController {

    @Reference
    private UserService userService;


    @RequestMapping("index")
//    @LoginRequired(loginSuccess = false)
    public String index(String returnUrl, ModelMap modelMap) {
        modelMap.put("returnUrl", returnUrl);
        return "index";
    }

    /**
     * 登录.
     * 需要同步cookie购物车数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("login")
    @ResponseBody
    public String login(UmsMember umsMember, HttpServletRequest request, HttpServletResponse response) {
        //验证 用户信息 生成token
        UmsMember user = userService.login(umsMember);
        String token = "";
        if (null != user) {
            //生成token
            token = generalToken(user.getId(), user.getNickname(), request);
        } else {
            token = "fail";
        }
        return token;
    }

    /**
     * token验证.
     *
     * @param token         token
     * @param currentIp     原始请求ip
     * @return
     */
    @RequestMapping("verify")
    @ResponseBody
    public String verify(String token, String currentIp) {
        //token放在请求url中不安全且容易溢出，最好放到请求头中，且隐藏token

        //验证token
        Map<String, Object> decode = JwtUtil.decode(token, "hmlr123", currentIp);
        Map<String, String> map = new HashMap<>();
        if (null != decode) {
            map.put("memberId", (String) decode.get("memberId"));
            map.put("nickname", (String) decode.get("nickname"));
            map.put("status", "success");
        } else {
            map.put("status", "fail");
        }
        return JSON.toJSONString(map);
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


    /**
     * 第三方登录.
     *
     * @param code  许可码
     * @return
     */
    @RequestMapping("vlogin")
    private String vlogin(String code, HttpServletRequest request) {
        String token = "";
        ///封装请求数据
        String url = Constant.ACCESS_TOKEN_URL;
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("client_id", Constant.APP_KEY);
        paramMap.put("client_secret", Constant.APP_SECRET);
        paramMap.put("grant_type", "authorization_code");
        paramMap.put("redirect_uri", Constant.REDIRECT_URL);
        paramMap.put("code", code);

        String accessToken = HttpclientUtil.doPost(url, paramMap);
//        if (StringUtils.isNotBlank(accessToken)) {
            Map<String, String> parseObject = JSONObject.parseObject(accessToken, Map.class);
            //获取许可token
            String access_token = parseObject.get("access_token");
            String uid = parseObject.get("uid");
            //许可token获取用户信息
            String userInfoUrl = Constant.USER_INFO_URL + "?access_token=" + access_token + "&uid=" + uid;
            String doGet = HttpclientUtil.doGet(userInfoUrl);
            Map<String, Object> userInfo = JSON.parseObject(doGet, Map.class);

            Long id = (Long) userInfo.get("id");
            //检查该用户是否已经注册过
            UmsMember umsMember = userService.checkUser(id);
            if (null == umsMember) {
                umsMember = new UmsMember();
                //获取有用的用户信息
                String location = (String) userInfo.get("location");
                String nickName = (String) userInfo.get("screen_name");
                String gender = (String) userInfo.get("gender");

                //封装用户信息
                if ("m".equals(gender)) {
                    umsMember.setGender(1);
                } else if ("f".equals(gender)) {
                    umsMember.setGender(2);
                } else if ("n".equals(gender)) {
                    umsMember.setGender(0);
                }
                umsMember.setCity(location);
                umsMember.setNickname(nickName);
                umsMember.setSourceId(id);
                umsMember.setSourceType(1);
                umsMember.setAccessToken(access_token);
                umsMember.setAccessCode(code);
                umsMember.setCreateTime(new Date());

                //保存用户信息 下面两种方法可以合并.
                userService.addUser(umsMember);
                //查询
                umsMember = userService.checkUser(id);
            }
            //生成token
            token = generalToken(umsMember.getId(), umsMember.getNickname(), request);
//        }




        //重定向到首页
        return "redirect:Http://search.gmall.hmlr123.com/index?token=" + token;
    }


    /**
     * 生成token.
     *
     * @param memberId
     * @param nickname
     */
    private String generalToken(String memberId, String nickname, HttpServletRequest request) {
        String token = "";
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("memberId", memberId);
        userMap.put("nickname", nickname);

        //获取Ip 获取nginx转发的客户端ip
        String ip = getIp(request);

        //加密 盐可以使用ip + time（过期时间） 避免存入token
        token = JwtUtil.encode("hmlr123", userMap, ip);

        //token存到redis中
        userService.addToken(token, memberId);
        return token;
    }




}
