package com.hmlr123.gmall.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring容器获取类.
 *
 * @author liwei
 * @date 2019/9/17 10:35
 */
@Component
public class SpringUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext = null;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (null == SpringUtil.applicationContext) {
            SpringUtil.applicationContext = applicationContext;
        }
    }

    /**
     * 全局获取上下文.
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过name获取Bean, 命名风格自定义.
     *
     * @param name  bean名称
     * @return      bean实例
     */
    public static Object getBeanSpringStyle(String name) {
        String beanName = null;
        if (Character.isLowerCase(name.charAt(0))) {
            beanName = name;
        } else if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) {
            beanName = name;
        } else {
            char[] chars = name.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            beanName = new String(chars);
        }
        return getApplicationContext().getBean(beanName);
    }

    /**
     * 通过name获取Bean, 命名风格自定义.
     *
     * @param name  bean名称
     * @return      bean实例
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz 反射类
     * @return Bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean.
     *
     * @param name  bean名字
     * @param clazz 反射类
     * @return Bean实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
