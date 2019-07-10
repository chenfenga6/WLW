package com.Fate_Project.Entity;

public class Email {
    private String email;
    private String flag;

    public Email() {
    }

    public Email(String email, String flag) {
        this.email = email;
        this.flag = flag;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
