package com.quseit.gosparkvr.util;

import com.quseit.gosparkvr.App;
import com.quseit.gosparkvr.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/9/15.
 */

public class JsonUtil {

    //提取json数据
    public static List<HashMap<String,String>> getMenuData(int index) {
//        int index = 0;
        String str = FileUtil.readRawTextFile(R.raw.menudata);
//        App.e("getData", str);
        List<HashMap<String,String>> dataList = new ArrayList<>();
        if (!"".equals(str)) {
            JSONObject object = null;
            JSONObject allObject = null;
            try {
                object = new JSONObject(str);
                JSONArray jsonArray = object.getJSONArray("topmenu");
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (i==index){
                            JSONObject json = jsonArray.getJSONObject(i);
                            JSONArray jsonArray1 = json.getJSONArray("items");
                            if (jsonArray1.length()>0){
                                for (int j=0;j<jsonArray1.length();j++){
                                    JSONObject jsonObject = jsonArray1.getJSONObject(j);
                                    HashMap<String, String> hashMap = new HashMap<>();
                                    App.e("getItems", j + "---" + jsonObject.toString());
                                    hashMap.put("id", jsonObject.getString("id"));
                                    hashMap.put("title", jsonObject.getString("title"));
                                    hashMap.put("pic", jsonObject.getString("pic"));
                                    hashMap.put("stream", jsonObject.getString("stream"));
                                    dataList.add(hashMap);
                                }
                            }else{
                                return null;
                            }
                        }
                    }
                    return dataList;
                }else{
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            App.e("提取数据失败");
            //若为空，则再执行下载配置操作
//            HandlerService.startUpdataAdData(App.getContext());
        }
        return null;
    }


    //提取json数据
    public static List<String> getTopMenuData() {
//        int index = 0;
        String str = FileUtil.readRawTextFile(R.raw.menudata);
//        App.e("getData", str);
        List<String> dataList = new ArrayList<>();
        if (!"".equals(str)) {
            JSONObject object = null;
            JSONObject allObject = null;
            try {
                object = new JSONObject(str);
                JSONArray jsonArray = object.getJSONArray("topmenu");
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            dataList.add(json.getString("title"));
                    }
                    App.e("获得菜单："+dataList.toString());
                    return dataList;
                }else{
                    return null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            App.e("提取数据失败");
            //若为空，则再执行下载配置操作
//            HandlerService.startUpdataAdData(App.getContext());
        }
        return null;
    }

}
