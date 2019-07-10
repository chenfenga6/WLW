package com.Fate_Project.Mqtt;

 import com.Fate_Project.Dao.TerminalDao;
 import com.Fate_Project.Entity.Terminal;
 import com.Fate_Project.Thread.AsyncService;
 import com.Fate_Project.Websocket.WebSocketServer;
 import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
 import org.eclipse.paho.client.mqttv3.MqttCallback;
 import org.eclipse.paho.client.mqttv3.MqttException;
 import org.eclipse.paho.client.mqttv3.MqttMessage;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.mail.SimpleMailMessage;
 import org.springframework.mail.javamail.JavaMailSender;
 import org.springframework.scheduling.annotation.Async;
 import org.springframework.scheduling.annotation.EnableAsync;
 import org.springframework.stereotype.Component;
 import javax.annotation.PostConstruct;
 import javax.annotation.Resource;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.HashMap;
 import java.util.Map;
 import static com.Fate_Project.FateProjectApplication.*;

 @EnableAsync
public class PushCallback implements MqttCallback {

    private static final Logger logger = LoggerFactory.getLogger(PushCallback.class);
    private static float Sen_wmax=0,Sen_tmax=0,Sen_hmax=0,Sen_amax=0,Sen_phtempmax=0,Sen_phmax=0,Sen_phmvmax=0,Sen_watempmax=0,Sen_dmax=0;
    private static float Sen_wmin=0,Sen_tmin=0,Sen_hmin=0,Sen_amin=0,Sen_phtempmin=0,Sen_phmin=0,Sen_phmvmin=0,Sen_watempmin=0,Sen_dmin=0;
    private static Boolean WARN_FLAG = true;
    /*掉线重连的回调函数*/
    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("ConnectLost :"+throwable);
        while (true) {
            try {
                logger.error("MQTT [连接断开]，正在进行重连...");
                Thread.sleep(10000);
                ServerMQTT.connect();
                break;
            } catch (MqttException | InterruptedException e) {
                e.printStackTrace();
                continue;
            }
        }

    }


    public static int i = 1;      //计数接收到的消息条数
    @Override
    /*接收信息成功回调函数*/
    public void messageArrived(String topic, MqttMessage message) throws Exception,InterruptedException  {
        // 订阅后得到的消息会执行到这里面
        WARN_FLAG = true;
        String str = new String(message.getPayload());
//        logger.trace("===========================(MQTT收到订阅主题的消息)==================================");
//        logger.trace("——第"+ i++ +"条——\n");
//        logger.trace("接收消息主题 : " + topic);
//        logger.trace("接收消息内容 : " + str+"\n");
//        System.out.println("(MQTT收到订阅主题的消息)");
//        System.out.println("——第"+ i++ +"条——\n");
//        System.out.println("接收消息主题 : " + topic);
//        System.out.println("接收消息内容 : " + str+"\n");

        /*获取当前时间*/
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String HMS = (dateFormat.format(date)).split(" ")[1];       //只获取时间的HH:mm:ss

        /*WebSocket主动推送信息*/
        WebSocketServer.sendInfo(HMS+" "+topic+" "+str);


        /*发送报警邮件*/
        Terminal terminalinfo = new Terminal();
        terminalinfo = SaveData.saveData.selectTerminal(Integer.parseInt(str.split(" ")[0]));
        if (terminalinfo.getWarn().equals("0")){
            WARN_FLAG = false;
            logger.info("设备["+str.split(" ")[0]+"]"+"  关闭了报警功能");
        }
        if(WARN_FLAG && FLAG_EMAIL){                     //判断用户是否开启报警功能 && 时间间隔是否冷却完成
//            logger.info("设备["+str.split(" ")[0]+"]"+"  开启了报警功能");
            setMax(terminalinfo);
            switchWarning(topic,str,terminalinfo.getTuser());
        }

        /*判断控制命令是否控制成功*/
        if (topic.equals("relay_return")){
            FLAG =Integer.parseInt(str.split(" ")[1]);
        }else if (topic.equals("sensor_return")){
            String websocketinfo ="SENSOR "+topic+" "+str;
            switch (str.split(" ")[1]){
                case "wind":{
                    WebSocketServer.sendInfo(websocketinfo);        //格式：“SENSOR”+”空格”+”主题”+”空格”+”tid”+”空格”+”传感器”+”空格”+”开/关”
                };break;
                case "gas":{
                    WebSocketServer.sendInfo(websocketinfo);
                };break;
                case "oxygen":{
                    WebSocketServer.sendInfo(websocketinfo);
                };break;
                case "ph":{
                    WebSocketServer.sendInfo(websocketinfo);
                };break;
            }
        }
        /*接收到的信息格式为：”tid”+”空格”+“数据1”+”空格”+“数据2”+”空格”+“数据3”*/
        /* 将获取到的数据存储到数据库*/
        Map<String, String> tag = new HashMap<>();
        Map<String, Object> field = new HashMap<>();
        if (topic.equals("Sensor/wind_data")) {
            tag.clear();
            field.clear();
            tag.put("tid", str.split(" ")[0]);          /*插入tag为tid的数据,str.split(" ")[0]:以空格为分隔符，
                                                                取第0个字段,（即开头到第一个空格之间的String）*/
            field.put("wind", str.split(" ")[1]);       //插入field为wind的数据
//            logger.warn("向Influxdb数据库[wind_data]中插入数据，[tid = "+tag.get("tid")+"][wind ="+field.get("wind")+" ]");
            MyInflxDB.insert("wind_data", tag, field);  //插入数据库（表名，tag，field）
        }
        else if (topic.equals("Sensor/air_data")) {
            tag.clear();
            field.clear();
            tag.put("tid", str.split(" ")[0]);          //取tid
            field.put("temp", str.split(" ")[1]);       //取field
            field.put("hum", str.split(" ")[2]);
            field.put("amm", str.split(" ")[3]);
//            logger.warn("向Influxdb数据库[air_data]中插入数据，[tid = "+tag.get("tid")+"][temp ="+field.get("temp")+" ][hum="+field.get("hum")+"][amm="+field.get("amm")+"]");
            MyInflxDB.insert("air_data", tag, field);
        }
        else if(topic.equals("Sensor/ph_data")){
            tag.clear();
            field.clear();
            tag.put("tid",str.split(" ")[0]);
            field.put("phtemp",str.split(" ")[1]);
            field.put("ph",str.split(" ")[2]);
            field.put("phmv",str.split(" ")[3]);
//            logger.warn("向Influxdb数据库[ph_data]中插入数据，[tid = "+tag.get("tid")+"][phtemp ="+field.get("phtemp")+" ][ph="+field.get("ph")+"][phmv="+field.get("phmv")+"]");
            MyInflxDB.insert("ph_data",tag,field);
        }
        else if(topic.equals("Sensor/diss_data")){
            tag.clear();
            field.clear();
            tag.put("tid",str.split(" ")[0]);
            field.put("watemp",str.split(" ")[1]);
            field.put("diss",str.split(" ")[2]);
//            logger.warn("向Influxdb数据库[diss_data]中插入数据，[tid = "+tag.get("tid")+"][watemp ="+field.get("watemp")+" ][diss="+field.get("diss")+"]");
            MyInflxDB.insert("diss_data",tag,field);
        }

    }

    /*判断信息是否发送成功回调*/
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        //System.out.println("——消息发送成功-----\n" + token.isComplete());
    }

    /*设置当前的传感器阈值*/
    public void setMax(Terminal t){
        Sen_wmin = Float.parseFloat (t.getWmax().split("-")[0]);
        Sen_wmax = Float.parseFloat (t.getWmax().split("-")[1]);

        Sen_hmin = Float.parseFloat (t.getHmax().split("-")[0]);
        Sen_hmax = Float.parseFloat (t.getHmax().split("-")[1]);

        Sen_tmin = Float.parseFloat (t.getTmax().split("-")[0]);
        Sen_tmax = Float.parseFloat (t.getTmax().split("-")[1]);

        Sen_amin = Float.parseFloat (t.getAmax().split("-")[0]);
        Sen_amax = Float.parseFloat (t.getAmax().split("-")[1]);

        Sen_watempmin = Float.parseFloat (t.getWatempmax().split("-")[0]);
        Sen_watempmax = Float.parseFloat (t.getWatempmax().split("-")[1]);

        Sen_dmin = Float.parseFloat (t.getDmax().split("-")[0]);
        Sen_dmax = Float.parseFloat (t.getDmax().split("-")[1]);

        Sen_phmin = Float.parseFloat (t.getPhmax().split("-")[0]);
        Sen_phmax = Float.parseFloat (t.getPhmax().split("-")[1]);

        Sen_phtempmin = Float.parseFloat (t.getPhtempmax().split("-")[0]);
        Sen_phtempmax = Float.parseFloat (t.getPhtempmax().split("-")[1]);

        Sen_phmvmin = Float.parseFloat (t.getPhmvmax().split("-")[0]);
        Sen_phmvmax = Float.parseFloat (t.getPhmvmax().split("-")[1]);
    }

    /**
     * 判断是否超过阈值，并发送邮件
     */
    public void switchWarning(String topic,String str,String uemail) throws InterruptedException {
        String sensor="",envir="",value="";
        switch (topic){
            case "Sensor/wind_data":{
                if( Float.parseFloat(str.split(" ")[1]) > Sen_wmax  && WARN_FLAG){
                    SaveData.saveData.sendWarnEmail(uemail,"风速传感器","风速",str.split(" ")[1]);
                }
            };break;
            case "Sensor/ph_data":{
                sensor="";envir="";value="";
                if (Float.parseFloat(str.split(" ")[1]) >Sen_phtempmax ){
                    envir = "PH温度 ";
                    value = str.split(" ")[1]+" ";
                }
                if(Float.parseFloat(str.split(" ")[2]) >Sen_phmax ){
                    envir +="PH ";
                    value +=str.split(" ")[2]+" ";
                }
                if (Float.parseFloat(str.split(" ")[3]) >Sen_phmvmax ){
                    envir +="PHmv ";
                    value +=str.split(" ")[3]+" ";
                }
                if (!envir.equals("")){
                    sensor = "PH传感器";
                    SaveData.saveData.sendWarnEmail(uemail,sensor,envir,value);
                }
            };break;
            case "Sensor/air_data":{
                sensor="";envir="";value="";
                if ( Float.parseFloat(str.split(" ")[1]) >Sen_tmax ){
                    envir = "温度 ";
                    value = str.split(" ")[1]+" ";
                }
                if( Float.parseFloat(str.split(" ")[2]) >Sen_hmax  ){
                    envir +="湿度 ";
                    value +=str.split(" ")[2]+" ";
                }
                if ( Float.parseFloat(str.split(" ")[3]) >Sen_amax ){
                    envir +="氨气 ";
                    value +=str.split(" ")[3]+" ";
                }
                if ( !envir.equals("")){
                    sensor = "空气三合一传感器";
                    SaveData.saveData.sendWarnEmail(uemail,sensor,envir,value);
                }
            };break;
            case "Sensor/diss_data":{
                sensor="";envir="";value="";
                if (Float.parseFloat(str.split(" ")[1]) >Sen_watempmax ){
                    envir = "水温 ";
                    value = str.split(" ")[1]+" ";
                }
                if(Float.parseFloat(str.split(" ")[2]) >Sen_dmax ){
                    envir +="溶氧值 ";
                    value +=str.split(" ")[2]+" ";
                }
                if (!envir.equals("")){
                    sensor = "溶氧度传感器";
                    SaveData.saveData.sendWarnEmail(uemail,sensor,envir,value);
                }
            };break;
        }
    }

    /**
     * 解决了在PushCallbcak中无法对数据库和邮件操作的问题
     */
    @Component
    public static class SaveData{
        @Resource
        private TerminalDao terminalDao;
        @Autowired
        private JavaMailSender javaMailSender;
        @Autowired
        private AsyncService asyncService;

        private static SaveData saveData;
        @PostConstruct
        public void init(){
            saveData = this;
            saveData.terminalDao = this.terminalDao;
            saveData.javaMailSender = javaMailSender;
        }

        public Terminal selectTerminal(int tid){
            return terminalDao.Select_byTid(tid);
        }

        @Async
        public void sendWarnEmail(String uemail,String sensor,String envir ,String value) throws InterruptedException {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("物联网<chen1327261488@163.com>");
            mailMessage.setTo(uemail);
            mailMessage.setSubject(sensor+"传感器异常警报！");
            mailMessage.setText(envir+"的值超出了阈值范围！当前值分别为："+value);
            javaMailSender.send(mailMessage);

            logger.warn("===========================(发送报警邮件)==================================");
            logger.warn("主题："+sensor+"传感器异常警报！");
            logger.warn("内容："+envir+"值超出了阈值范围！当前值分别为："+value);
            FLAG_EMAIL = false;
            asyncService.executeAsync();
            logger.warn("发送警报进入冷却！");
        }
    }

}
