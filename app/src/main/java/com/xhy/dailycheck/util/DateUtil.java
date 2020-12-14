package com.xhy.dailycheck.util;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    static String DATE_FORMAT = "yyyy-MM-dd";
    static String TIME_FORMAT = "yyyy-MM-dd HH:mm";
    static SimpleDateFormat sdfDate = new SimpleDateFormat(DATE_FORMAT);
    static SimpleDateFormat sdfTime = new SimpleDateFormat(TIME_FORMAT);

    public static long getNowTimeStamp() {
        return System.currentTimeMillis();
    }

    public static String getNowDateString() {
        return sdfDate.format(new Date());
    }

    public static String getNowTimeString() {
        return sdfTime.format(new Date());
    }

    public static int getPassDays(String start,String end) {
        int result = 0;
        try {
            long span = sdfDate.parse(end).getTime() - sdfDate.parse(start).getTime();
            result = (int)(span/86400000)+1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - past);
        Date today = calendar.getTime();
        String result = sdfDate.format(today);
        return result;
    }

    public static String TimeStamp2Date(long ts) {
        return sdfTime.format(ts);
    }

    public static long DateString2TimeStamp(String time) {
        long ts = 0;
        try {
            return sdfTime.parse(time).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ts;
    }

    //显示日期选择对话框
    public static void showDateDialogPick(Context context, final EditText etTime) {
        final StringBuffer time = new StringBuffer();
        //获取Calendar对象，用于获取当前时间
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        //实例化DatePickerDialog对象
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            //选择完日期后会调用该回调函数
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //因为monthOfYear会比实际月份少一月所以这边要加1
                time.append(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                //选择完日期后弹出选择时间对话框
                etTime.setText(time);
            }
        }, year, month, day);
        //弹出选择日期对话框
        datePickerDialog.show();
    }

    //显示日期和时间选择对话框
    public static void showDialogPick(Context context, final EditText etTime) {
        final StringBuffer time = new StringBuffer();
        //获取Calendar对象，用于获取当前时间
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        //实例化TimePickerDialog对象
        final TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            //选择完时间后会调用该回调函数
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time.append(" " + hourOfDay + ":" + minute);
                //设置TextView显示最终选择的时间
                etTime.setText(time);
            }
        }, hour, minute, true);
        //实例化DatePickerDialog对象
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            //选择完日期后会调用该回调函数
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //因为monthOfYear会比实际月份少一月所以这边要加1
                time.append(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                //选择完日期后弹出选择时间对话框
                timePickerDialog.show();
            }
        }, year, month, day);
        //弹出选择日期对话框
        datePickerDialog.show();
    }
}
