package com.Fate_Project.Servicelmpl;

import com.Fate_Project.Dao.EmailDao;
import com.Fate_Project.Dao.HistoryDao;
import com.Fate_Project.Dao.TerminalDao;
import com.Fate_Project.Dao.UserDao;
import com.Fate_Project.Entity.*;
import com.Fate_Project.Service.LoginService;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static com.Fate_Project.FateProjectApplication.*;


@Service
public class LoginServicelmpl implements LoginService {
    /*日志获取实例*/
    private static final Logger logger = LoggerFactory.getLogger(LoginServicelmpl.class);
    @Resource
    private UserDao userDao;
    @Resource
    private TerminalDao tempinalDao;
    @Resource
    private EmailDao emailDao;
    @Resource
    private HistoryDao historyDao;
    @Autowired
    JavaMailSender jms;

    /**
     * 用户登陆接口实现类
     */
    public MResult Login(String uacc,String upwd) throws InterruptedException {
        logger.info("===========================(用户登陆)==================================");
        if ( (uacc == null || uacc.equals("")) || (upwd == null || upwd.equals("")) ){
            logger.info("用户：["+uacc+"] 登陆时：[账号或密码不可为空！]");
            return new MResult(-1,"账号或密码不可为空！");
        }
        User userinfor = new User();        //用来存放用户信息
        /*判断uacc是邮箱还是电话*/
        if(PD_Acc(uacc)){
            userinfor = userDao.Select_By_Uemail(uacc);
            logger.info("用户：["+uacc+"] 登陆时：[使用的是邮箱！]");
        }
        else if (PD_Phone(uacc)){
            userinfor = userDao.Select_By_Uphone(uacc);
            logger.info("用户：["+uacc+"] 登陆时：[使用的是手机！]");
        }

        if(userinfor == null){
            logger.info("用户：["+uacc+"] 登陆时：[该用户不存在！请先注册！]");
            return new MResult(-1,"该用户不存在！请先注册！");
        }

        if (!(upwd.equals( userinfor.getUpwd() )))
        {
            logger.info("用户：["+uacc+"] 登陆时：[账号或密码不正确！]");
            return new MResult(-1,"账号或密码不正确！");
        }
        /*获取这个用户拥有的设备*/
        int num = 0;
        for(int i=0;i<userinfor.getUtnum();i++){
            if ( (userinfor.getUtid().split("/")[i] ).equals("0")){
                num +=1;
            }
            if ( (userinfor.getUtid().split("/")[i] ).equals("1")){
                num +=2;
            }
        }
        /*给全局变量的 设备阈值 赋值*/
        if(num==1 || num==3){
            logger.info("用户：["+uacc+"]  [登陆设备A！]");
        }else if(num == 2) {
            logger.info("用户：["+uacc+"]  [登陆设备B！]");
        }
        /*一个用户只可登陆一次*/
//        userDao.Updata_ustate_byUid(userinfor.getUid(),1);
//        logger.warn("向数据库[user_data]中,修改用户["+uacc+"]的状态为[1]");
        logger.info("用户：["+uacc+"] 登陆时：[登陆成功！]");
        return new MResult(num,"success");  //num：  0为无设备 1为A设备 2为B设备 3为A和B设备
    }

