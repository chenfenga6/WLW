package com.Fate_Project.Entity;

import com.sun.javafx.beans.IDProperty;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;

import java.util.Date;
public class User {
    private Integer uid;            //用户ID               主键（不为null）从1000000自增
    private String uname;           //用户名               不为null
    private String upwd;            //用户密码             不为null
    private String uimage;          //用户头像
    private Integer ustate;         //用户在线状态         不为null
    private Integer utnum;          //用户拥有终端数量
    private String utid;            //用户拥有设备ID       设备id之间使用“/”分割
    private Date uontime;           //用户上线时间
    private Date uofftime;          //用户下线时间
    private String uemail;          //用户邮箱             主键（不为null）
    private String uphone;          //用户电话
    private String uflag;           //验证码

    public User(){}

    public User(Integer uid, String uname, String upwd, String uimage, Integer ustate, Integer utnum, String utid, Date uontime, Date uofftime, String uemail, String uphone, String uflag) {
        this.uid = uid;
        this.uname = uname;
        this.upwd = upwd;
        this.uimage = uimage;
        this.ustate = ustate;
        this.utnum = utnum;
        this.utid = utid;
        this.uontime = uontime;
        this.uofftime = uofftime;
        this.uemail = uemail;
        this.uphone = uphone;
        this.uflag = uflag;
    }
    public void setoutuid(String uname, String upwd, String uimage, Integer ustate, Integer utnum, String utid, Date uontime, Date uofftime, String uemail, String uphone, String uflag) {
        this.uname = uname;
        this.upwd = upwd;
        this.uimage = uimage;
        this.ustate = ustate;
        this.utnum = utnum;
        this.utid = utid;
        this.uontime = uontime;
        this.uofftime = uofftime;
        this.uemail = uemail;
        this.uphone = uphone;
        this.uflag = uflag;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpwd() {
        return upwd;
    }

    public void setUpwd(String upwd) {
        this.upwd = upwd;
    }

    public String getUimage() {
        return uimage;
    }

    public void setUimage(String uimage) {
        this.uimage = uimage;
    }

    public Integer getUstate() {
        return ustate;
    }

    public void setUstate(Integer ustate) {
        this.ustate = ustate;
    }

    public Integer getUtnum() {
        return utnum;
    }

    public void setUtnum(Integer utnum) {
        this.utnum = utnum;
    }

    public String getUtid() {
        return utid;
    }

    public void setUtid(String utid) {
        this.utid = utid;
    }

    public Date getUontime() {
        return uontime;
    }

    public void setUontime(Date uontime) {
        this.uontime = uontime;
    }

    public Date getUofftime() {
        return uofftime;
    }

    public void setUofftime(Date uofftime) {
        this.uofftime = uofftime;
    }

    public String getUemail() {
        return uemail;
    }

    public void setUemail(String uemail) {
        this.uemail = uemail;
    }

    public String getUphone() {
        return uphone;
    }

    public void setUphone(String uphone) {
        this.uphone = uphone;
    }

    public String getUflag() {
        return uflag;
    }

    public void setUflag(String uflag) {
        this.uflag = uflag;
    }
}
