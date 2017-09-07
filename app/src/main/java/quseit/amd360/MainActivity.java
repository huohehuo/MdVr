package quseit.amd360;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String sPath = "file:///sdcard/DCIM/Camera/qqq.mp4";
    public static final String sPathImg = "file:///sdcard/DCIM/Camera/tvimg.jpg";
    EditText editText;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ExMenuActivity.startBitmap(MainActivity.this, Uri.parse(sPathImg));
            finish();

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

//        if ("2".equals(getResources().getConfiguration().orientation)){
//            handler.sendEmptyMessage(1);
//        }
        handler.sendEmptyMessage(1);
//        findViewById(R.id.button).performClick();

        Log.e("ori",getResources().getConfiguration().orientation+"方向");
//        getActivity().getResources().getConfiguration().orientation
    }
}
