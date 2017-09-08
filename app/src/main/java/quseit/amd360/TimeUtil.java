package quseit.amd360;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2017/9/8.
 */

public class TimeUtil {


    public static final String getDateForMenu(){
        Calendar cal = Calendar.getInstance();
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String cdate = sdf.format(cal.getTime());
        return cdate;
    }
}
