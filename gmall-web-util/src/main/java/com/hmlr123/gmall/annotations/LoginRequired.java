package com.hmlr123.gmall.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liwei
 * @date 2019/9/12 15:17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
    /**
     * 是否需要登录成功才能使用，默认true.
     *
     * @return
     */
    boolean loginSuccess() default true;
}
