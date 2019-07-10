package com.Fate_Project.Mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ServerMQTT {
    private static final Logger logger = LoggerFactory.getLogger(ServerMQTT.class);
    public static final String HOST = "tcp://47.94.17.3:1883";      //mosquitto代理服务器的ip和端口
    public static final String TOPIC_date = "Sensor";               //接收的主题
    public static final String TOPIC_control = "Terminal/control";  //发布的主题
    public static final String clientid = "Service";                 //定义MQTT的ID，可以在MQTT服务配置中指定
    public static final String TOPIC_relay="relay_return";
    public static final String TOPIC_sensor="sensor_return";
    public static MqttClient client;
    public MqttMessage message;                                     //定义发送的消息
    public static MqttTopic topic_date;
    public static MqttTopic topic_control;

    /*构造函数*/
    public ServerMQTT() throws MqttException, InterruptedException {
        connect();
    }

    /*用来连接服务器*/
    public static void connect() throws MqttException {
        client = new MqttClient(HOST, clientid, new MemoryPersistence());       //给Client赋值
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);                                          //是否清空上次的会话信息
        options.setConnectionTimeout(10);                                       // 设置超时时间
        options.setKeepAliveInterval(20);                                       // 设置会话心跳时间
        try {
            client.setCallback(new PushCallback());                             //设置回调函数
            client.connect(options);                                            //连接mosquitto服务代理
            logger.info("===========================(连接MQTT)==================================");
            logger.info("      MyMQTT连接MQTT服务器成功！");

            topic_date = client.getTopic(TOPIC_date);                           //将TOPIC_data 强制转换成MqttTopic类型 赋值给topic_data
            topic_control = client.getTopic(TOPIC_control);                     //同上
        } catch (Exception e) {
            e.printStackTrace();
        }
        int[] news = {1};
        String[] topic = {TOPIC_date + "/#"};                                   //要订阅主题  #为通配符  能够匹配当前层或下层
        client.subscribe(topic, news);                                          //订阅
        String[] topic_relay = {TOPIC_relay};
        client.subscribe(topic_relay,news);                                     //主题功能： 控制命令是否控制成功
        String[] topic_sensor = {TOPIC_sensor};
        client.subscribe(topic_sensor);                                         //主题功能： 判断传感器是否存在
    }


    /*发布消息：发送给其他相同主题的人 */
    public void publish(MqttTopic topic, MqttMessage message) throws MqttPersistenceException, MqttException {
        MqttDeliveryToken token = topic.publish(message);
        token.waitForCompletion();
        logger.info( "——发送[" + token.isComplete()+"]——");
    }

}
