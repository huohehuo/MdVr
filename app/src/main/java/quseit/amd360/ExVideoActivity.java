package quseit.amd360;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.model.MDViewBuilder;
import com.asha.vrlib.model.position.MDMutablePosition;
import com.asha.vrlib.plugins.MDAbsPlugin;
import com.asha.vrlib.plugins.hotspot.IMDHotspot;
import com.asha.vrlib.plugins.hotspot.MDAbsView;
import com.asha.vrlib.plugins.hotspot.MDView;
import com.asha.vrlib.texture.MD360BitmapTexture;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * using MD360Renderer
 *用于视频播放页面的继承类
 * Created by hzqiujiadi on 16/1/22.
 * hzqiujiadi ashqalcn@gmail.com
 */
public abstract class ExVideoActivity extends Activity{

    private static final String TAG = "ExVideoActivity";
    ImageView ivLeft;
    ImageView ivRight;
//    public static void startVideo(Context context, Uri uri){
//        start(context, uri, VideoPlayerActivity.class);
//    }
//    private static void start(Context context, Uri uri, Class<? extends Activity> clz){
//        Intent i = new Intent(context,clz);
//        i.setData(uri);
//        context.startActivity(i);
//    }

    private MDVRLibrary mVRLibrary;

    // load resource from android drawable and remote url.
    private MDVRLibrary.IImageLoadProvider mImageLoadProvider = new ImageLoadProvider();
    public MediaPlayerWrapper mMediaPlayerWrapper = new MediaPlayerWrapper();
    // load resource from android drawable only.
    private MDVRLibrary.IImageLoadProvider mAndroidProvider = new AndroidProvider(this);

    private List<MDAbsPlugin> plugins = new LinkedList<>();

    private MDPosition logoPosition = MDMutablePosition.newInstance().setY(-8.0f).setYaw(-90.0f);
    private MDPosition backPosition = MDPosition.newInstance().setZ(-8.0f).setX(-3.8f).setY(-5.0f).setYaw(-45.0f);
    private MDPosition playPosition = MDPosition.newInstance().setZ(-8.0f).setX(-2.0f).setY(-5.0f).setYaw(-45.0f);
    private MDPosition videoTimePosition = MDPosition.newInstance().setZ(-8.0f).setX(-0.8f).setY(-5.0f).setYaw(-45.0f);
    private MDPosition backgroundPosition = MDPosition.newInstance().setZ(-8.0f).setX(0.0f).setY(-5.0f).setYaw(-45.0f);
    //    private MDPosition[] positions = new MDPosition[]{
//            MDPosition.newInstance().setZ(-8.0f).setYaw(-45.0f),
//            MDPosition.newInstance().setZ(-18.0f).setYaw(15.0f).setAngleX(15),
//            MDPosition.newInstance().setZ(-10.0f).setYaw(-10.0f).setAngleX(-15),
//            MDPosition.newInstance().setZ(-10.0f).setYaw(30.0f).setAngleX(30),
//            MDPosition.newInstance().setZ(-10.0f).setYaw(-30.0f).setAngleX(-30),
//            MDPosition.newInstance().setZ(-5.0f).setYaw(30.0f).setAngleX(60),
//            MDPosition.newInstance().setZ(-3.0f).setYaw(15.0f).setAngleX(-45),
//            MDPosition.newInstance().setZ(-3.0f).setYaw(15.0f).setAngleX(-45).setAngleY(45),
//            MDPosition.newInstance().setZ(-3.0f).setYaw(0.0f).setAngleX(90),
//    };

    private String clickName="";
    private String videoLength="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // set content view
        setContentView(R.layout.activity_md_using_surface_view_video);
        // init VR Library
        mVRLibrary = createVRLibrary();


        final Activity activity = this;

        final List<View> hotspotPoints = new LinkedList<>();
        hotspotPoints.add(findViewById(R.id.hotspot_point1));
        hotspotPoints.add(findViewById(R.id.hotspot_point2));


        findViewById(R.id.ll_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (clickName){
                    case "ivClose":
                        finish();
                        break;
                    case "ivPlay":
                        Log.e("play","click play");
                        playVideo(mMediaPlayerWrapper);
                        break;
                }
            }
        });
        findViewById(R.id.ll_view).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getVRLibrary().removePlugins();
