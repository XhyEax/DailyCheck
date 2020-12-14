package com.xhy.dailycheck.util;

import android.content.Context;
import android.widget.Toast;


public class ToastUtil {

    public static void ToastMsg(Context ctx, String str) {
        Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
    }

}
