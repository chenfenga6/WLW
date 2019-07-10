package com.Fate_Project.Service;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;

public interface MqttService {
    String mqtt_control(String constr) throws UnsupportedEncodingException, MqttException;                         //用户控制设备回调函数
}
