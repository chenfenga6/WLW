package com.Fate_Project.Service;

import com.Fate_Project.Entity.History;
import com.Fate_Project.Entity.MResult;
import com.Fate_Project.Entity.Terminal;
import org.influxdb.dto.QueryResult;
import java.text.ParseException;
import java.util.List;

public interface LoginService {
    MResult Login(String uacc,String upwd) throws InterruptedException;                         //用户登陆回调
    MResult Registration(String uname, String upwd,String uemail,String uphone,String flag);    //用户注册回调
    String SendEmail(String uemail);                                                            //发送验证码回调
    String chgpswd(String uacc,String newpwd);                                                  //修改密码回调
    String chgbyemail(String uacc,String flag);                                                 //忘记密码(邮箱)回调
    String chgwithEcode(String newupwd,String uacc);                                            //重置密码(邮箱)回调
    String chgEmail(String uacc,String newEmail);                                               //修改Email回调
    String chgPhone(String uacc,String newPhone);                                               //修改Phone回调
    MResult getuserinfo(String uacc);                                                           //获取用户信息
    QueryResult HistorySensor(String tid, String sensorid, String startime, String endtime) throws ParseException;//获取传感器历史数据
    String BuyTerminal(String tid,String uacc);                                                 //用户购买设备
    String SelectTerminal();                                                                    //用户查看当前终端库存
    String SelectOwnTerm(String uacc);                                                          //用户查看自己拥有的设备
    String SetSenMax(String tid,String sensor,String min,String max);                           //用户设置传感器阈值
    Terminal SelectSenStatus(String tid);                                                       //用户查看设备当前传感器状态
    List<History> SelectTerHistory(String tid);                                                 //用户查看设备的历史在线状态
    String SwtichWarning(String tid,String flag);                                               //是否开启报警开关
    void ExitLogin(String uacc);                                                                //用户退出登陆
}
