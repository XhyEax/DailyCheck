package com.xhy.dailycheck.bean;

public class RegBean {
    public boolean success;
    public String msg;

    @Override
    public String toString() {
        return "RegBean{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }

    public String uid;
}
