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
    EditText editText;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            ExMenuActivity.startBitmap(MainActivity.this, Uri.parse(Config.IMG_MAIN));
            ExMenuActivity.startBitmap(MainActivity.this, Uri.parse("http://attachments.gfan.com/forum/attachments2/201408/28/225557gmw08txms60gqag6.jpg"));
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
