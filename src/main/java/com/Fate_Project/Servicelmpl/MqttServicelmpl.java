package com.Fate_Project.Servicelmpl;

import com.Fate_Project.Dao.TerminalDao;
import com.Fate_Project.Mqtt.PushCallback;
import com.Fate_Project.Service.MqttService;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.Fate_Project.FateProjectApplication.FLAG;
import static com.Fate_Project.FateProjectApplication.Pub_service;

@Service
public class MqttServicelmpl implements MqttService {
    private static final Logger logger = LoggerFactory.getLogger(MqttServicelmpl.class);
    @Resource
    private TerminalDao terminalDao;
    /**
     * 用户控制设备接口实现类
     * @param constr        控制语句：”终端tid”-”传感器”-”打开/关闭” （以-分割）
     * @return
     */
    public String mqtt_control(String constr) throws UnsupportedEncodingException, MqttException {
        logger.info("===========================(控制传感器和设备)==================================");
        String RULE_Control="\\d+\\-+\\d+\\-+\\d";
        Pattern P = Pattern.compile(RULE_Control);
        Matcher M = P.matcher(constr);
        if(M.matches() == false){
            logger.info("用户控制设备时，输入错误命令!");
            return "请输入正确的控制命令！";
        }
        Pub_service.message = new MqttMessage();
        Pub_service.message.setRetained(false);
        Pub_service.message.setQos(1);
        Pub_service.message.setPayload(constr.getBytes("UTF-8"));
        Pub_service.publish(Pub_service.topic_control,Pub_service.message);

        while (true){
            if (FLAG == 0){
                FLAG = -1;
                return "failed";
            }
            if (FLAG == 1){
                FLAG = -1;
                break;
            }
        }

        logger.warn("控制设备命令："+Pub_service.topic_control+"——内容："+constr+"\n");
        /*改变传感器的状态，同时数据库也进行更新*/
        UPdate_state(constr.split("-")[0],constr.split("-")[1],constr.split("-")[2]);
        return "success";
    }

    public void UPdate_state(String tid,String sensorid,String state) {
        int TID = Integer.parseInt(tid);
        int STATE = Integer.parseInt(state);
        switch (sensorid){
            case "0":{
                logger.warn("用户控制[设备终端] 开/关["+state+"]");
                terminalDao.Updata_tstate_byTid(TID,STATE);
            };break;
            case "1":{
                logger.warn("用户控制[风速传感器] 开/关["+state+"]");
                terminalDao.Updata_wstate_byTid(TID,STATE);
            };break;
            case "2":{
                logger.warn("用户控制[空气三合一传感器] 开/关["+state+"]");
                terminalDao.Updata_astate_byTid(TID,STATE);
            };break;
            case "3":{
                logger.warn("用户控制[溶解氧传感器] 开/关["+state+"]");
                terminalDao.Updata_dstate_byTid(TID,STATE);
            };break;
            case "4":{
                logger.warn("用户控制[PH传感器] 开/关["+state+"]");
                terminalDao.Updata_phstate_byTid(TID,STATE);
            };break;
        }

    }
}
