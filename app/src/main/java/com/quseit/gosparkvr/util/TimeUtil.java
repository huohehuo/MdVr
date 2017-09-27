package com.quseit.gosparkvr.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/9/8.
 */

public class TimeUtil {


    //用于主页右上角的时间显示
    public static final String getDateForMenu(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String cdate = sdf.format(cal.getTime());
        return cdate;
    }


    //用于把00:00:00 转化成秒数形式
    public static int setTime2int(String time) {
        String [] str = time.split(":");
        try {
            return Integer.valueOf(str[0])*3600+Integer.valueOf(str[1])*60+Integer.valueOf(str[2]);
        }catch (Exception e){
            Log.e("error",e.toString());
            return 0;
        }
    }

    //用于把秒数转化成：00:00:00 形式
    public static String videoTimeString(int num) {
        if (num < 60) {//小于一分钟
            if (num < 10) {
                return "00:00:0" + num;
            } else {
                return "00:00:" + num;
            }
        } else if (num >= 60 && num < 3600) {//一个小时以内
            int fen = num / 60;
            int miao = num % 60;
            if (fen < 10) {
                if (miao < 10) {
                    return "00:0" + fen + ":0" + miao;
                } else {
                    return "00:0" + fen + ":" + miao;
                }
            } else {
                if (miao < 10) {
                    return "00:" + fen + ":0" + miao;
                } else {
                    return "00:" + fen + ":" + miao;
                }
            }
        } else if (num >= 3600 && num < 43200) {//12小时以内
            int hour = num / 3600;
            if (num % 3600 <= 60) {
                int miao = num % 3600;
                if (hour < 10) {
                    if (miao < 10) {
                        return "0" + hour + ":" + "00" + ":0" + miao;
                    } else {
                        return "0" + hour + ":" + "00" + ":" + miao;
                    }
                } else {
                    if (miao < 10) {
                        return hour + ":" + "00" + ":0" + miao;
                    } else {
                        return hour + ":" + "00" + ":" + miao;
                    }
                }
            } else if (num % 3600 > 60 && num % 3600 < 3600) {
                int fen = num % 3600;
                int fens=fen/60;
                int miao = fen % 60;
                if (hour<10){
                    if (fens<10){
                        if (miao<10){
                            return "0"+hour + ":0" + fens + ":0" + miao;
                        }else{
                            return "0"+hour + ":0" + fens + ":" + miao;
                        }
                    }else{
                        if (miao<10){
                            return "0"+hour + ":" + fens + ":0" + miao;
                        }else{
                            return "0"+hour + ":" + fens + ":" + miao;
                        }
                    }
                }else{
                    if (fens<10){
                        if (miao<10){
                            return hour + ":0" + fens + ":0" + miao;
                        }else{
                            return hour + ":0" + fens + ":" + miao;
                        }
                    }else{
                        if (miao<10){
                            return hour + ":" + fens + ":0" + miao;
                        }else{
                            return hour + ":" + fens + ":" + miao;
                        }
                    }
                }
            }
        }else{
            return "12:00:00";
        }
        return "00:00:00";
    }
}