    /**
     * 用户注册接口实现类
     */
    public MResult Registration(String uname, String upwd,String uemail,String uphone,String flag){
        logger.info("===========================(用户注册)==================================");
        if( flag==null || flag.equals("") ){
            logger.info("用户：["+uname+"] 注册时：[输入验证码为空！]");
            return new MResult(-1,"验证码不可为空");
        }
        User userinfor = new User();
        userinfor = userDao.Select_By_Uemail(uemail);
        if(userinfor != null) {
            logger.info("用户：["+uname+"] 注册时：[使用邮箱已被注册！]");
            return new MResult(-1,"该邮箱已被注册！");
        }
        userinfor = userDao.Select_By_Uphone(uphone);
        if(userinfor != null){
            logger.info("用户：["+uname+"] 注册时：[使用手机已被注册！]");
            return new MResult(-1,"改手机号已被绑定！");
        }

        /*验证邮箱、电话是否符合标准*/
        if( !(PD_Acc(uemail))) {
            logger.info("用户：["+uname+"] 注册时：[邮箱格式不正确!]");
            return new MResult(-1,"邮箱格式不正确!");
        }

        if(! PD_Phone(uphone)){
            logger.info("用户：["+uname+"] 注册时：[手机格式不正确!]");
            return new MResult(-1,"请输入正确的手机号！");
        }
        /*判断验证码是否正确*/
        if( ! PD_Flag(uemail,flag) ){
            logger.info("用户：["+uname+"] 注册时：[验证码输入错误!]");
            return new MResult(-1,"验证码错误！");
        }

        /*存入数据库*/
        User user = new User();
        user.setoutuid(uname,upwd,null,0,0,null,null,null,uemail,uphone,null);
        logger.warn("用户：["+uname+"] 注册成功！[用户个人信息存入数据库user_data中]");
        userDao.Insert_User(user);
        Email emailinfo = new Email(uemail,null);
        emailDao.ReplaceEmail(emailinfo);
        return new MResult(1,"success");
    }

    /**
     * 发送邮件实现类     判断uemail是否合理——生成随机数和uemail存入数据库——发送邮件随机数
     */
    public String SendEmail(String uemail){
        logger.info("===========================(发送邮件)==================================");
        if( !(PD_Acc(uemail))) {
            logger.info("向用户：["+uemail+"] 发送邮箱时：[邮箱格式错误！]");
            return "请输入正确的邮箱！";
        }
        /*生成一个6位随机数 flag*/
        Random random = new Random();
        String flag = "";
        for(int i=0;i<6;i++){
            flag+=random.nextInt(10);
        }

        Email email= new Email(uemail,flag);
        emailDao.ReplaceEmail(email);

        /*发送邮件*/
        SimpleMailMessage mainMessage = new SimpleMailMessage();
        mainMessage.setFrom("物联网<chen1327261488@163.com>");                            //发送者邮箱的昵称
        mainMessage.setTo(uemail);                                                        //接收者邮箱
        mainMessage.setSubject("您的验证码！");                                           //邮件主题
        mainMessage.setText("尊敬的用户，欢迎使用我们的产品，验证码是["+flag+"]");        //邮件内容
        jms.send(mainMessage);                                  //发送
        logger.info("向用户：["+uemail+"] 发送邮箱：[发送成功！验证码为：("+flag+")]");
        return "success";
    }

    /**
     * 用户修改密码实现类
     * @param uacc 用户账号
     * @param newpwd 新密码
     * @return  "success"
     */
    public String chgpswd(String uacc,String newpwd){
        logger.info("===========================(用户修改密码)==================================");
        if(PD_Acc(uacc)){
            logger.info("用户：["+uacc+"] 使用邮箱登陆：[修改密码成功！]");
            userDao.Update_upwd_byEmail(uacc,newpwd);
            logger.warn("向数据库[user_data]中：修改用户["+uacc+"]"+"的密码");
        }else {
            logger.info("用户：["+uacc+"] 使用手机登陆：[修改密码成功！]");
            userDao.Update_upwd_byPhone(uacc,newpwd);
            logger.warn("向数据库[user_data]中：修改用户["+uacc+"]"+"的密码");
        }
        return "success";
    }

