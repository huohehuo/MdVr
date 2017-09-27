package com.quseit.gosparkvr;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.quseit.gosparkvr.util.KeyValueStorage;

/**
 * Created by Administrator on 2017/9/11.
 */

public class App extends Application{

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
//        setKeyValue("menudata",Config.JSON);
    }
    public static Context getContext(){
        return mContext;
    }

    public static void setKeyValue(String key,String value){
        if (value!=null){
            new KeyValueStorage(mContext).putString(key,value);
        }
    }
    public static String getKeyValue(String key){
        return new KeyValueStorage(mContext).getString(key,"");
    }
    public static void e(String tag,String str){
        if (str==null){
            Log.e(tag,"---------|\n"+"！！！！ 传值为空 NULL"+"\n---------|");
        }else{
            Log.e(tag,"---------|\n"+str+"\n---------|");
        }
    }
    public static void e(String str){
        if (str==null){
            Log.e("--GoSpark--","---------|\n"+"！！！！ 传值为空 NULL"+"\n---------|");
        }else{
            Log.e("--GoSpark--","---------|\n"+str+"\n---------|");
        }
    }
}
