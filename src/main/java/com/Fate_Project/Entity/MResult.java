package com.Fate_Project.Entity;

public class MResult {

    private int code;
    private String msg;
    private Object data;

    public MResult() {
    }
    public MResult(int code, String message, Object data) {
        this.code = code;
        this.msg = message;
        this.data = data;
    }
    public MResult(int code, String message) {
        this.code = code;
        this.msg = message;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(Object data) {
        this.data = data;
    }

}

