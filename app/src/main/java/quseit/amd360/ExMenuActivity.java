package quseit.amd360;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.asha.vrlib.MDDirectorCamUpdate;
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
import java.util.Locale;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * using MD360Renderer
 * 用于主页页面的继承类
 * Created by hzqiujiadi on 16/1/22.
 * hzqiujiadi ashqalcn@gmail.com
 */
public abstract class ExMenuActivity extends Activity {

    private static final String TAG = "MD360PlayerActivity";
    private boolean isChangeHover=true;
    ImageView ivLeft;
    ImageView ivRight;
    public static void startBitmap(Context context, Uri uri) {
        start(context, uri, BitmapPlayerActivityEx.class);
    }

    private static void start(Context context, Uri uri, Class<? extends Activity> clz) {
        Intent i = new Intent(context, clz);
        i.setData(uri);
        context.startActivity(i);
    }

    private MDVRLibrary mVRLibrary;

    // load resource from android drawable and remote url.
    private MDVRLibrary.IImageLoadProvider mImageLoadProvider = new ImageLoadProvider();

    // load resource from android drawable only.
    private MDVRLibrary.IImageLoadProvider mAndroidProvider = new AndroidProvider(this);

    private List<MDAbsPlugin> plugins = new LinkedList<>();

    private MDPosition logoPosition = MDMutablePosition.newInstance().setY(-8.0f).setYaw(-90.0f);

    private MDPosition topLogoPosition = MDMutablePosition.newInstance().setZ(-8.0f).setX(-3.2f).setY(2.5f);

    private MDPosition topTimePosition = MDMutablePosition.newInstance().setZ(-8.0f).setX(3.2f).setY(2.5f);

    private MDPosition top1Position = MDMutablePosition.newInstance().setZ(-8.0f).setX(-1.2f).setY(2.0f);
    private MDPosition top2Position = MDMutablePosition.newInstance().setZ(-8.0f).setY(2.0f);
    private MDPosition top3Position = MDMutablePosition.newInstance().setZ(-8.0f).setX(1.2f).setY(2.0f);



    private MDPosition onePosition = MDPosition.newInstance().setZ(-8.0f).setX(-1.6f);
    private MDPosition twoPosition = MDPosition.newInstance().setZ(-8.0f).setX(1.6f);
    private MDPosition threePosition = MDPosition.newInstance().setZ(-8.0f).setX(1.6f).setY(-3.2f);
    private MDPosition fourPosition = MDPosition.newInstance().setZ(-8.0f).setX(-1.6f).setY(-3.2f);

