package quseit.amd360;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String sPath = "file:///sdcard/DCIM/Camera/qqq.mp4";
    public static final String sPathImg = "file:///sdcard/DCIM/Camera/ppp.jpg";
    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.et_input);
        editText.setText(sPath);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MD360PlayerActivity.startVideo(MainActivity.this, Uri.parse(sPath));
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MD360PlayerActivity.startBitmap(MainActivity.this, Uri.parse(sPathImg));
            }
        });
//        findViewById(R.id.button).performClick();
    }
}