//                initMenu(true);
                initMenu();
                return true;
            }
        });

        ivLeft = (ImageView) findViewById(R.id.iv_hover_video_left);
        ivRight = (ImageView) findViewById(R.id.iv_hover_video_right);
        getVRLibrary().setEyePickChangedListener(new MDVRLibrary.IEyePickListener() {
            @Override
            public void onHotspotHit(IMDHotspot hotspot, long hitTimestamp) {
                if (hotspot != null) {
//                    if (System.currentTimeMillis() - hitTimestamp > 1000)
                    clickName = hotspot.getTag();
                    switch (hotspot.getTag() == null ? "" : hotspot.getTag()) {
                        case "ivClose":

                            break;
                        case "ivPlay":

                            break;
                    }
//                    Log.e("click", "正在看——view " + hotspot.getTag());
                    isChangeHover=true;
                    changeHover();
                } else {
                    clickName = "";
//                    Log.e("vr", "nothing2");
                    isChangeHover=false;
                    changeHover();


                }

            }
        });


    }
    //播放按钮的控制及状态
    private boolean isPlay=true;
    //用于设置播放完毕后的状态值
    public boolean isAutoPlay=false;
    private void playVideo(MediaPlayerWrapper playerWrapper){
        //当播放完毕时，再次点击播放按钮将会重新播放
        if (isAutoPlay){
            playerWrapper.stop();
            playerWrapper.prepare();
            playBtn(false);
            isPlay=false;
            isAutoPlay=false;
            return;
        }
        //当视频处于播放状态时，转变成暂停状态
        if (isPlay){
            playerWrapper.pause();
            playBtn(true);
            isPlay=false;
        }else{
            //当视频处于暂停状态时，转变成播放状态
            playerWrapper.start();
            playBtn(false);
            isPlay=true;
        }
    }
    private boolean isChangeHover=true;
    //改变中心焦点图标
    private void changeHover(){
        if (isChangeHover){
            ivLeft.setImageResource(R.drawable.ic_hover_selected);
            ivRight.setImageResource(R.drawable.ic_hover_selected);
        }else{
            ivLeft.setImageResource(R.drawable.ic_hover_normal);
//            ivLeft.setAlpha(0.5f);
            ivRight.setImageResource(R.drawable.ic_hover_normal);
//            ivRight.setAlpha(0.5f);
        }
    }

    //初始化页面模块
    private TextView tvTime,ivBg;
    private ImageView ivPlay,ivClose;
    public void initMenu(){
        //关闭按钮
        ivClose = new ImageView(this);
        ivClose.setBackgroundResource(R.drawable.ic_close_video);
        MDViewBuilder builder = MDViewBuilder.create()
                .provider(ivClose, 100/*view width*/, 100/*view height*/)
                .size(1, 1)
                .position(backPosition)
                .title("ivClose")
                .tag("ivClose")
                ;
        MDAbsView mdView = new MDView(builder);
        //灰色背景
        ivBg = new TextView(this);
        ivBg.setBackgroundColor(getResources().getColor(R.color.video_toolbar_bg));
        MDViewBuilder builderbg = MDViewBuilder.create()
                .provider(ivBg, 100/*view width*/, 150/*view height*/)
                .size(6, 1.2f)
                .position(backgroundPosition)
                .title("ivbg")
                .tag("ivbg");
        MDAbsView mdViewbg = new MDView(builderbg);
        //播放按钮
        ivPlay=new ImageView(this);
        ivPlay.setBackgroundResource(R.drawable.ic_pause);
        MDViewBuilder builderPlay = MDViewBuilder.create()
                .provider(ivPlay, 100/*view width*/, 100/*view height*/)
                .size(1, 1)
                .position(playPosition)
                .title("ivPlay")
                .tag("ivPlay")
                ;
        MDAbsView mdView2 = new MDView(builderPlay);

        //视频进度条显示
        tvTime = new TextView(this);
//        top1.setBackgroundResource(R.drawable.met);
        tvTime.setText(videoLength);
        tvTime.setTextSize(4);
        tvTime.setTextColor(getResources().getColor(R.color.menu_top_nor));
        //菜单一
        MDViewBuilder topBuild1 = MDViewBuilder.create()
                .provider(tvTime, 70, 18)
                .size(1, 0.35f)
                .position(videoTimePosition)
                .title("videotime")
                .tag("videotime");
        MDAbsView mdViewVideoTime = new MDView(topBuild1);


        mdView.rotateToCamera();
        mdViewbg.rotateToCamera();
        mdView2.rotateToCamera();
        mdViewVideoTime.rotateToCamera();

        plugins.add(mdView);
        plugins.add(mdViewbg);
        plugins.add(mdView2);
        plugins.add(mdViewVideoTime);

        getVRLibrary().addPlugin(mdView);
        getVRLibrary().addPlugin(mdViewbg);
        getVRLibrary().addPlugin(mdView2);
        getVRLibrary().addPlugin(mdViewVideoTime);
    }


    private boolean toChange=true;
    private boolean toChangeBack=true;
    public void playBtn(boolean change){
        if (ivPlay==null){
//            Log.e("view","textView更新");
            ivPlay=new ImageView(this);
            ivPlay.setBackgroundResource(R.drawable.ic_pause);
        }
        if (change){
            if (toChange){
                toChange=false;
                toChangeBack=true;
                //暂停播放
                ivPlay.setBackgroundResource(R.drawable.ic_play);
  //            getVRLibrary().removePlugin(getVRLibrary().findViewByTag("ivPlay"));
                MDViewBuilder builder = MDViewBuilder.create()
                        .provider(ivPlay, 100/*view width*/, 100/*view height*/)
                        .size(1, 1)
                        .position(playPosition)
                        .title("ivPlay")
                        .tag("ivPlay")
                        ;
                MDAbsView mdView = new MDView(builder);
                plugins.add(mdView);
                getVRLibrary().addPlugin(mdView);
            }

        }else{
            if (toChangeBack){
                toChangeBack=false;
                toChange=true;
                ivPlay.setBackgroundResource(R.drawable.ic_pause);
//                getVRLibrary().removePlugin(getVRLibrary().findViewByTag("ivPlay"));
                MDViewBuilder builder = MDViewBuilder.create()
                        .provider(ivPlay, 100/*view width*/, 100/*view height*/)
                        .size(1, 1)
                        .position(playPosition)
                        .title("ivPlay")
                        .tag("ivPlay")
                        ;
                MDAbsView mdView = new MDView(builder);
                plugins.add(mdView);
                getVRLibrary().addPlugin(mdView);
            }

        }


    }
    public void setVideoSize(boolean isChangePlace,String videoLength){
        this.videoLength=videoLength;
        if (tvTime==null){
            tvTime = new TextView(this);
//        top1.setBackgroundResource(R.drawable.met);
            tvTime.setText(videoLength);
            tvTime.setTextSize(4);
            tvTime.setTextColor(getResources().getColor(R.color.menu_top_nor));
        }
        //菜单一
        tvTime.setText(videoLength);
//        getVRLibrary().removePlugin(getVRLibrary().findViewByTag("videotime"));
        MDViewBuilder topBuild1 = MDViewBuilder.create()
                .provider(tvTime, 70, 18)
                .size(1, 0.35f)
                .position(videoTimePosition)
                .title("videotime")
                .tag("videotime");
        MDAbsView mdViewTopTime = new MDView(topBuild1);
        plugins.add(mdViewTopTime);
        if (isChangePlace){
            mdViewTopTime.rotateToCamera();
        }
        getVRLibrary().addPlugin(mdViewTopTime);
    }


    abstract protected MDVRLibrary createVRLibrary();

    public MDVRLibrary getVRLibrary() {
        return mVRLibrary;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVRLibrary.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVRLibrary.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVRLibrary.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mVRLibrary.onOrientationChanged(this);
    }

