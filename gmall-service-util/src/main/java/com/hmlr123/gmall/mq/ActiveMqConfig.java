package com.hmlr123.gmall.mq;

import com.hmlr123.gmall.util.Constant;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import javax.jms.Session;

/**
 * AvtiveMq消息队列配置类.
 * 1. 初始化消息队列
 * 2.
 *
 * @author liwei
 * @date 2019/9/16 17:36
 */

@Configuration
public class ActiveMqConfig {

    @Value("${spring.activemq.broker-url:disabled}")
    private String brokeURL;

    @Value("${activemq.listener.enable:disabled}")
    private String listenerEnable;

    /**
     * 解决@value从配置文件获取不到值的问题.
     *
     * @return
     */
//    @Bean
//    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
//        return new PropertySourcesPlaceholderConfigurer();
//    }

    /**
     * mq初始化到Spring容器中.
     * Springboot注解不支持静态的变量和方法
     * @return
     */
    @Bean
    public ActiveMqUtil getActiveMqUtil() {
        if ("disabled".equals(brokeURL)) {
            return null;
        }
        ActiveMqUtil activeMqUtil = new ActiveMqUtil();
        activeMqUtil.init(brokeURL);
        return activeMqUtil;
    }

    /**
     * 注入Active连接工厂 项目启动的时候实例化Active连接工厂.
     *
     * @return
     */
    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        return new ActiveMQConnectionFactory(brokeURL);
    }

    /**
     * 队列模式的监听连接工厂.
     * Spring的mq实现：用Spring的JMS监听工厂包装ActiveMQConnectionFactory 实现软件控制权交给我们Spring
     *
     * @param activeMQConnectionFactory
     * @return
     */
    @Bean(name = Constant.JMS_QUEUE_LISTENER)
    public DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory(ActiveMQConnectionFactory activeMQConnectionFactory) {
        //Spring封装的连接工厂
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        if (!"true".equals(listenerEnable)) {
            return null;
        }

        factory.setConnectionFactory(activeMQConnectionFactory);
        factory.setConcurrency("5");
        //重连间隔时间
        factory.setRecoveryInterval(5000L);
        //配置管理本地事务
        factory.setSessionTransacted(false);
        //确认模式
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }
}