    /**
     * 忘记密码通过邮箱实现类
     * @param uacc  用户账号
     * @param flag  收到的验证码
     * @return      "验证码不可为空！""请输入正确的邮箱！""success""false"
     */
    public String chgbyemail(String uacc,String flag){
        logger.info("===========================(忘记密码通过邮箱)==================================");
        if(flag==null || flag.equals("")){
            logger.info("用户：["+uacc+"] 通过 邮件修改邮箱 时：[输入空验证码]");
            return "验证码不可为空！";
        }
        /*判断uacc是否是邮箱*/
        if(PD_Acc(uacc)){
            User userinfor = new User();
            userinfor = userDao.Select_By_Uemail(uacc);
            if(userinfor == null){
                logger.info("用户：["+uacc+"] 通过 邮件修改邮箱 时：[用户未注册、不存在！]");
                return "该用户不存在";
            }
        }
        else {
            logger.info("用户：["+uacc+"] 通过 邮件修改邮箱 时：[输入邮箱格式错误！]");
            return "请输入正确的邮箱！";
        }
        /*判断验证码是否正确*/
        if(PD_Flag(uacc,flag)){
            logger.info("用户：["+uacc+"] 通过 邮件修改邮箱 时：[修改成功]");
            return "success";
        }
        else {
            logger.info("用户：["+uacc+"] 通过 邮件修改邮箱 时：[验证码错误！]");
            return "验证码错误！";
        }
    }

    /**
     * 重置密码实现类（重复）
     */
    public String chgwithEcode(String newupwd,String uacc){

        if(PD_Acc(uacc)){
            userDao.Update_upwd_byEmail(uacc,newupwd);
        }else {
            userDao.Update_upwd_byPhone(uacc,newupwd);
        }
        return "success";
    }

    /**
     * 修改邮箱实现类
     */
    public String chgEmail(String uacc,String newEmail){
        logger.info("===========================(修改邮箱)==================================");
        User userinfo = new User();
        if(PD_Acc(uacc)){
            userinfo = userDao.Select_By_Uemail(newEmail);
            if (userinfo!=null){
                logger.info("用户：["+uacc+"]  修改邮箱 时：[新邮箱已被绑定]");
                return "该邮箱已被注册！";
            }
            userDao.Updata_uemail_byEmail(uacc,newEmail);
            logger.warn("向数据库[user_data]中：修改用户["+uacc+"]"+"的邮箱");
        }
        else {
            userinfo = userDao.Select_By_Uphone(newEmail);
            if (userinfo!=null){
                logger.info("用户：["+uacc+"]  修改邮箱 时：[新邮箱已被绑定]");
                return "该邮箱已被注册！";
            }
            userDao.Updata_uemail_byPhone(uacc,newEmail);
            logger.warn("向数据库[user_data]中：修改用户["+uacc+"]"+"的邮箱");
        }
        logger.info("用户：["+uacc+"]  修改邮箱 时：[修改成功！]");
        return "success";
    }

    /**
     * 修改手机实现类
     */
    public String chgPhone(String uacc,String newPhone){
        logger.info("===========================(修改手机)==================================");
        User userinfo = new User();
        if(PD_Acc(uacc)){
            userinfo = userDao.Select_By_Uemail(newPhone);
            if (userinfo!=null){
                logger.info("用户：["+uacc+"]  修改手机号 时：[新手机号已被绑定]");
                return "该手机号已被注册！";
            }
            userDao.Updata_uphone_byEmail(uacc,newPhone);
            logger.warn("向数据库[user_data]中：修改用户["+uacc+"]"+"的手机号");
        }
        else {
            userinfo = userDao.Select_By_Uphone(newPhone);
            if (userinfo!=null){
                logger.info("用户：["+uacc+"]  修改手机号 时：[新手机号已被绑定]");
                return "该手机号已被注册！";
            }
            userDao.Updata_uphone_byPhone(uacc,newPhone);
            logger.warn("向数据库[user_data]中：修改用户["+uacc+"]"+"的手机号");
        }
        logger.info("用户：["+uacc+"]  修改手机号 时：[修改成功！]");
        return "success";
    }

    /**
     * 获取用户信息实现类
     */
    public MResult getuserinfo(String uacc){
        logger.info("===========================(获取用户信息)==================================");
        User userinfo = new User();
        if (PD_Acc(uacc)){
            logger.info("用户：["+uacc+"]  获取用户信息时：[通过邮箱]");
            userinfo = userDao.Select_By_Uemail(uacc);
        }
        else {
            logger.info("用户：["+uacc+"]  获取用户信息时：[通过手机号]");
            userinfo = userDao.Select_By_Uphone(uacc);
        }
        if (userinfo == null){
            logger.info("用户：["+uacc+"]  获取用户信息时：[该用户不存在]");
            return new MResult(-1,"用户不存在");
        }
        logger.info("用户：["+uacc+"]  获取用户信息时：[获取成功]");
        return new MResult(1,"success",userinfo);
    }

