package com.Fate_Project.Entity;

public class History {
    private Integer tid;                // 终端ID         主键（不为null）
    private String  tontime;            //上线时间
    private String  tofftime;           //下线时间
    private String  continuetime;       //在线时长

    public Integer getTid() {
        return tid;
    }

    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public String getTontime() {
        return tontime;
    }

    public void setTontime(String tontime) {
        this.tontime = tontime;
    }

    public String getTofftime() {
        return tofftime;
    }

    public void setTofftime(String tofftime) {
        this.tofftime = tofftime;
    }

    public String getContinuetime() {
        return continuetime;
    }

    public void setContinuetime(String continuetime) {
        this.continuetime = continuetime;
    }
}
