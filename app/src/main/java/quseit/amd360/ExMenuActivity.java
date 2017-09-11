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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.asha.vrlib.MDDirectorCamUpdate;
import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.model.MDRay;
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

    private MDPosition logoPosition = MDMutablePosition.newInstance().setY(-12.0f).setYaw(-90.0f);

    private MDPosition topLogoPosition = MDMutablePosition.newInstance().setZ(-12.0f).setX(-3.2f).setY(2.5f);

    private MDPosition topTimePosition = MDMutablePosition.newInstance().setZ(-12.0f).setX(3.2f).setY(2.8f);
    private MDPosition topWifiPosition = MDMutablePosition.newInstance().setZ(-12.0f).setX(2.5f).setY(2.8f);

    private MDPosition top1Position = MDMutablePosition.newInstance().setZ(-12.0f).setX(-1.2f).setY(2.0f);
    private MDPosition top2Position = MDMutablePosition.newInstance().setZ(-12.0f).setY(2.0f);
    private MDPosition top3Position = MDMutablePosition.newInstance().setZ(-12.0f).setX(1.2f).setY(2.0f);



    private MDPosition menu1Position = MDPosition.newInstance().setZ(-12.0f).setX(-1.6f);
    private MDPosition menu2Position = MDPosition.newInstance().setZ(-12.0f).setX(1.6f);
    private MDPosition menu3Position = MDPosition.newInstance().setZ(-12.0f).setX(1.6f).setY(-3.2f);
    private MDPosition menu4Position = MDPosition.newInstance().setZ(-12.0f).setX(-1.6f).setY(-3.2f);

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
                    initMenu(true);
                    break;

            }
        }
    };
    private String clickName="";
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

        findViewById(R.id.ll_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (clickName){
                    case "menu2":
                        VideoPlayerActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_WEB));
                        break;
                }
            }
        });
        findViewById(R.id.ll_view).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getVRLibrary().removePlugins();
                initMenu(true);
                return true;
            }
        });

        final TextView hotspotText = (TextView) findViewById(R.id.hotspot_text);

        getVRLibrary().setEyePickChangedListener(new MDVRLibrary.IEyePickListener() {
            @Override
            public void onHotspotHit(IMDHotspot hotspot, long hitTimestamp) {
                if (hotspot != null) {
//                    if (System.currentTimeMillis() - hitTimestamp > 5000)
//                        Log.e("time", (System.currentTimeMillis() - hitTimestamp) / 1000.0f+"");
//                        switch (hotspot.getTag()==null?"":hotspot.getTag()) {
//                            case "menu1":
////                                ExVideoActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_A));
//                                getVRLibrary().resetEyePick();
//                                break;
//                            case "menu2":
//                                Log.e("click","点击了");
//                                getVRLibrary().resetEyePick();
//                                VideoPlayerActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_WEB));
//                                break;
//                            case "menu3":
//                                break;
//                            case "menu4":
////                                ExVideoActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_C));
//                                getVRLibrary().resetEyePick();
//                                break;
//                        }
//                        Log.e("click", "看中了——view " + hotspot.getTag());

                    clickName=hotspot.getTag();
                        switch (hotspot.getTag()==null?"":hotspot.getTag()) {
                            case "menu1":
                                changeMenu(menu1,"menu1",menu1Position,true);
                                hideMenu("menu1");
                                break;
                            case "menu2":
                                changeMenu(menu2,"menu2",menu2Position,true);
                                hideMenu("menu2");
                                break;
                            case "menu3":
                                changeMenu(menu3,"menu3",menu3Position,true);
                                hideMenu("menu3");
                                break;
                            case "menu4":
                                changeMenu(menu4,"menu4",menu4Position,true);
                                hideMenu("menu4");
                                break;
                            case "top1":
                                changeTop(top1,"top1",top1Position,true);
                                hideTop("top1");
                                break;
                            case "top2":
                                changeTop(top2,"top2",top2Position,true);
                                hideTop("top2");
                                break;
                            case "top3":
                                changeTop(top3,"top3",top3Position,true);
                                hideTop("top3");
                                break;
                        }
//                        Log.e("click", "正在看——view " + hotspot.getTag());
                        isChangeHover=true;
                        changeHover();
                } else {
//                    Log.e("vr", "看空白位置nothing2");
                    isChangeHover=false;
                    changeHover();
                    hideMenu("all");
                    hideTop("all");

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
    //隐藏非焦点模块菜单
    private void hideMenu(String menu){
        switch (menu){
            case "menu1":
                changeMenu(menu2,"menu2",menu2Position,false);
                changeMenu(menu3,"menu3",menu3Position,false);
                changeMenu(menu4,"menu4",menu4Position,false);
                break;
            case "menu2":
                changeMenu(menu1,"menu1",menu1Position,false);
                changeMenu(menu3,"menu3",menu3Position,false);
                changeMenu(menu4,"menu4",menu4Position,false);
                break;
            case "menu3":
                changeMenu(menu1,"menu1",menu1Position,false);
                changeMenu(menu2,"menu2",menu2Position,false);
                changeMenu(menu4,"menu4",menu4Position,false);
                break;
            case "menu4":
                changeMenu(menu2,"menu2",menu2Position,false);
                changeMenu(menu3,"menu3",menu3Position,false);
                changeMenu(menu1,"menu1",menu1Position,false);
                break;
            default:
                changeMenu(menu1,"menu1",menu1Position,false);
                changeMenu(menu2,"menu2",menu2Position,false);
                changeMenu(menu3,"menu3",menu3Position,false);
                changeMenu(menu4,"menu4",menu4Position,false);
                break;
        }
    }
    //隐藏非焦点模块菜单
    private void hideTop(String top){
        switch (top){
            case "top1":
                changeTop(top2,"top2",top2Position,false);
                changeTop(top3,"top3",top3Position,false);
                break;
            case "top2":
                changeTop(top1,"top1",top1Position,false);
                changeTop(top3,"top3",top3Position,false);
                break;
            case "top3":
                changeTop(top2,"top2",top2Position,false);
                changeTop(top1,"top1",top1Position,false);
                break;
            default:
                changeTop(top2,"top2",top2Position,false);
                changeTop(top3,"top3",top3Position,false);
                changeTop(top1,"top1",top1Position,false);
                break;
        }
    }

    View topLogo,menu1,menu2,menu3;
    TextView menu4;
    TextView text1,text2,text3;
    TextView top1,top2,top3;
    TextView tvTime;
    ImageView imgWifi;
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

        top1 = new TextView(this);
        top1.setText("精选");
        top1.setTextSize(8);
        top1.setGravity(Gravity.CENTER_HORIZONTAL);
        top1.setTextColor(getResources().getColor(R.color.menu_top_nor));
        top2 = new TextView(this);
        top2.setText("风景");
        top2.setTextSize(8);
        top2.setGravity(Gravity.CENTER_HORIZONTAL);
        top2.setTextColor(getResources().getColor(R.color.menu_top_nor));
        top3 = new TextView(this);
        top3.setText("科技");
        top3.setTextSize(8);
        top3.setGravity(Gravity.CENTER_HORIZONTAL);
        top3.setTextColor(getResources().getColor(R.color.menu_top_nor));


        tvTime = new TextView(this);
//        top1.setBackgroundResource(R.drawable.met);
        tvTime.setText(TimeUtil.getDateForMenu());
        tvTime.setTextSize(4);
        tvTime.setTextColor(getResources().getColor(R.color.menu_top_nor));

        imgWifi = new ImageView(this);
        imgWifi.setBackgroundResource(R.drawable.wifi);

    }

    /**
     *
     * @param isChangePlace 是否更新显示位置，否则只更新内容
     */
    public void initMenu(boolean isChangePlace){
        initMenuView();
        initMenuTop();
        initToolBar(true);
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
                .position(menu1Position)
                .title("menu1")
                .tag("menu1")
                .listenClick(new MDVRLibrary.ITouchPickListener() {
                    @Override
                    public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
                        Log.e("viewclick","viewClick");
                    }
                });
        MDAbsView mdView1 = new MDView(builder1);
        //菜单二
        MDViewBuilder builder2 = MDViewBuilder.create();
        builder2.provider(menu2, 300, 200);
        builder2.size(3, 3);
        builder2.position(menu2Position);
        builder2.title("menu2");
        builder2.tag("menu2");

        builder2.listenClick(new MDVRLibrary.ITouchPickListener() {
            @Override
            public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
                Log.e("clck","----------click2");
            }
        });
        MDAbsView mdView2 = new MDView(builder2);
        //菜单三
        MDViewBuilder builder3 = MDViewBuilder.create()
                .provider(menu3, 300/*view width*/, 200/*view height*/)
                .size(3, 3)
                .position(menu3Position)
                .title("menu3")
                .tag("menu3");
        MDAbsView mdView3 = new MDView(builder3);
        //菜单四
        MDViewBuilder builder4 = MDViewBuilder.create()
                .provider(menu4, 300/*view width*/, 200/*view height*/)
                .size(3, 3)
                .position(menu4Position)
                .title("menu4")
                .tag("menu4");
        MDAbsView mdView4 = new MDView(builder4);

        if (isChangePlace){
            mdViewLogo.rotateToCamera();
            mdView1.rotateToCamera();
            mdView2.rotateToCamera();
            mdView3.rotateToCamera();
            mdView4.rotateToCamera();
        }
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

    public void initMenuTop(){
        //菜单一
        MDViewBuilder topBuild1 = MDViewBuilder.create()
                .provider(top1, 80, 50)
                .size(1, 0.80f)
                .position(top1Position)
                .title("top1")
                .tag("top1");
        MDAbsView mdViewTop1 = new MDView(topBuild1);
        //菜单二
        MDViewBuilder topBuild2 = MDViewBuilder.create()
                .provider(top2, 80, 50)
                .size(1, 0.80f)
                .position(top2Position)
                .title("top2")
                .tag("top2");
        MDAbsView mdViewTop2 = new MDView(topBuild2);
        //菜单三
        MDViewBuilder topBuild3 = MDViewBuilder.create();
        topBuild3.provider(top3, 80, 50);
        topBuild3.size(1, 0.80f);
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
    private void initToolBar(boolean isChangePlace){
        tvTime.setText(TimeUtil.getDateForMenu());
        //菜单一
        MDViewBuilder topBuild1 = MDViewBuilder.create()
                .provider(tvTime, 70, 18)
                .size(1, 0.35f)
                .position(topTimePosition)
                .title("toptime")
                .tag("toptime");
        MDAbsView mdViewTopTime = new MDView(topBuild1);
        plugins.add(mdViewTopTime);
        if (isChangePlace){
            mdViewTopTime.rotateToCamera();
        }
        getVRLibrary().addPlugin(mdViewTopTime);
        //菜单一
        MDViewBuilder topwifi = MDViewBuilder.create()
                .provider(imgWifi, 50, 50)
                .size(0.35f, 0.35f)
                .position(topWifiPosition)
                .title("topwifi")
                .tag("topwifi");
        MDAbsView mdViewTopWifi = new MDView(topwifi);
        plugins.add(mdViewTopWifi);
        if (isChangePlace){
            mdViewTopWifi.rotateToCamera();
        }
        getVRLibrary().addPlugin(mdViewTopWifi);
    }

    private Runnable updataTimeRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("updata","updata time ");
            initToolBar(false);
            handler.postDelayed(updataTimeRunnable,30000);
        }
    };

    private boolean toChangeBack=true;
    private boolean toChange=true;
    private boolean toChangeBack2=true;
    private boolean toChange2=true;
    private boolean toChangeBack3=true;
    private boolean toChange3=true;

    /**
     *
     * @param view 传入的顶部选项view
     * @param viewName  顶部选项view的tag
     * @param position  坐标信息
     * @param change 是否改变状态
     */
    private void changeTop(TextView view,String viewName,MDPosition position,boolean change){
        if (view==null){
            Log.e("view","textView更新");
            view=new TextView(this);
        }
        if (change){
            //改变状态
            switch (viewName){
                case "top1":
                    if (toChange){
                        toChange=false;
                        toChangeBack=true;
                        Log.e("view","更新");
                        view.setText("精选");
//                        view.setTextSize(10);
                        view.setTextColor(getResources().getColor(R.color.menu_top_pass));
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName));
                        //菜单一
                        MDViewBuilder topBuild1 = MDViewBuilder.create()
                                .provider(view, 80, 50)
                                .size(1.2f, 1.0f)
                                .position(position)
                                .title(viewName)
                                .tag(viewName+"change");
                        MDAbsView mdViewTop1 = new MDView(topBuild1);
                        plugins.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
                case "top2":
                    if (toChange2){
                        toChange2=false;
                        toChangeBack2=true;
                        Log.e("view","更新");
                        view.setText("风景");
//                        view.setTextSize(10);
                        view.setTextColor(getResources().getColor(R.color.menu_top_pass));
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName));
                        //菜单一
                        MDViewBuilder topBuild1 = MDViewBuilder.create()
                                .provider(view, 80, 50)
                                .size(1.2f, 1.0f)
                                .position(position)
                                .title(viewName)
                                .tag(viewName+"change");
                        MDAbsView mdViewTop1 = new MDView(topBuild1);
                        plugins.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
                case "top3":
                    if (toChange3){
                        toChange3=false;
                        toChangeBack3=true;
                        Log.e("view","更新");
                        view.setText("科技");
//                        view.setTextSize(10);
                        view.setTextColor(getResources().getColor(R.color.menu_top_pass));
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName));
                        //菜单一
                        MDViewBuilder topBuild1 = MDViewBuilder.create()
                                .provider(view, 80, 50)
                                .size(1.2f, 1.0f)
                                .position(position)
                                .title(viewName)
                                .tag(viewName+"change");
                        MDAbsView mdViewTop1 = new MDView(topBuild1);
                        plugins.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
            }

        }else{
            //还原初始状态
            switch (viewName){
                case "top1":
                    if (toChangeBack){
                        toChangeBack=false;
                        toChange=true;
                        Log.e("view","还原更新");
                        view.setText("精选");
//                        view.setTextSize(8);
                        view.setTextColor(getResources().getColor(R.color.menu_top_nor));
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName+"change"));
                        //菜单一
                        MDViewBuilder topBuild1 = MDViewBuilder.create()
                                .provider(view, 80, 50)
                                .size(1, 0.80f)
                                .position(position)
                                .title(viewName)
                                .tag(viewName);
                        MDAbsView mdViewTop1 = new MDView(topBuild1);
                        plugins.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
                case "top2":
                    if (toChangeBack2){
                        toChangeBack2=false;
                        toChange2=true;
                        Log.e("view","还原更新");
                        view.setText("风景");
//                        view.setTextSize(8);
                        view.setTextColor(getResources().getColor(R.color.menu_top_nor));
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName+"change"));
                        //菜单一
                        MDViewBuilder topBuild1 = MDViewBuilder.create()
                                .provider(view, 80, 50)
                                .size(1, 0.80f)
                                .position(position)
                                .title(viewName)
                                .tag(viewName);
                        MDAbsView mdViewTop1 = new MDView(topBuild1);
                        plugins.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
                case "top3":
                    if (toChangeBack3){
                        toChangeBack3=false;
                        toChange3=true;
                        Log.e("view","还原更新");
                        view.setText("科技");
//                        view.setTextSize(8);
                        view.setTextColor(getResources().getColor(R.color.menu_top_nor));
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName+"change"));
                        //菜单一
                        MDViewBuilder topBuild1 = MDViewBuilder.create()
                                .provider(view, 80, 50)
                                .size(1, 0.80f)
                                .position(position)
                                .title(viewName)
                                .tag(viewName);
                        MDAbsView mdViewTop1 = new MDView(topBuild1);
                        plugins.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
            }

        }
    }
    /**
     *
     * @param change 当1 时，改变，0 时还原
     */
    private boolean toChangeMenu1=true;
    private boolean toChangeMenuBack1=true;
    private boolean toChangeMenu2=true;
    private boolean toChangeMenuBack2=true;
    private boolean toChangeMenu3=true;
    private boolean toChangeMenuBack3=true;
    private boolean toChangeMenu4=true;
    private boolean toChangeMenuBack4=true;
    private MDViewBuilder menuBuilder;
    private void changeMenu(View view,String viewName,MDPosition position,boolean change){
        if (view==null){
            view = new ImageView(this);
            view.setBackgroundResource(R.drawable.img_page);
        }
        if (change){
            switch (viewName){
                case "menu1":
                    if (toChangeMenu1){
                        toChangeMenu1=false;
                        toChangeMenuBack1=true;
                        Log.e("view","更新");
//                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName));
                        //菜单一
                        MDViewBuilder topBuild1 = MDViewBuilder.create()
                                .provider(view, 300/*view width*/, 200/*view height*/)
                                .size(3.5f, 3.5f)
                                .position(position)
                                .title(viewName)
                                .tag(viewName+"change");

                        MDAbsView mdViewTop1 = new MDView(topBuild1);
                        plugins.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);

                    }
                    break;
                case "menu2":
                    if (toChangeMenu2){
                        toChangeMenu2=false;
                        toChangeMenuBack2=true;
                        Log.e("view","更新");
//                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName));
                        //菜单一
                        MDViewBuilder topBuild1 = MDViewBuilder.create()
                                .provider(view, 300/*view width*/, 200/*view height*/)
                                .size(3.5f, 3.5f)
                                .position(position)
                                .title(viewName)
                                .tag(viewName+"change")
                                .listenClick(new MDVRLibrary.ITouchPickListener() {
                                    @Override
                                    public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
                                        Log.e("clck","----------click2");
                                    }
                                });
                        MDAbsView mdViewTop1 = new MDView(topBuild1);
                        plugins.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
                case "menu3":
                    if (toChangeMenu3){
                        toChangeMenu3=false;
                        toChangeMenuBack3=true;
                        Log.e("view","更新");
//                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName));
                        //菜单一
                        MDViewBuilder topBuild1 = MDViewBuilder.create()
                                .provider(view, 300/*view width*/, 200/*view height*/)
                                .size(3.5f, 3.5f)
                                .position(position)
                                .title(viewName)
                                .tag(viewName+"change");
                        MDAbsView mdViewTop1 = new MDView(topBuild1);
                        plugins.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
                case "menu4":
                    if (toChangeMenu4){
                        toChangeMenu4=false;
                        toChangeMenuBack4=true;
                        Log.e("view","更新");
//                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName));
                        //菜单一
                        MDViewBuilder topBuild1 = MDViewBuilder.create()
                                .provider(view, 300/*view width*/, 200/*view height*/)
                                .size(3.5f, 3.5f)
                                .position(position)
                                .title(viewName)
                                .tag(viewName+"change");
                        MDAbsView mdViewTop1 = new MDView(topBuild1);
                        plugins.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
            }
        }else{
            switch (viewName){
                case "menu1":
                    if (toChangeMenuBack1){
                        toChangeMenuBack1=false;
                        toChangeMenu1=true;
                        Log.e("view","还原更新");
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName+"change"));
                        //菜单一
                        MDViewBuilder topBuild1 = MDViewBuilder.create()
                                .provider(view, 300/*view width*/, 200/*view height*/)
                                .size(3, 3)
                                .position(position)
                                .title(viewName)
                                .tag(viewName);
                        MDAbsView mdViewTop1 = new MDView(topBuild1);
                        plugins.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);

                    }
                    break;
                case "menu2":
                    if (toChangeMenuBack2){
                        toChangeMenuBack2=false;
                        toChangeMenu2=true;
                        Log.e("view","还原更新");
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName+"change"));
                        //菜单一
                        MDViewBuilder topBuild1 = MDViewBuilder.create()
                                .provider(view, 300/*view width*/, 200/*view height*/)
                                .size(3, 3)
                                .position(position)
                                .title(viewName)
                                .tag(viewName)
                                .listenClick(new MDVRLibrary.ITouchPickListener() {
                                    @Override
                                    public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
                                        Log.e("clck","----------click2");
                                    }
                                });
                        MDAbsView mdViewTop1 = new MDView(topBuild1);
                        plugins.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
                case "menu3":
                    if (toChangeMenuBack3){
                        toChangeMenuBack3=false;
                        toChangeMenu3=true;
                        Log.e("view","还原更新");
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName+"change"));
                        //菜单一
                        MDViewBuilder topBuild1 = MDViewBuilder.create()
                                .provider(view, 300/*view width*/, 200/*view height*/)
                                .size(3, 3)
                                .position(position)
                                .title(viewName)
                                .tag(viewName);
                        MDAbsView mdViewTop1 = new MDView(topBuild1);
                        plugins.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
                case "menu4":
                    if (toChangeMenuBack4){
                        toChangeMenuBack4=false;
                        toChangeMenu4=true;
                        Log.e("view","还原更新");
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName+"change"));
                        //菜单一
                        MDViewBuilder topBuild1 = MDViewBuilder.create()
                                .provider(view, 300/*view width*/, 200/*view height*/)
                                .size(3, 3)
                                .position(position)
                                .title(viewName)
                                .tag(viewName);
                        MDAbsView mdViewTop1 = new MDView(topBuild1);
                        plugins.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
            }
        }
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


    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(updataTimeRunnable,1000);
        mVRLibrary.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updataTimeRunnable);
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