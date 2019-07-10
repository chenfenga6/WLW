package com.Fate_Project.Entity;

import jdk.nashorn.internal.ir.IdentNode;

public class Terminal {
    private  Integer tid;                   //终端ID          主键（不为null）
    private  Integer tstate;                //终端状态
    private  Integer wstate;                //风状态
    private  Integer astate;                //气状态
    private  Integer dstate;                //氧状态
    private  Integer phstate;               //ph状态
    private  String  wmax;                  //风阈值
    private  String  tmax;                  //温度阈值
    private  String  hmax;                  //湿度阈值
    private  String  amax;                  //氨气阈值
    private  String  watempmax;             //水温阈值
    private  String  dmax;                  //溶解氧阈值
    private  String phtempmax;              //ph温度阈值
    private  String phmax;                  //ph阈值
    private  String phmvmax;                //phmv阈值
    private  String tuser;                  //设备的用户
    private  String warn;                 //是否开启报警标志


    public Terminal() {
    }

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public Integer getTstate() {
        return tstate;
    }

    public void setTstate(Integer tstate) {
        this.tstate = tstate;
    }

    public Integer getWstate() {
        return wstate;
    }

    public void setWstate(Integer wstate) {
        this.wstate = wstate;
    }

    public Integer getAstate() {
        return astate;
    }

    public void setAstate(Integer astate) {
        this.astate = astate;
    }

    public Integer getDstate() {
        return dstate;
    }

    public void setDstate(Integer dstate) {
        this.dstate = dstate;
    }

    public Integer getPhstate() {
        return phstate;
    }

    public void setPhstate(Integer phstate) {
        this.phstate = phstate;
    }

    public String getWmax() {
        return wmax;
    }

    public void setWmax(String wmax) {
        this.wmax = wmax;
    }

    public String getTmax() {
        return tmax;
    }

    public void setTmax(String tmax) {
        this.tmax = tmax;
    }

    public String getHmax() {
        return hmax;
    }

    public void setHmax(String hmax) {
        this.hmax = hmax;
    }

    public String getAmax() {
        return amax;
    }

    public void setAmax(String amax) {
        this.amax = amax;
    }

    public String getWatempmax() {
        return watempmax;
    }

    public void setWatempmax(String watempmax) {
        this.watempmax = watempmax;
    }

    public String getDmax() {
        return dmax;
    }

    public void setDmax(String dmax) {
        this.dmax = dmax;
    }

    public String getPhtempmax() {
        return phtempmax;
    }

    public void setPhtempmax(String phtempmax) {
        this.phtempmax = phtempmax;
    }

    public String getPhmax() {
        return phmax;
    }

    public void setPhmax(String phmax) {
        this.phmax = phmax;
    }

    public String getPhmvmax() {
        return phmvmax;
    }

    public void setPhmvmax(String phmvmax) {
        this.phmvmax = phmvmax;
    }

    public String getTuser() {
        return tuser;
    }

    public void setTuser(String tuser) {
        this.tuser = tuser;
    }

    public String getWarn() {
        return warn;
    }

    public void setWarn(String warn) {
        this.warn = warn;
    }
}
