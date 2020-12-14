package com.xhy.dailycheck.bean;

import java.util.ArrayList;

public class ListBean {
    public boolean success;
    public ArrayList<ListItem> data;

    @Override
    public String toString() {
        return "ListBean{" +
                "success=" + success +
                ", data=" + data +
                '}';
    }
}
