package quseit.amd360;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Administrator on 2017/9/11.
 */

public class NetUtil {
    private static ConnectivityManager connectivityManager = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//    private static TelephonyManager  telephonyManager = (TelephonyManager) App.getContext().getSystemService(Context.TELEPHONY_SERVICE);
    public static boolean isNetworkAvailable() {
        if (connectivityManager == null) {
            return false;
        } else {
            //如果仅仅是用来判断网络连接
            //则可以使用 connectivityManager.getActiveNetworkInfo().isAvailable();
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null) {
                return info.isAvailable();//判断是否有网
            }
        }
        return false;
    }

    public static boolean is3rd() {
        NetworkInfo networkINfo = connectivityManager.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    public static boolean isWifi() {
        NetworkInfo networkINfo = connectivityManager.getActiveNetworkInfo();
        if (networkINfo != null
                && networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static void getStor(){
    }

}
