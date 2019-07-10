package com.Fate_Project;

import com.Fate_Project.Inflxdb.ServerInflxdb;
import com.Fate_Project.Mqtt.ServerMQTT;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocket;


@EnableWebSocket  				//开启对WebSocket的支持
@EnableAsync					//开启异步线程的支持
@SpringBootApplication
public class FateProjectApplication {
	//用来判断 用户控制设备命令 是否成功
	public static int FLAG = -1;
	//用来判断 发送报警邮件的冷却时间
	public static Boolean FLAG_EMAIL = true;
	//定义 Pub_service 与 MQTT 的连接
	public static ServerMQTT Pub_service;
	//定义 MyInflxDB 与 Influxdb 的连接
	public static ServerInflxdb MyInflxDB = new ServerInflxdb("root","123456","http://47.94.17.3:8086","Fate_project");

	public static void main(String[] args) throws MqttException, InterruptedException {
		MyInflxDB.influxdbBuild();									// MyInflxDB   连接 Inflxdb 服务器
		Pub_service = new ServerMQTT();								// Pub_service 连接 MQTT的mosquitto代理
		SpringApplication.run(FateProjectApplication.class, args);

	}

}