    /**用户查看历史数据实现类
     */
    public QueryResult HistorySensor(String tid, String sensorid, String startime, String endtime) throws ParseException {
        /*将本地时间转换成UTC时间*/
        startime = timeTochange(startime,false);
        endtime = timeTochange(endtime,false);
        logger.info("===========================(用户查看历史数据)==================================");
        QueryResult results = new QueryResult();
        String sql = null;
        switch (sensorid){
            case "1":{
                logger.info("获取设备：["+tid+"] 的[风速]传感器历史数据，从["+startime+"]至["+endtime+"]");
                sql="select * from wind_data where tid='"+tid+"' and time>='"+startime+"' and time<='"+endtime+"'";
                results = MyInflxDB.query(sql);
            };break;
            case "2":{
                logger.info("获取设备：["+tid+"] 的[空气三合一]传感器历史数据，从["+startime+"]至["+endtime+"]");
                sql="select * from air_data where tid='"+tid+"' and time>='"+startime+"' and time<='"+endtime+"'";
                results = MyInflxDB.query(sql);
            };break;
            case "3":{
                logger.info("获取设备：["+tid+"] 的[溶解氧]传感器历史数据，从["+startime+"]至["+endtime+"]");
                sql="select * from diss_data where tid='"+tid+"' and time>='"+startime+"' and time<='"+endtime+"'";
                results = MyInflxDB.query(sql);
            };break;
            case "4":{
                logger.info("获取设备：["+tid+"] 的[PH]传感器历史数据，从["+startime+"]至["+endtime+"]");
                sql="select * from ph_data where tid='"+tid+"' and time>='"+startime+"' and time<='"+endtime+"'";
                results = MyInflxDB.query(sql);
            };break;
        }
        logger.info("获取设备：["+tid+"] 传感器历史数据：[成功]");
        return results;
    }

    /**
     * 用户购买设备的实现类
     */
    public String BuyTerminal(String tid, String uacc) {
        logger.info("===========================(用户购买设备)==================================");
        User userinfo = new User();
        if(PD_Acc(uacc)){
            userinfo = userDao.Select_By_Uemail(uacc);
        }else {
            userinfo = userDao.Select_By_Uphone(uacc);
        }
        logger.warn("向数据库[user_data]中，增加用户：["+uacc+"]  的拥有设备数量为"+"["+(userinfo.getUtnum()+1)+"]");
        userDao.Updata_utnum_byUid(userinfo.getUid(),userinfo.getUtnum()+1);    //增加用户的设备数量

        String newtid;
        if (userinfo.getUtid() == null){
            newtid = tid+"/";
        }
        else{
            newtid = userinfo.getUtid()+tid+"/";
        }
        logger.warn("向数据库[user_data]中，修改用户：["+uacc+"]  的拥有设备id为"+"["+newtid+"]");
        userDao.Updata_utid_byUid(userinfo.getUid(),newtid);                            //增加用户的拥有设备
        logger.warn("向数据库[terminal_data]中，修改设备：["+tid+"]  的拥有用户为"+"["+uacc+"]");
        tempinalDao.Updata_tuser_byTid(Integer.parseInt(tid),uacc);                    //修改设备的拥有者

        logger.info("用户：["+uacc+"] 购买设备 [成功]");
        return "success";
    }

    /**
     * 用户查看当前设备库存
     */
    public String SelectTerminal() {
        logger.info("===========================(用户查看当前设备库存)==================================");
        int num_A=0,num_B=0;
        Terminal terminal = new Terminal();
        terminal=tempinalDao.Select_byTid(0);
        if(terminal.getTuser() == null){
            num_A = 1;
        }
        terminal = tempinalDao.Select_byTid(1);
        if (terminal.getTuser() == null){
            num_B = 1;
        }
        logger.info("当前设备库存：A:"+num_A+"B:"+num_B);
        return "A: "+num_A+" B: "+num_B;
    }

