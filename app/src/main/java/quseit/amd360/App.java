package quseit.amd360;

import android.app.Application;
import android.content.Context;

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

}