    private MDPosition[] positions = new MDPosition[]{
            MDPosition.newInstance().setZ(-8.0f).setYaw(-45.0f),
            MDPosition.newInstance().setZ(-18.0f).setYaw(15.0f).setAngleX(15),
            MDPosition.newInstance().setZ(-10.0f).setYaw(-10.0f).setAngleX(-15),
            MDPosition.newInstance().setZ(-10.0f).setYaw(30.0f).setAngleX(30),
            MDPosition.newInstance().setZ(-10.0f).setYaw(-30.0f).setAngleX(-30),
            MDPosition.newInstance().setZ(-5.0f).setYaw(30.0f).setAngleX(60),
            MDPosition.newInstance().setZ(-3.0f).setYaw(15.0f).setAngleX(-45),
            MDPosition.newInstance().setZ(-3.0f).setYaw(15.0f).setAngleX(-45).setAngleY(45),
            MDPosition.newInstance().setZ(-3.0f).setYaw(0.0f).setAngleX(90),
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    initMenu();
                    break;

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // no title
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // set content view
        setContentView(R.layout.activity_md_using_surface_view_menu);

        // init VR Library
        mVRLibrary = createVRLibrary();

        final Activity activity = this;
        ivLeft = (ImageView) findViewById(R.id.iv_hover_left);
        ivRight = (ImageView) findViewById(R.id.iv_hover_right);
        final List<View> hotspotPoints = new LinkedList<>();
        hotspotPoints.add(findViewById(R.id.hotspot_point1));
        hotspotPoints.add(findViewById(R.id.hotspot_point2));

        //重定位
        findViewById(R.id.ll_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVRLibrary().removePlugins();
                initMenu();
//                if (plugins.size() > 0) {
//                    MDAbsPlugin plugin = plugins.remove(plugins.size() - 1);
//                    getVRLibrary().removePlugin(plugin);
//                }
            }
        });

        final TextView hotspotText = (TextView) findViewById(R.id.hotspot_text);

        getVRLibrary().setEyePickChangedListener(new MDVRLibrary.IEyePickListener() {
            @Override
            public void onHotspotHit(IMDHotspot hotspot, long hitTimestamp) {
                if (hotspot != null) {
                    if (System.currentTimeMillis() - hitTimestamp > 1000) {
                        switch (hotspot.getTag()==null?"":hotspot.getTag()) {
                            case "A":
                                Log.e("vv","wa___a");
                                ExVideoActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_A));
                                getVRLibrary().resetEyePick();
                                break;
                            case "B":
                                ExVideoActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_WEB));
                                getVRLibrary().resetEyePick();
                                break;
                            case "C":
                                break;
                            case "D":
                                ExVideoActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_C));
                                getVRLibrary().resetEyePick();
                                break;
                        }
                        getVRLibrary().resetEyePick();

//                        if ("A".equals(hotspot.getTag())) {
//                            Log.e("vr", "选中——view1");
//                            if (isGoVideo){
//                                isGoVideo=false;
//                                ExVideoActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_A));
//                                getVRLibrary().resetEyePick();
//                            }
////                            MD360PlayerActivity.startVideo(MD360PlayerActivity.this,Uri.parse("http://cache.utovr.com/201508270528174780.m3u8"));
//                        } else if ("B".equals(hotspot.getTag())) {
//                            Log.e("vr", "选中——view2");
//                            ExVideoActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_WEB));
//                        } else if ("C".equals(hotspot.getTag())) {
//                        } else if ("D".equals(hotspot.getTag())) {
//                            ExVideoActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_C));
////                            getVRLibrary().removePlugins();
////                            initMenu();
////                            text.setTextColor(getResources().getColor(R.color.colorPrimary));
//                        }
                    } else {

                        switch (hotspot.getTag()==null?"":hotspot.getTag()) {
                            case "A":
                                Log.e("vv","wa___a");
                                break;
                            case "B":
                                break;
                            case "top1":
                                changeT();
                                break;
                        }
                        Log.e("vr", "看中——view " + hotspot.getTag());
                        isChangeHover=true;
                        changeHover();
                    }
                } else {
                    Log.e("vr", "看其他位置nothing2");
                    isChangeHover=false;
                    changeHover();
                    changeTB();
                    initTime();
//                    view.setBackgroundResource(R.drawable.img_page);
//                    poni1.setText("+");
//                    poni2.setText("+");
//                    text.setTextColor(getResources().getColor(R.color.colorAccent));

                }
                String text = hotspot == null ? "nop" : String.format(Locale.CHINESE, "%s  %fs", hotspot.getTitle(), (System.currentTimeMillis() - hitTimestamp) / 1000.0f);
                hotspotText.setText(text);

            }
        });
    }
    //改变中心焦点图标
    private void changeHover(){
        if (isChangeHover){
            ivLeft.setImageResource(R.drawable.ic_hover_selected);
            ivRight.setImageResource(R.drawable.ic_hover_selected);
        }else{
            ivLeft.setImageResource(R.drawable.ic_hover_normal);
            ivRight.setImageResource(R.drawable.ic_hover_normal);
        }
    }

    private boolean isok = false;

