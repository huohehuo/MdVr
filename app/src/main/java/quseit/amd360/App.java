package quseit.amd360;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by Administrator on 2017/9/11.
 */

public class App extends Application{

    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;


    }
    public static Context getContext(){
        return mContext;
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
            Log.e("--XSocks--","---------|\n"+"！！！！ 传值为空 NULL"+"\n---------|");
        }else{
            Log.e("--XSocks--","---------|\n"+str+"\n---------|");
        }
    }
}