//    protected Uri getUri() {
//        Intent i = getIntent();
//        if (i == null || i.getData() == null){
//            return null;
//        }
//        return i.getData();
//    }

    public void cancelBusy(){
        findViewById(R.id.progress).setVisibility(View.GONE);
    }

    public void busy(){
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
    }

    // android impl
    private class AndroidProvider implements MDVRLibrary.IImageLoadProvider {

        Activity activity;

        public AndroidProvider(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onProvideBitmap(Uri uri, MD360BitmapTexture.Callback callback) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(uri));
                callback.texture(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    // picasso impl
    private class ImageLoadProvider implements MDVRLibrary.IImageLoadProvider{

        private SimpleArrayMap<Uri,Target> targetMap = new SimpleArrayMap<>();

        @Override
        public void onProvideBitmap(final Uri uri, final MD360BitmapTexture.Callback callback) {

            final Target target = new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    // texture
                    callback.texture(bitmap);
                    targetMap.remove(uri);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    targetMap.remove(uri);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            targetMap.put(uri, target);
            Picasso.with(getApplicationContext()).load(uri).resize(callback.getMaxTextureSize(),callback.getMaxTextureSize()).onlyScaleDown().centerInside().memoryPolicy(NO_CACHE, NO_STORE).into(target);
        }
    }
}