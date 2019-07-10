package com.Fate_Project.Entity;

public class Diss {
    private String time;        //时间
    private String tid;         //终端id
    private String watemp;      //水温
    private String diss;        //溶氧值

    public Diss() {
    }

    public Diss(String time, String tid, String watemp, String diss) {
        this.time = time;
        this.tid = tid;
        this.watemp = watemp;
        this.diss = diss;
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

    public String getWatemp() {
        return watemp;
    }

    public void setWatemp(String watemp) {
        this.watemp = watemp;
    }

    public String getDiss() {
        return diss;
    }

    public void setDiss(String diss) {
        this.diss = diss;
    }
}
