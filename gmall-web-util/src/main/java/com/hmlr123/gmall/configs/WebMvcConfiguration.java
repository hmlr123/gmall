package com.hmlr123.gmall.configs;

import com.hmlr123.gmall.interceptors.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 将拦截器加入到我们的配置类中.
 * 这里可以添加很多前置操作
 *
 * @author liwei
 * @date 2019/9/12 15:17
 */
@Configuration
public class WebMvcConfiguration  extends WebMvcConfigurerAdapter {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器，定义拦截范围
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