    /**
     * 用户查看自己拥有的设备实现类
     */
    public String SelectOwnTerm(String uacc) {
        logger.info("===========================(用户查看自己拥有的设备)==================================");
        User userinfo = new User();
        if (PD_Acc(uacc)){
            userinfo = userDao.Select_By_Uemail(uacc);
        } else if (PD_Phone(uacc))
        {
            userinfo = userDao.Select_By_Uphone(uacc);
        }
        if (userinfo == null){
            logger.info("用户：["+uacc+"]  获取拥有设备信息时：[用户不存在]");
            return "用户不存在";
        }
        if ( (userinfo.getUtnum()) == 0){
            logger.info("用户：["+uacc+"]  获取拥有设备信息时：[没有任何设备]");
            return "没有任何设备！";
        }
        String Sen=userinfo.getUtid();
        String results="";
        for (int i=0;i<userinfo.getUtnum();i++){
            results +=Sen.split("/")[i]+" ";
        }
        logger.info("用户：["+uacc+"]  获取拥有设备信息时：[成功],拥有设备："+results);
        return results;
    }

    /**
     * 用户修改传感器阈值实现类
     * @return
     */
    public String SetSenMax(String tid, String sensor, String min,String max) {
        logger.info("===========================(用户修改传感器阈值)==================================");
        if (Float.parseFloat(min) >= Float.parseFloat(max)){
            logger.info("用户修改设备["+tid+"]时 [最小阈值]>=[最大阈值]");
            return "最小阈值不可大于等于最大阈值！";
        }
        String value=min+"-"+max;
        int Tid = Integer.parseInt(tid);
        switch (sensor){
            case "wind":{
                logger.warn("向数据库[terminal_data]中，修改[风速]的阈值为："+value);
                tempinalDao.Updata_wmax_byTid(Tid,value);
            };break;
            case "temp":{
                logger.warn("向数据库[terminal_data]中，修改[温度]的阈值为："+value);
                tempinalDao.Updata_tmax_byTid(Tid,value);
            };break;
            case "hum":{
                logger.warn("向数据库[terminal_data]中，修改[湿度]的阈值为："+value);
                tempinalDao.Updata_hmax_byTid(Tid,value);
            };break;
            case "amm":{
                    logger.warn("向数据库[terminal_data]中，修改[氨气]的阈值为："+value);
                tempinalDao.Updata_amax_byTid(Tid,value);
            };break;
            case "watemp":{
                logger.warn("向数据库[terminal_data]中，修改[水温]的阈值为："+value);
                tempinalDao.Updata_watempmax_byTid(Tid,value);
            };break;
            case "diss":{
                logger.warn("向数据库[terminal_data]中，修改[溶氧度]的阈值为："+value);
                tempinalDao.Updata_dmax_byTid(Tid,value);
            };break;
            case "phtemp":{
                logger.warn("向数据库[terminal_data]中，修改[PH温度]的阈值为："+value);
                tempinalDao.Updata_phtempmax_byTid(Tid,value);
            };break;
            case "ph":{
                logger.warn("向数据库[terminal_data]中，修改[PH]的阈值为："+value);
                tempinalDao.Updata_phmax_byTid(Tid,value);
            };break;
            case "phmv":{
                logger.warn("向数据库[terminal_data]中，修改[PHMV]的阈值为："+value);
                tempinalDao.Updata_phmvmax_byTid(Tid,value);
            };break;
        }
        logger.info("修改设备["+tid+"]阈值成功！");
        return "success";
    }

    /**
     * 查看当前传感器的开关状态
     */
    public Terminal SelectSenStatus(String tid) {
        logger.info("===========================(查看当前传感器的开关状态)==================================");
        if(!(tid.equals("0")||tid.equals("1"))){
            logger.info("查看设备["+tid+"]上的传感器开关状态时 [没有该设备]");
            return null;
        }
        Terminal terminalinfo = new Terminal();
        terminalinfo = tempinalDao.Select_byTid(Integer.parseInt(tid));
        logger.info("查看设备["+tid+"]上的传感器开关状态 [成功]");
        return terminalinfo;
    }


