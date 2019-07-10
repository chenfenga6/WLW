package com.Fate_Project.Controller;
import com.Fate_Project.AES.AesUtil;
import com.Fate_Project.Entity.History;
import com.Fate_Project.Entity.MResult;
import com.Fate_Project.Entity.Terminal;
import com.Fate_Project.Service.LoginService;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.print.attribute.standard.PDLOverrideSupported;
import java.util.List;

@RestController
@CrossOrigin
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    protected LoginService loginService;

    /**
     * 用户登陆接口
     * @param uacc     账号
     * @param upwd     密码
     * @return         "账号或密码不可为空！"/"该用户不存在！请先注册！"/"账号或密码不正确！"/"success"
     */
    @RequestMapping("/user/login")
    public MResult Login(@RequestParam String uacc,@RequestParam String upwd) throws Exception {
        if( uacc.length()<24 || upwd.length() < 24 ){
            logger.warn("用户[登陆]接口入参未加密！");
            return null;
        }
        uacc = AesUtil.decrypt(uacc);
        upwd = AesUtil.decrypt(upwd);
        return loginService.Login(uacc,upwd);
    }

    /**
     * 用户注册接口
     * @param uname     昵称
     * @param upwd      密码
     * @param uemail    电子邮箱
     * @param uphone    用户电话
     * @param flag      验证码
     * @return  "验证码不可为空""该邮箱已被注册！""改手机号已被绑定！""邮箱格式不正确!""请输入正确的手机号！""验证码错误！""success"
     */
    @RequestMapping("/user/reg")
    public MResult Registration(@RequestParam String uname,@RequestParam String upwd,@RequestParam String uemail,@RequestParam String uphone,@RequestParam String flag) throws Exception {
        if( uname.length()<24 || upwd.length()<24 || uemail.length()<24 || uphone.length()<24 || flag.length()<24 ){
            logger.warn("用户[注册]接口入参未加密！");
            return null;
        }
        uname = AesUtil.decrypt(uname);
        upwd = AesUtil.decrypt(upwd);
        uemail = AesUtil.decrypt(uemail);
        uphone = AesUtil.decrypt(uphone);
        flag = AesUtil.decrypt(flag);
        return loginService.Registration(uname,upwd,uemail,uphone,flag);
    }

    /**
     * 用法发送验证码
     * @param uemail 用户邮箱
     * @return "请输入正确的邮箱！" / "success"
     */
    @RequestMapping("/SendEmail")
    public String SendEmail(@RequestParam String uemail) throws Exception {
        if( uemail.length()<24 ){
            logger.warn("用户[发送验证码]接口入参未加密！");
            return null;
        }
        uemail = AesUtil.decrypt(uemail);
        return loginService.SendEmail(uemail);
    }

    /**
     * 用户修改密码（没有加对密码的要求）
     * @param uacc          用户账号
     * @param newpwd        新密码
     * @return              "success"
     */
    @RequestMapping("/user/chgpswd")
    public String chgpswd(@RequestParam String uacc,@RequestParam String newpwd) throws Exception {
        if( uacc.length()<24 || newpwd.length()<24 ){
            logger.warn("用户[修改密码]接口入参未加密！");
            return null;
        }
        uacc = AesUtil.decrypt(uacc);
        newpwd = AesUtil.decrypt(newpwd);
        return loginService.chgpswd(uacc,newpwd);
    }

    /**
     * 忘记密码通过邮箱接口（还没有进行具体的操作）
     * @param uacc  用户账号
     * @param flag  验证码
     * @return "验证码不可为空！""请输入正确的邮箱！""success""false""该用户不存在"
     */
    @RequestMapping("/user/chgbyemail")
    public String chgbyemail(@RequestParam String uacc,@RequestParam String flag) throws Exception {
        if( uacc.length()<24 || flag.length()<24 ){
            logger.warn("用户[通过邮箱修改密码]接口入参未加密！");
            return null;
        }
        uacc = AesUtil.decrypt(uacc);
        flag = AesUtil.decrypt(flag);
        return loginService.chgbyemail(uacc,flag);
    }

    /**
     * 重置密码
     * @param uacc      账号
     * @param newupwd   新密码
     * @return          "success"
     */
    @RequestMapping("/user/chgwithEcode")
    public String chgwithEcode(@RequestParam String uacc,@RequestParam String newupwd) throws Exception {
        if( uacc.length()<24 || newupwd.length()<24 ){
            logger.warn("用户[重置密码]接口入参未加密！");
            return null;
        }
        uacc = AesUtil.decrypt(uacc);
        newupwd = AesUtil.decrypt(newupwd);
        return loginService.chgwithEcode(newupwd,uacc);
    }

    /**
     * 修改Email
     * @param uacc      用户账号
     * @param newEmail  新Email
     * @return
     */
    @RequestMapping("/user/chgEmail")
    public String chgEmail(@RequestParam String uacc,@RequestParam String newEmail) throws Exception {
        if( uacc.length()<24 || newEmail.length()<24 ){
            logger.warn("用户[修改邮箱]接口入参未加密！");
            return null;
        }
        uacc = AesUtil.decrypt(uacc);
        newEmail = AesUtil.decrypt(newEmail);
        return loginService.chgEmail(uacc,newEmail);

    }

    /**
     * 修改手机号
     * @param uacc      账号
     * @param newPhone  新手机号
     * @return          "success"
     */
    @RequestMapping("/user/chgPhone")
    public String chgPhone(@RequestParam String uacc,@RequestParam String newPhone) throws Exception {
        if( uacc.length()<24 || newPhone.length()<24 ){
            logger.warn("用户[修改手机号]接口入参未加密！");
            return null;
        }
        uacc = AesUtil.decrypt(uacc);
        newPhone = AesUtil.decrypt(newPhone);
        return loginService.chgPhone(uacc,newPhone);
    }

    /**
     * 获取用户个人信息
     * @param uacc      用户账号
     * @return          MResult(1,"success",userinfo)
     */
    @RequestMapping("/user/getuserinfo")
    public MResult getuserinfo(@RequestParam String uacc) throws Exception {
        if( uacc.length()<24){
            logger.warn("用户[获取个人信息]接口入参未加密！");
            return null;
        }
        uacc = AesUtil.decrypt(uacc);
        return  loginService.getuserinfo(uacc);
    }

    /**
     * 获取某传感器的历史数据
     * @param tid           终端id     0设备A  1设备B
     * @param sensorid      传感器id   1:风  2：空  3：氧  4:ph
     * @param startime      开始时间   yyyy-MM-DDThh:mm:ssZ
     * @param endtime       结束时间   yyyy-MM-DDThh:mm:ssZ
     * @return
     */
    @RequestMapping("/HistorySensor")
    public QueryResult HistorySensor(@RequestParam String tid, @RequestParam String sensorid, @RequestParam String startime, @RequestParam String endtime) throws Exception {
        if( tid.length()<24 || sensorid.length()<24 ||startime.length()<24 ||endtime.length()<24){
            logger.warn("[传感器的历史数据]接口入参未加密！");
            return null;
        }
        tid = AesUtil.decrypt(tid);
        sensorid = AesUtil.decrypt(sensorid);
        startime = AesUtil.decrypt(startime);
        endtime = AesUtil.decrypt(endtime);
        return loginService.HistorySensor(tid,sensorid,startime,endtime);
    }

    /**
     * 用户购买终端
     * @param tid   哪块设备
     * @param uacc  用户账号
     * @return
     */
    @RequestMapping("/BuyTerminal")
    public String BuyTerminal(@RequestParam String tid,@RequestParam String uacc) throws Exception {
        if( uacc.length()<24 || tid.length()<24 ){
            logger.warn("用户[购买终端]接口入参未加密！");
            return null;
        }
        tid = AesUtil.decrypt(tid);
        uacc = AesUtil.decrypt(uacc);
        return  loginService.BuyTerminal(tid,uacc);
    }

    /**
     * 用户查看库存设备数量
     * @return  A: "当前个数" B: "当前个数"
     */
    @RequestMapping("/SelectTerminal")
    public String SelectTerminal(){
        return loginService.SelectTerminal();
    }

    /**
     * 用户查看自己拥有的设备
     * @param uacc      账号
     * @return          如果有一个A设备：0   如果有一个B设备：1  如果有两个设备：0 1
     */
    @RequestMapping("/SelectOwnTerm")
    public String SelectOwnTerm(@RequestParam String uacc) throws Exception {
        if( uacc.length()<24 ){
            logger.warn("用户[查看自己的设备]接口入参未加密！");
            return null;
        }
        uacc = AesUtil.decrypt(uacc);
        return loginService.SelectOwnTerm(uacc);
    }

    /**
     *用户设置传感器数据的阈值
     * @param tid       哪个设备
     * @param sensor    哪个传感器(wind,temp,hum,amm,watemp,diss,phtemp,ph,phmv)
     * @param min       最小阈值
     * @param max       最大阈值
     * @return
     */
    @RequestMapping("/SetSenMax")
    public String SetSenMax(@RequestParam String tid,@RequestParam String sensor,@RequestParam String min,@RequestParam String max) throws Exception {
        if( tid.length()<24 || sensor.length()<24 ||min.length()<24 ||max.length()<24){
            logger.warn("用户[设置传感器阈值]接口入参未加密！");
            return null;
        }
        tid = AesUtil.decrypt(tid);
        sensor = AesUtil.decrypt(sensor);
        min = AesUtil.decrypt(min);
        max = AesUtil.decrypt(max);
        return loginService.SetSenMax(tid,sensor,min,max);
    }

    /**
     * 查看所有传感器当前的状态
     * @param tid   设备id
     * @return  Json字符串
     */
    @RequestMapping("/SelectSenStatus")
    public Terminal SelectSenStatus(@RequestParam String tid) throws Exception {
        if( tid.length()<24 ){
            logger.warn("用户[查看传感器当前状态]接口入参未加密！");
            return null;
        }
        tid = AesUtil.decrypt(tid);
        return loginService.SelectSenStatus(tid);
    }


    /**
     * 用户查看设备的历史在线情况
     * @param tid
     * @return  List<History>
     */
    @RequestMapping("/SelectTerHistory")
    public List<History> SelectTerHistory(@RequestParam String tid) throws Exception {
        if( tid.length()<24 ){
            logger.warn("用户[查看设备历史上下线]接口入参未加密！");
            return null;
        }
        tid = AesUtil.decrypt(tid);
        return loginService.SelectTerHistory(tid);
    }

    /**
     * 是否开启报警开关
     * @param flag      开启："1"  关闭："0"
     * @return          "success"
     */
    @RequestMapping("/SwitchWarning")
    public String SwtichWarning(@RequestParam String tid,@RequestParam String flag) throws Exception {
        if( tid.length()<24 || flag.length()<24){
            logger.warn("用户[开启报警开关]接口入参未加密！");
            return null;
        }
        tid = AesUtil.decrypt(tid);
        flag = AesUtil.decrypt(flag);
        return loginService.SwtichWarning(tid,flag);
    }

    /**
     * 用户退出登陆接口
     * @param uacc  用户账号
     */
    @RequestMapping("/user/exit")
    public void ExitLogin(@RequestParam String uacc) throws Exception {
        uacc = AesUtil.decrypt(uacc);
        loginService.ExitLogin(uacc);
    }

}
