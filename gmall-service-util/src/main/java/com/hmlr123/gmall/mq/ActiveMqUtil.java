package com.hmlr123.gmall.mq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

import javax.jms.*;

/**
 * @author liwei
 * @date 2019/9/16 17:41
 */
public class ActiveMqUtil {

    private static PooledConnectionFactory pooledConnectionFactory = null;

    /**
     * 初始化连接工厂.
     *
     * @param brokerUrl 负载Url
     * @return
     */
    public ConnectionFactory init(String brokerUrl) {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        //加入连接池
        pooledConnectionFactory=new PooledConnectionFactory(factory);
        //出现异常时重新连接
        pooledConnectionFactory.setReconnectOnException(true);
        pooledConnectionFactory.setMaxConnections(5);
        pooledConnectionFactory.setExpiryTimeout(10000);
        return pooledConnectionFactory;
    }

    /**
     * 获取连接工厂.
     *
     * @return
     */
    public static ConnectionFactory getConnectionFactory(){
        return pooledConnectionFactory;
    }

    /**
     * 创建连接.
     *
     * @return
     * @throws JMSException
     */
    public static Connection getConnection() throws JMSException {
        return getConnectionFactory().createConnection();
    }


    /**
     * 发送消息 队列模式
     *
     * @param theme         主题
     * @param message       消息
     * @param sessionStatus 会话模式
     */
    public static void sendMessage(String theme, Message message, int sessionStatus) {
        //创建连接 会话
        Connection connection = null;
        Session session = null;

        try {
            //获取连接
            connection = getConnectionFactory().createConnection();
            //开启连接
            connection.start();
            //开启事务
            session = connection.createSession(true, sessionStatus);
            //创建队列
            Queue sessionQueue = session.createQueue(theme);
            //创建生产者
            MessageProducer producer = session.createProducer(sessionQueue);
            //发送消息
            producer.send(message);
            //提交事务
            session.commit();
            //关闭生产者
            producer.close();
            //关闭session
            session.close();
        } catch (Exception e) {
            try {
                //失败回滚
                if (null != session) {
                    session.rollback();
                }
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
        } finally {
            try {
                //关闭连接
                if (null != connection) {
                    connection.close();
                }
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