    /**
     *用户查看设备的历史在线状态
     */
    public List<History> SelectTerHistory(String tid) {
        logger.info("===========================(用户查看设备的历史在线状态)==================================");
        List<History> list = new ArrayList<>();
        list = historyDao.Select_By_Tid(Integer.parseInt(tid));
        logger.info("用户查看设备["+tid+"]的历史上下线数据");

        return list;
    }

    /**
     *是否开启报警开关
     */
    public String SwtichWarning(String tid,String flag) {
        logger.info("===========================(是否开启报警开关)==================================");
        if (  !(flag.equals("0") || flag.equals("1")) ){
            logger.info("设置设备["+tid+"]是否开启报警[flag 格式不正确！]");
            return null;
        }
        tempinalDao.Updata_warn_byTid(Integer.parseInt(tid),flag);
        logger.warn("向数据库terminal中，修改当前是否报警字段[warn="+flag+"]");
        return "success";
    }


    /**
     *用户退出登陆实现类
     */
    public void ExitLogin(String uacc) {
        User userinfo = new User();
        if(PD_Acc(uacc)){
            userinfo = userDao.Select_By_Uemail(uacc);
        }else {
            userinfo = userDao.Select_By_Uphone(uacc);
        }
        if (userinfo.getUstate() == 0){
            logger.info("用户["+uacc+"] 退出登陆");
        }else {
            userDao.Updata_ustate_byUid(userinfo.getUid(),0);
            logger.warn("向数据库[user_data]中,修改用户["+uacc+"]的状态为[0]");
            logger.info("用户["+uacc+"] 成功[退出登陆]");
        }
    }


    /*************************************************************************************************/
    /**
     * 判断 acc 是邮箱  还是  电话
     * @param acc   当前登陆账号
     * @return      boolean  true是邮箱  false是电话
     */
    public boolean PD_Acc(String acc){
        /*判断账号是不是 邮箱*/
        String RULE_EMAIL  ="^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";                 // 匹配邮箱的正则表达式
        Pattern p = Pattern.compile(RULE_EMAIL);  //编译正则表达式
        Matcher m = p.matcher(acc);
        return m.matches();
    }

    /**
     * 判断 acc 是邮箱  还是  电话
     * @param acc   当前登陆账号
     * @return      boolean  true是邮箱  false是电话
     */
    public boolean PD_Phone(String acc){
        /*判断账号是不是 手机号*/
        String RULE_Phone = "^(13[0-9]|14[5|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";     // 匹配手机号的正则表达式
        Pattern p = Pattern.compile(RULE_Phone);  //编译正则表达式
        Matcher m = p.matcher(acc);
        return m.matches();
    }

    /**
     * 判断 验证码是否正确
     * @param uemail    邮箱
     * @param flag      用户输入的验证码
     * @return          boolean
     */
    public boolean PD_Flag(String uemail,String flag){
        boolean result;
        Email email = new Email();
        email = emailDao.Select_By_Email(uemail);
        result = flag.equals(email.getFlag());
        /*如果验证码正确，清空当前数据库的验证码*/
        if(result){
            email.setFlag(null);
            emailDao.ReplaceEmail(email);
        }
        return result;
    }

    /**
     *将time加上8hours,把UTC时间转换成本地时间
     * time 是待转换的时间
     * flag 为true  加8小时，将utc时间转换成本地时间
     *      为false 减8小时，将本地时间转换成utc时间
     */
    public String timeTochange(String time,boolean flag) throws ParseException {
        time = time.replace('T', ' ');
        time = time.replace('Z','\0');
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(time);
        long rightTime;
        if(flag){
             rightTime = (long)(date.getTime() + 8*60*60*1000);
        }
        else {
            rightTime = (long)(date.getTime() - 8*60*60*1000);
        }
        String newtime = simpleDateFormat.format(rightTime);
        return newtime;
    }


}
