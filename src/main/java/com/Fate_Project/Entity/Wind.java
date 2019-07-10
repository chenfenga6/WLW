package com.Fate_Project.Entity;

public class Wind {
    private String time;        //时间
    private String tid;         //终端id
    private String wind;        //风速m/s

    public Wind() {
    }

    public Wind(String time, String tid, String wind) {
        this.time = time;
        this.tid = tid;
        this.wind = wind;
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

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }
}