//    public void addOne() {
////        View view = new HoverView(this);
////        view.setBackgroundColor(0x55FFCC11);
//        View view = new ImageView(this);
//        view.setBackgroundResource(R.drawable.img_page);
//        MDViewBuilder builder = MDViewBuilder.create()
//                .provider(view, 300/*view width*/, 200/*view height*/)
//                .size(3, 2)
//                .position(onePosition)
//                .title("md view")
//                .tag("A");
//
//        Log.e("getD", onePosition.toString() + "----");
//        MDAbsView mdView = new MDView(builder);
//        mdView.rotateToCamera();
//        plugins.add(mdView);
//        getVRLibrary().addPlugin(mdView);
//    }
//
//
//    MDViewBuilder buildertwo;
//    MDAbsView mdView;
//    View view;
//
//    public void setView(int resid) {
//        view = new ImageView(this);
//        view.setBackgroundResource(resid);
//    }
//
//    public void addTwo(int resid) {
////        setView(R.drawable.img_page);
//        view = new ImageView(this);
//        view.setBackgroundResource(resid);
//            buildertwo = MDViewBuilder.create();
//            buildertwo.provider(view, 300, 200);
//            buildertwo.size(3, 2);
//            buildertwo.position(twoPosition);
//            buildertwo.title("md view");
//            buildertwo.tag("B");
//            mdView = new MDView(buildertwo);
//            mdView.rotateToCamera();
//            plugins.add(mdView);
//            getVRLibrary().addPlugin(mdView);
//    }
//
//    public void addTwo2(int resid) {
////        setView(R.drawable.img_page);
//        view = new ImageView(this);
//        view.setBackgroundResource(resid);
//
//        if (buildertwo == null) {
//            buildertwo = MDViewBuilder.create();
//            buildertwo.provider(view, 300, 200);
//            buildertwo.size(3, 2);
//            buildertwo.position(twoPosition);
//            buildertwo.title("md view");
//            buildertwo.tag("B");
//            isok = true;
//            mdView = new MDView(buildertwo);
//            mdView.rotateToCamera();
//            plugins.add(mdView);
//            getVRLibrary().addPlugin(mdView);
//        } else {
//            if (isok) {
////                buildertwo.provider(view, 500, 200);
//                buildertwo.size(7, 3);
//                Log.e("test", "11111");
//                getVRLibrary().removePlugin(mdView);
//                mdView = new MDView(buildertwo);
//                mdView.rotateToCamera();
//                plugins.add(mdView);
//                getVRLibrary().addPlugin(mdView);
//                isok=false;
//            } else {
////                buildertwo.provider(view, 300, 200);
//                buildertwo.size(3, 2);
//                Log.e("test", "2222");
//                getVRLibrary().removePlugin(mdView);
//                mdView = new MDView(buildertwo);
//                mdView.rotateToCamera();
//                plugins.add(mdView);
//                getVRLibrary().addPlugin(mdView);
//            }
//        }
//    }
//
//    public void addThree() {
////        View view = new HoverView(this);
////        view.setBackgroundColor(0x55FFCC11);
//
//        View view = new ImageView(this);
//        view.setBackgroundResource(R.drawable.img_page);
//        MDViewBuilder builder = MDViewBuilder.create()
//                .provider(view, 300/*view width*/, 200/*view height*/)
//                .size(3, 2)
//                .position(threePosition)
//                .title("md view")
//                .tag("C");
//        MDAbsView mdView = new MDView(builder);
//        mdView.rotateToCamera();
//        plugins.add(mdView);
//        getVRLibrary().addPlugin(mdView);
//    }
//
//
//    public void addFour() {
//    TextView text;
//        text = new TextView(this);
//        text.setBackgroundResource(R.drawable.dome_pic);
////        text.setText("菜单");
//        text.setTextColor(getResources().getColor(R.color.colorAccent));
//        MDViewBuilder builder = MDViewBuilder.create()
//                .provider(text, 300/*view width*/, 200/*view height*/)
//                .size(3, 2)
//                .position(fourPosition)
//                .title("md view")
//                .tag("D");
//        MDAbsView mdView = new MDView(builder);
//        mdView.rotateToCamera();
//        plugins.add(mdView);
//        getVRLibrary().addPlugin(mdView);
//    }

    View topLogo,menu1,menu2,menu3;
    TextView menu4;
    TextView text1,text2,text3;
    private void initMenuView(){

        topLogo = new ImageView(this);
        topLogo.setBackgroundResource(R.drawable.ic_home_logo);

        menu1 = new ImageView(this);
        menu1.setBackgroundResource(R.drawable.img_page);

        menu2 = new ImageView(this);
        menu2.setBackgroundResource(R.drawable.ych);

        menu3 = new ImageView(this);
        menu3.setBackgroundResource(R.drawable.img_page);

        menu4 = new TextView(this);
        menu4.setBackgroundResource(R.drawable.met);
//        text.setText("菜单");
//        menu4.setTextColor(getResources().getColor(R.color.colorAccent));



    }
    public void initMenu(){
        initMenuTop();
        initTime();
        initMenuView();
        //logo
        MDViewBuilder builderLoge = MDViewBuilder.create()
                .provider(topLogo, 60, 60)
                .size(1, 1)
                .position(topLogoPosition)
                .title("md view")
                .tag("Logo");
        MDAbsView mdViewLogo = new MDView(builderLoge);
        //菜单一
        MDViewBuilder builder1 = MDViewBuilder.create()
                .provider(menu1, 300/*view width*/, 200/*view height*/)
                .size(3, 3)
                .position(onePosition)
                .title("md view")
                .tag("A");
        MDAbsView mdView1 = new MDView(builder1);
        //菜单二
        MDViewBuilder builder2 = MDViewBuilder.create();
        builder2.provider(menu2, 300, 200);
        builder2.size(3, 3);
        builder2.position(twoPosition);
        builder2.title("md view");
        builder2.tag("B");
        MDAbsView mdView2 = new MDView(builder2);
        //菜单三
        MDViewBuilder builder3 = MDViewBuilder.create()
                .provider(menu3, 300/*view width*/, 200/*view height*/)
                .size(3, 3)
                .position(threePosition)
                .title("md view")
                .tag("C");
        MDAbsView mdView3 = new MDView(builder3);
        //菜单四
        MDViewBuilder builder4 = MDViewBuilder.create()
                .provider(menu4, 300/*view width*/, 200/*view height*/)
                .size(3, 3)
                .position(fourPosition)
                .title("md view")
                .tag("D");
        MDAbsView mdView4 = new MDView(builder4);

        mdViewLogo.rotateToCamera();
        mdView1.rotateToCamera();
        mdView2.rotateToCamera();
        mdView3.rotateToCamera();
        mdView4.rotateToCamera();

        plugins.add(mdViewLogo);
        plugins.add(mdView1);
        plugins.add(mdView2);
        plugins.add(mdView3);
        plugins.add(mdView4);
        getVRLibrary().addPlugin(mdViewLogo);
        getVRLibrary().addPlugin(mdView1);
        getVRLibrary().addPlugin(mdView2);
        getVRLibrary().addPlugin(mdView3);
        getVRLibrary().addPlugin(mdView4);
    }
    public void initMenu2(){
        initMenuView();
        //logo
        MDViewBuilder builderLoge = MDViewBuilder.create()
                .provider(topLogo, 60, 60)
                .size(1, 1)
                .position(topLogoPosition)
                .title("md view")
                .tag("Logo");
        MDAbsView mdViewLogo = new MDView(builderLoge);
        //菜单一
        MDViewBuilder builder1 = MDViewBuilder.create()
                .provider(menu1, 300/*view width*/, 200/*view height*/)
                .size(3, 2)
                .position(onePosition)
                .title("md view")
                .tag("A");
        MDAbsView mdView1 = new MDView(builder1);
        //菜单二
        MDViewBuilder builder2 = MDViewBuilder.create();
        builder2.provider(menu2, 300, 200);
        builder2.size(3, 2);
        builder2.position(twoPosition);
        builder2.title("md view");
        builder2.tag("B");
        MDAbsView mdView2 = new MDView(builder2);
        //菜单三
        MDViewBuilder builder3 = MDViewBuilder.create()
                .provider(menu3, 300/*view width*/, 200/*view height*/)
                .size(3, 2)
                .position(threePosition)
                .title("md view")
                .tag("C");
        MDAbsView mdView3 = new MDView(builder3);
        //菜单四
        MDViewBuilder builder4 = MDViewBuilder.create()
                .provider(menu4, 300/*view width*/, 200/*view height*/)
                .size(3, 2)
                .position(fourPosition)
                .title("md view")
                .tag("D");
        MDAbsView mdView4 = new MDView(builder4);

//        mdViewLogo.rotateToCamera();
//        mdView1.rotateToCamera();
//        mdView2.rotateToCamera();
//        mdView3.rotateToCamera();
//        mdView4.rotateToCamera();

        plugins.add(mdViewLogo);
        plugins.add(mdView1);
        plugins.add(mdView2);
        plugins.add(mdView3);
        plugins.add(mdView4);
        getVRLibrary().addPlugin(mdViewLogo);
        getVRLibrary().addPlugin(mdView1);
        getVRLibrary().addPlugin(mdView2);
        getVRLibrary().addPlugin(mdView3);
        getVRLibrary().addPlugin(mdView4);
    }

    TextView top1,top2,top3;
    public void initMenuTop(){
        top1 = new TextView(this);
//        top1.setBackgroundResource(R.drawable.met);
        top1.setText("精选");
        top1.setTextSize(10);
        top1.setTextColor(getResources().getColor(R.color.menu_top_nor));
        top2 = new TextView(this);
//        top2.setBackgroundResource(R.drawable.met);
        top2.setText("风景");
        top2.setTextSize(10);
        top2.setTextColor(getResources().getColor(R.color.menu_top_nor));
        top3 = new TextView(this);
//        top3.setBackgroundResource(R.drawable.met);
        top3.setText("科技");
        top3.setTextSize(10);
        top3.setTextColor(getResources().getColor(R.color.menu_top_nor));
        //菜单一
        MDViewBuilder topBuild1 = MDViewBuilder.create()
                .provider(top1, 90, 60)
                .size(1, 1)
                .position(top1Position)
                .title("top1")
                .tag("top1");
        MDAbsView mdViewTop1 = new MDView(topBuild1);
        //菜单二
        MDViewBuilder topBuild2 = MDViewBuilder.create()
                .provider(top2, 90, 60)
                .size(1, 1)
                .position(top2Position)
                .title("top2")
                .tag("top2");
        MDAbsView mdViewTop2 = new MDView(topBuild2);
        //菜单三
        MDViewBuilder topBuild3 = MDViewBuilder.create();
        topBuild3.provider(top3, 90, 60);
        topBuild3.size(1, 1);
        topBuild3.position(top3Position);
        topBuild3.title("top3");
        topBuild3.tag("top3");
        MDAbsView mdViewTop3 = new MDView(topBuild3);

        mdViewTop1.rotateToCamera();
        mdViewTop2.rotateToCamera();
        mdViewTop3.rotateToCamera();

        plugins.add(mdViewTop1);
        plugins.add(mdViewTop2);
        plugins.add(mdViewTop3);
        getVRLibrary().addPlugin(mdViewTop1);
        getVRLibrary().addPlugin(mdViewTop2);
        getVRLibrary().addPlugin(mdViewTop3);
    }
    TextView tvTime;
    private void initTime(){
        tvTime = new TextView(this);
//        top1.setBackgroundResource(R.drawable.met);
        tvTime.setText("12:00");
        tvTime.setTextSize(15);
        tvTime.setTextColor(getResources().getColor(R.color.menu_top_nor));
        //菜单一
        MDViewBuilder topBuild1 = MDViewBuilder.create()
                .provider(tvTime, 80, 30)
                .size(1, 1)
                .position(topTimePosition)
                .title("toptime")
                .tag("toptime");
        MDAbsView mdViewTopTime = new MDView(topBuild1);
        plugins.add(mdViewTopTime);
        getVRLibrary().addPlugin(mdViewTopTime);
    }

    private void changeT(){
        top1 = new TextView(this);
//        top1.setBackgroundResource(R.drawable.met);
        top1.setText("精选");
        top1.setTextSize(10);
        top1.setTextColor(getResources().getColor(R.color.colorAccent));
        //菜单一
        MDViewBuilder topBuild1 = MDViewBuilder.create()
                .provider(top1, 90, 60)
                .size(1, 1)
                .position(top1Position)
                .title("top1c")
                .tag("top1c");
        MDAbsView mdViewTop1 = new MDView(topBuild1);
        plugins.add(mdViewTop1);

//        getVRLibrary().removePlugin(getVRLibrary().findViewByTag("top1"));
        getVRLibrary().addPlugin(mdViewTop1);
    }
    private void changeTB(){
        top1 = new TextView(this);
//        top1.setBackgroundResource(R.drawable.met);
        top1.setText("精选");
        top1.setTextSize(10);
        top1.setTextColor(getResources().getColor(R.color.menu_top_nor));
        //菜单一
        MDViewBuilder topBuild1 = MDViewBuilder.create()
                .provider(top1, 90, 60)
                .size(1, 1)
                .position(top1Position)
                .title("top1")
                .tag("top1");
        MDAbsView mdViewTop1 = new MDView(topBuild1);
        plugins.add(mdViewTop1);
        getVRLibrary().removePlugin(getVRLibrary().findViewByTag("top1c"));
        getVRLibrary().addPlugin(mdViewTop1);
    }






    private ValueAnimator animator;

    private void startCameraAnimation(final MDDirectorCamUpdate cameraUpdate, PropertyValuesHolder... values) {
        if (animator != null) {
            animator.cancel();
        }

        animator = ValueAnimator.ofPropertyValuesHolder(values).setDuration(2000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float near = (float) animation.getAnimatedValue("near");
                float eyeZ = (float) animation.getAnimatedValue("eyeZ");
                float pitch = (float) animation.getAnimatedValue("pitch");
                float yaw = (float) animation.getAnimatedValue("yaw");
                float roll = (float) animation.getAnimatedValue("roll");
                cameraUpdate.setEyeZ(eyeZ).setNearScale(near).setPitch(pitch).setYaw(yaw).setRoll(roll);
            }
        });
        animator.start();
    }

    abstract protected MDVRLibrary createVRLibrary();

    public MDVRLibrary getVRLibrary() {
        return mVRLibrary;
    }


    private boolean isGoVideo=true;
    @Override
    protected void onResume() {
        super.onResume();
        isGoVideo=true;
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

    protected Uri getUri() {
        Intent i = getIntent();
        if (i == null || i.getData() == null) {
            return null;
        }
        return i.getData();
    }

    public void cancelBusy() {
        findViewById(R.id.progress).setVisibility(View.GONE);
    }

    public void busy() {
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
    private class ImageLoadProvider implements MDVRLibrary.IImageLoadProvider {

        private SimpleArrayMap<Uri, Target> targetMap = new SimpleArrayMap<>();

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
            Picasso.with(getApplicationContext()).load(uri).resize(callback.getMaxTextureSize(), callback.getMaxTextureSize()).onlyScaleDown().centerInside().memoryPolicy(NO_CACHE, NO_STORE).into(target);
        }
    }
}