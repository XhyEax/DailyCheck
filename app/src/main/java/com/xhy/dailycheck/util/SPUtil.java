package com.xhy.dailycheck.util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SPUtil {
    private static String SPNAME = "user";

    public static String getUid(Context ctx){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SPNAME, MODE_PRIVATE);
        return sharedPreferences.getString("uid", "");
    }

    public static void setUid(Context ctx, String uid){
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(SPNAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("uid", uid);
        editor.commit();
    }

}
