package com.Fate_Project.Entity;

public class Air {
    private String time;        //时间
    private String tid;         //终端id
    private String temp;        //温度
    private String hum;         //湿度
    private String amm;         //氨气 ppm

    public Air() {
    }

    public Air(String time, String tid, String temp, String hum, String amm) {
        this.time = time;
        this.tid = tid;
        this.temp = temp;
        this.hum = hum;
        this.amm = amm;
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

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getHum() {
        return hum;
    }

    public void setHum(String hum) {
        this.hum = hum;
    }

    public String getAmm() {
        return amm;
    }

    public void setAmm(String amm) {
        this.amm = amm;
    }
}
