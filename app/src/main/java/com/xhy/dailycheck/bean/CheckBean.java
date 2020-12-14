package com.xhy.dailycheck.bean;

public class CheckBean {
    public boolean success;
    public String msg;
    public boolean isChecked;

    @Override
    public String toString() {
        return "CheckBean{" +
                "success=" + success +
                ", msg='" + msg + '\'' +
                ", isChecked='" + isChecked + '\'' +
                '}';
    }

}
