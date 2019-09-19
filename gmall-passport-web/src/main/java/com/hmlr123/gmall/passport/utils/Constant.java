package com.hmlr123.gmall.passport.utils;

/**
 * 常量类.
 *
 * @author liwei
 * @date 2019/9/13 19:58
 */
public class Constant {

    //App Key
    public final static String APP_KEY = "4007185819";

    //App Secret
    public final static String APP_SECRET = "31a89f05242bb0f1cb86c8bb79609a6d";

    //请求登录回调地址
    public final static String REDIRECT_URL = "http://passport.gmall.hmlr123.com/vlogin";

    //请求许可token的地址 需要拼接
    public final static String ACCESS_TOKEN_URL = "https://api.weibo.com/oauth2/access_token";

    //获取用户信息
    public final static String USER_INFO_URL = "https://api.weibo.com/2/users/show.json";
}
