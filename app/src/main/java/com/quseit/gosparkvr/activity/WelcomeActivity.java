package com.quseit.gosparkvr.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.quseit.gosparkvr.R;

public class WelcomeActivity extends AppCompatActivity {
    EditText editText;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            ExMenuActivity.startBitmap(WelcomeActivity.this, Uri.parse(Config.IMG_MAIN));
            ExMenuActivity.startBitmap(WelcomeActivity.this, Uri.parse("http://attachments.gfan.com/forum/attachments2/201408/28/225557gmw08txms60gqag6.jpg"));
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

        Log.e("ori",getResources().getConfiguration().orientation+"方向");

        if (getResources().getConfiguration().orientation==2){
            handler.sendEmptyMessage(1);
        }
        findViewById(R.id.iv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(1);
            }
        });
        findViewById(R.id.tv_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://vr.quseit.cn");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }
}
