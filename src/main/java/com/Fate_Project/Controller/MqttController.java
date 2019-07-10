package com.Fate_Project.Controller;

import com.Fate_Project.AES.AesUtil;
import com.Fate_Project.Service.MqttService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.UnsupportedEncodingException;

@RestController
@CrossOrigin
public class MqttController {
    @Autowired
    protected MqttService mqttService;

    /**
     * 用户控制设备端口
     * @param constr            控制命令："终端tid"-"传感器"-"打开/关闭"
     * @return                  "success"
     * @throws UnsupportedEncodingException
     * @throws MqttException
     */
    @RequestMapping("/mqtt/control")
    public String mqtt_control(@RequestParam String constr) throws Exception {
        constr = AesUtil.decrypt(constr);
        return mqttService.mqtt_control(constr);
    }

}
