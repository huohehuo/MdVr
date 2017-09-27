package com.quseit.gosparkvr.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by aid on 9/18/16.
 */
public class KeyValueStorage {

    private static final String NAME = "VrKeyValueStorage";

    private Context mContext;
    // 注意SharedPreferences并非线程安全的
    private SharedPreferences kv;

    public KeyValueStorage(Context context) {
        mContext = context;
        kv = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public void putString(String key, String value){
        kv.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue){
        return kv.getString(key, defaultValue);
    }

    public void putInt(String key, Integer value){
        kv.edit().putInt(key, value).apply();
    }

    public Integer getInt(String key, Integer defaultValue){
        return kv.getInt(key, defaultValue);
    }



}
