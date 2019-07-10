package com.Fate_Project.Entity;

public class Ph {
    private String time;        //时间
    private String tid;         //终端id
    private String phtemp;      //ph温度
    private String ph;          //ph酸碱度
    private String phmv;        //phmv

    public Ph() {
    }

    public Ph(String time, String tid, String phtemp, String ph, String phmv) {
        this.time = time;
        this.tid = tid;
        this.phtemp = phtemp;
        this.ph = ph;
        this.phmv = phmv;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getPhtemp() {
        return phtemp;
    }

    public void setPhtemp(String phtemp) {
        this.phtemp = phtemp;
    }

    public String getPh() {
        return ph;
    }

    public void setPh(String ph) {
        this.ph = ph;
    }

    public String getPhmv() {
        return phmv;
    }

    public void setPhmv(String phmv) {
        this.phmv = phmv;
    }
}
