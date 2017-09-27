package com.quseit.gosparkvr.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.quseit.gosparkvr.App;
import com.quseit.gosparkvr.R;
import com.quseit.gosparkvr.manager.MediaPlayerWrapper;
import com.quseit.gosparkvr.util.TimeUtil;
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
    private MDPosition playPosition = MDPosition.newInstance().setZ(-8.02f).setX(-2.2f).setY(-3.5f).setYaw(-5.0f);
    private MDPosition backPosition = MDPosition.newInstance().setZ(-8.0f).setX(2.2f).setY(-3.5f).setYaw(-5.0f);

    private MDPosition videoTimePosition = MDPosition.newInstance().setZ(-8.0f).setX(1.2f).setY(-3.5f).setYaw(-5.0f);
    private MDPosition videoLeftTimePosition = MDPosition.newInstance().setZ(-8.0f).setX(0.0f).setY(-3.5f).setYaw(-5.0f);

    private MDPosition backgroundPosition = MDPosition.newInstance().setZ(-7.98f).setX(0.0f).setY(-3.5f).setYaw(-5.0f);


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
    private String strVideoLength="00:00:00";

    private int videoTime=0;
    private String strVideoLeftTime="00:00:00";

    private int intVideoTotalTime=0;
    private boolean isShow=true;
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

        //屏幕触摸事件，用于视线转移到某个模块时的操作事件
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
                    case "outside":
                        if (isShow){
                            getVRLibrary().removePlugins();
                            isShow=false;
                        }else{
                            initMenu();
                        }
                        break;
                }
                App.e("点击："+clickName);
            }
        });
        //长按屏幕时，重定位所有模块
        findViewById(R.id.ll_view).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getVRLibrary().removePlugins();
                initMenu();
                return true;
            }
        });

        ivLeft = (ImageView) findViewById(R.id.iv_hover_video_left);
        ivRight = (ImageView) findViewById(R.id.iv_hover_video_right);
        //当实现转移到某个模块时
        getVRLibrary().setEyePickChangedListener(new MDVRLibrary.IEyePickListener() {
            @Override
            public void onHotspotHit(IMDHotspot hotspot, long hitTimestamp) {
                if (hotspot != null) {//看着模块时
//                    if (System.currentTimeMillis() - hitTimestamp > 1000)
                    clickName = hotspot.getTag();
//                    switch (hotspot.getTag() == null ? "" : hotspot.getTag()) {
//                        case "ivClose":
//
//                            break;
//                        case "ivPlay":
//
//                            break;
//                    }
//                    Log.e("click", "正在看——view " + hotspot.getTag());
                    isChangeHover=true;
                    changeHover();
                } else {//看着空白处时
                    clickName = "outside";
                    isChangeHover=false;
                    changeHover();
                }

            }
        });


    }
    //播放按钮的控制及状态
    public static final int PLAYING = 0;//正在播放
    public static final int PAUSE = 1;//暂停播放
    public static final int STOP = 2;//停止播放，播放完毕
    public int playStatus = PLAYING;
    //用于设置播放状态
    private void playVideo(MediaPlayerWrapper playerWrapper){
        switch (playStatus){
            case PLAYING://当视频处于播放状态时，转变成暂停状态
                playerWrapper.pause();
                playBtn(PAUSE);
                playStatus = PAUSE;
                break;
            case PAUSE://当视频处于暂停状态时，转变成播放状态
                playerWrapper.start();
                playBtn(PLAYING);
                playStatus=PLAYING;
                break;
            case STOP://当播放完毕时，再次点击播放按钮将会重新播放
                videoTime=0;
                playerWrapper.stop();
                playerWrapper.prepare();
                playBtn(PLAYING);
                playStatus=PLAYING;
                break;
        }
//        if (isAutoPlay){
//            isPlay=false;
//            isAutoPlay=false;
//            return;
//        }
//
//        if (playStatus==PLAYING){
//
////            isPlay=false;
//        }
//
//        else{
//
//
//            isPlay=true;
//        }
    }
    private boolean isChangeHover=true;
    //改变中心焦点图标
    private void changeHover(){
        //当videoBar为显示状态时，才显示中心焦点图标
        if (isShow){
            ivLeft.setVisibility(View.VISIBLE);
            ivRight.setVisibility(View.VISIBLE);
            if (isChangeHover){
                ivLeft.setImageResource(R.drawable.ic_hover_selected);
                ivRight.setImageResource(R.drawable.ic_hover_selected);
            }else{
                ivLeft.setImageResource(R.drawable.ic_hover_normal);
                ivRight.setImageResource(R.drawable.ic_hover_normal);
            }
        }else{
            ivLeft.setVisibility(View.GONE);
            ivRight.setVisibility(View.GONE);
        }

    }

    //初始化页面模块
    private TextView tvTime,tvLeftTime,ivBg;
    private ImageView ivPlay,ivClose;
    //初始化videoBar
    public void initMenu(){
        isShow=true;
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
        if (playStatus==PLAYING){
            ivPlay.setBackgroundResource(R.drawable.ic_pause);
        }else{
            ivPlay.setBackgroundResource(R.drawable.ic_play);
        }
        MDViewBuilder builderPlay = MDViewBuilder.create()
                .provider(ivPlay, 100/*view width*/, 100/*view height*/)
                .size(1, 1)
                .position(playPosition)
                .title("ivPlay")
                .tag("ivPlay")
                ;
        MDAbsView mdView2 = new MDView(builderPlay);

//        //视频总时长
//        tvTime = new TextView(this);
//        tvTime.setText("/"+strVideoLength);
//        tvTime.setTextSize(4);
//        tvTime.setTextColor(getResources().getColor(R.color.menu_top_nor));
//        MDViewBuilder topBuild1 = MDViewBuilder.create()
//                .provider(tvTime, 70, 18)
//                .size(1, 0.35f)
//                .position(videoTimePosition)
//                .title("videotime")
//                .tag("videotime");
//        MDAbsView mdViewVideoTime = new MDView(topBuild1);

        //视频剩余时常
        tvLeftTime = new TextView(this);
        tvLeftTime.setText(strVideoLeftTime);
        tvLeftTime.setTextSize(4);
        tvLeftTime.setTextColor(getResources().getColor(R.color.menu_top_nor));
        MDViewBuilder topBuildLeftTime = MDViewBuilder.create()
                .provider(tvLeftTime, 70, 18)
                .size(1, 0.35f)
                .position(videoLeftTimePosition)
                .title("videolefttime" )
                .tag("videolefttime");
        MDAbsView mdViewVideoLeftTime = new MDView(topBuildLeftTime);



        mdViewbg.rotateToCamera();
        mdView.rotateToCamera();
        mdView2.rotateToCamera();
//        mdViewVideoTime.rotateToCamera();
        mdViewVideoLeftTime.rotateToCamera();

        plugins.add(mdView);
        plugins.add(mdViewbg);
        plugins.add(mdView2);
//        plugins.add(mdViewVideoTime);
        plugins.add(mdViewVideoLeftTime);

        getVRLibrary().addPlugin(mdView);
        getVRLibrary().addPlugin(mdViewbg);
        getVRLibrary().addPlugin(mdView2);
//        getVRLibrary().addPlugin(mdViewVideoTime);
        getVRLibrary().addPlugin(mdViewVideoLeftTime);
    }


    /**
     *  //播放按钮状态更新
     * @param state     变更的播放状态
     */
    public void playBtn(int state){
        if (ivPlay==null){
//            Log.e("view","textView更新");
            ivPlay=new ImageView(this);
            ivPlay.setBackgroundResource(R.drawable.ic_pause);
        }

        if (state==PAUSE){//设置为暂停播放
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
        }else if (state==PLAYING){//设置为播放播放
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
        }else{//停止播放时
            playStatus=STOP;//设置状态为停止
            strVideoLeftTime=strVideoLength;//使剩余时间与总时间同步
            initMenu();//初始化videoBar
        }


    }

    /**
     * //成功播放时执行
     * @param str     视频总时长 00:00:00
     */
    public void setTotalTime(String str){
        this.strVideoLength=str;//设置总时长string
        //根据总时长String转换成int数值
        this.intVideoTotalTime=TimeUtil.setTime2int(str);
        //执行倒计时循环
        handler.post(timeRunnable);
    }

    //设置视频总长度
    public void setVideoSize(boolean isChangePlace,String videoLength){
        this.strVideoLength=videoLength;
        //设置总时长String，并且转换为Int值
        this.intVideoTotalTime=TimeUtil.setTime2int(videoLength);
//        handler.post(timeRunnable);//执行视频时长计时
        if (tvTime==null){
            tvTime = new TextView(this);
            tvTime.setText(videoLength);
            tvTime.setTextSize(4);
            tvTime.setTextColor(getResources().getColor(R.color.menu_top_nor));
        }
        //菜单一
        tvTime.setText("/"+videoLength);
        getVRLibrary().removePlugin(getVRLibrary().findViewByTag("videotime"));
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
        handler.post(timeRunnable);
    }


    /**
     * //设置视频播放时长
     * @param isChangePlace 是否重定位，一般初始化整个页面的时候才需要为true，只更新内容false
     * @param strVideoLeftTime  剩余的时间数 00:00:00
     */
    public void setVideoLeftTime(boolean isChangePlace,String strVideoLeftTime){
        if (tvLeftTime==null){
            tvLeftTime = new TextView(this);
            tvLeftTime.setText(strVideoLeftTime);
            tvLeftTime.setTextSize(4);
            tvLeftTime.setTextColor(getResources().getColor(R.color.menu_top_nor));
        }
        //当状态为true时，为videoBar显示状态，就显示时间（以防runnable中不断刷新页面）
        if (isShow){
            //播放剩余时长
            tvLeftTime.setText(strVideoLeftTime);
            getVRLibrary().removePlugin(getVRLibrary().findViewByTag("videolefttime"));
            MDViewBuilder topBuild1 = MDViewBuilder.create()
                    .provider(tvLeftTime, 70, 18)
                    .size(1, 0.35f)
                    .position(videoLeftTimePosition)
                    .title("videolefttime")
                    .tag("videolefttime");
            MDAbsView mdViewTopTime = new MDView(topBuild1);
            plugins.add(mdViewTopTime);
            if (isChangePlace){//当为true时，为基于当前视角重定位布局，一般初始化的时候才会为true
                mdViewTopTime.rotateToCamera();
            }
            getVRLibrary().addPlugin(mdViewTopTime);
        }else{
        }
    }

    public Handler handler = new Handler();
    //执行视频时间循环
    public Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            //当为播放状态时，执行倒计时任务
            if (playStatus==PLAYING){
                App.e("runnable:"+intVideoTotalTime);
                //根据剩余时间int值格式化获取时间00：00：00
                strVideoLeftTime=TimeUtil.videoTimeString(intVideoTotalTime);
                //刷新时间模块
                setVideoLeftTime(false, strVideoLeftTime);
                if (!"00:00:00".equals(strVideoLeftTime)){
                    intVideoTotalTime--;
                    handler.postDelayed(timeRunnable,1000);
                }else{
                    setVideoLeftTime(false,strVideoLeftTime);
                }
            }else if (playStatus==PAUSE){
                //当为暂停状态时，不执行倒计时
                handler.postDelayed(timeRunnable,1000);
            }else if (playStatus==STOP){
                //当状态为停止时，取消循环
                handler.removeCallbacks(timeRunnable);
            }

        }
    };


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
        handler.removeCallbacks(timeRunnable);
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