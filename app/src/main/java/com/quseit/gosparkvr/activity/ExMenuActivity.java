package com.quseit.gosparkvr.activity;

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
import com.asha.vrlib.model.MDViewBuilder;
import com.asha.vrlib.model.position.MDMutablePosition;
import com.asha.vrlib.plugins.MDAbsPlugin;
import com.asha.vrlib.plugins.hotspot.IMDHotspot;
import com.asha.vrlib.plugins.hotspot.MDAbsView;
import com.asha.vrlib.plugins.hotspot.MDView;
import com.asha.vrlib.texture.MD360BitmapTexture;
import com.quseit.gosparkvr.App;
import com.quseit.gosparkvr.Config;
import com.quseit.gosparkvr.R;
import com.quseit.gosparkvr.util.JsonUtil;
import com.quseit.gosparkvr.util.TimeUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
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
        start(context, uri, MenuActivity.class);
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

//    private MDPosition logoPosition = MDMutablePosition.newInstance().setY(-13.5f).setYaw(-90.0f);

    private MDPosition topLogoPosition = MDMutablePosition.newInstance().setZ(-13.5f).setX(-4.8f).setY(4.1f);

    private MDPosition topTimePosition = MDMutablePosition.newInstance().setZ(-13.5f).setX(4.8f).setY(4.2f);
    private MDPosition topWifiPosition = MDMutablePosition.newInstance().setZ(-13.5f).setX(4.1f).setY(4.2f);

    private MDPosition top1Position = MDMutablePosition.newInstance().setZ(-13.5f).setX(-1.2f).setY(3.6f);
    private MDPosition top2Position = MDMutablePosition.newInstance().setZ(-13.5f).setY(3.6f);
    private MDPosition top3Position = MDMutablePosition.newInstance().setZ(-13.5f).setX(1.2f).setY(3.6f);



    private MDPosition menu1Position = MDPosition.newInstance().setZ(-13.5f).setX(-3.2f).setY(1.6f);
    private MDPosition menu2Position = MDPosition.newInstance().setZ(-13.5f).setX(0.0f).setY(1.6f);
    private MDPosition menu3Position = MDPosition.newInstance().setZ(-13.5f).setX(3.2f).setY(1.6f);
    private MDPosition menu4Position = MDPosition.newInstance().setZ(-13.5f).setX(-3.2f).setY(-1.8f);
    private MDPosition menu5Position = MDPosition.newInstance().setZ(-13.5f).setX(0.0f).setY(-1.8f);
    private MDPosition menu6Position = MDPosition.newInstance().setZ(-13.5f).setX(3.2f).setY(-1.8f);


    private MDPosition leftPosition = MDPosition.newInstance().setZ(-13.5f).setX(-5.5f);
    private MDPosition rightPosition = MDPosition.newInstance().setZ(-13.5f).setX(5.5f);

//    private MDPosition pagePosition = MDPosition.newInstance().setZ(-13.5f).setY(-4.5f);

    private MDPosition[] pagePositions = new MDPosition[]{
            MDPosition.newInstance().setZ(-13.5f).setX(-1.0f).setY(-4.5f),
            MDPosition.newInstance().setZ(-13.5f).setY(-4.5f),
            MDPosition.newInstance().setZ(-13.5f).setX(1.0f).setY(-4.5f),
            MDPosition.newInstance().setZ(-13.5f).setX(-2.0f).setY(-4.5f),
            MDPosition.newInstance().setZ(-13.5f).setX(2.0f).setY(-4.5f),
            MDPosition.newInstance().setZ(-13.5f).setX(2.6f).setY(-4.5f),

    };

    private MDPosition[] positions = new MDPosition[]{
            MDPosition.newInstance().setZ(-13.5f).setX(-3.2f).setY(1.6f),
            MDPosition.newInstance().setZ(-13.5f).setX(0.0f).setY(1.6f),
            MDPosition.newInstance().setZ(-13.5f).setX(3.2f).setY(1.6f),
            MDPosition.newInstance().setZ(-13.5f).setX(-3.2f).setY(-1.8f),
            MDPosition.newInstance().setZ(-13.5f).setX(0.0f).setY(-1.8f),
            MDPosition.newInstance().setZ(-13.5f).setX(3.2f).setY(-1.8f),
    };
    private String[] menus = new String[]{
            "menu1",
            "menu2",
            "menu3",
            "menu4",
            "menu5",
            "menu6",
    };
    private String[] pages = new String[]{
            "page1",
            "page2",
            "page3",
            "page4",
            "page5",
            "page6",
    };
    private MDViewBuilder[] mdViewBuilders = new MDViewBuilder[]{
            builder1=MDViewBuilder.create(),
            builder2=MDViewBuilder.create(),
            builder3=MDViewBuilder.create(),
            builder4=MDViewBuilder.create(),
            builder5=MDViewBuilder.create(),
            builder6=MDViewBuilder.create(),
    };
    private MDViewBuilder[] mdViewPageBuilders = new MDViewBuilder[]{
            builder1=MDViewBuilder.create(),
            builder2=MDViewBuilder.create(),
            builder3=MDViewBuilder.create(),
            builder4=MDViewBuilder.create(),
            builder5=MDViewBuilder.create(),
            builder6=MDViewBuilder.create(),
    };
    private List<MDAbsView> mdAbsViewList;      //已设置定位的View
    private List<MDAbsView> mdAbsViewPageList;      //已设置定位的View
    private List<MDAbsView> mdAbsTopViewList;   //顶部已设置定位的View

    private volatile int numChange=0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
//                    getVRLibrary().removePlugin(getVRLibrary().findViewByTag("menu1"));
//                    changeMenu(menu1,"menu1",menu1Position,false);
//                    initMenu(true);

                    if (numChange==imgViewList.size()&&numChange!=0){
                        for (int i=0;i<imgViewList.size();i++){
//                            Log.e("dd","移除："+i);
                            getVRLibrary().removePlugin(mdAbsViewList.get(i));
                        }
                        updataMenu(false);
                    }
                    break;
            }
        }
    };
    private String clickName="";
    private int resAllNum=0;           //数据总条数
    private int resOnePageNum=6;    //每页显示条数
    private int resAllPage=1;          //总页数
    private volatile int thisPage=1;         //当前页
    private int lastPageNum=1;      //最后一页的数量（求页数时的余数）
    private volatile int selectTopId=0;      //选中的Top菜单的角标
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initData();
        btnEven();      //按钮点击事件
        btnLooking();   //按钮聚焦事件
    }
    //初始化
    private void init(){
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
        //左右两点视线焦点
        ivLeft = (ImageView) findViewById(R.id.iv_hover_left);
        ivRight = (ImageView) findViewById(R.id.iv_hover_right);
    }

    List<HashMap<String,String>> dataMap;
    //初始化页数，数据
    private void initData(){
        //获取数据条数
        dataMap = JsonUtil.getMenuData(selectTopId);
        resAllNum = dataMap.size();
        if (JsonUtil.getTopMenuData()!=null){
            strTop1 = JsonUtil.getTopMenuData().get(0);
            strTop2 = JsonUtil.getTopMenuData().get(1);
            strTop3 = JsonUtil.getTopMenuData().get(2);
        }

        App.e("数据总条数："+resAllNum);
//        resAllNum=13;
        //算出页码数
        if (resAllNum <= 6) {
            resAllPage = 1;
        } else if (resAllNum > 6) {//数据多于6时
            if (resAllNum % resOnePageNum == 0) {
                resAllPage = resAllNum / resOnePageNum;//总页数
            } else {
                resAllPage = resAllNum / resOnePageNum + 1;//总页数
                //当最后一页为1时，
                lastPageNum = resAllNum % resOnePageNum;//最后一页的条目数
                App.e("求摩剩余："+lastPageNum);
            }
            App.e("有多少页：" + resAllPage);
        }
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
    private void hideMenuChange(String menu){
        switch (menu){
            case "menu1":
                changeMenu(menu2,"menu2",menu2Position,false);
                changeMenu(menu3,"menu3",menu3Position,false);
                changeMenu(menu4,"menu4",menu4Position,false);
                changeMenu(menu5,"menu5",menu5Position,false);
                changeMenu(menu6,"menu6",menu6Position,false);
                break;
            case "menu2":
                changeMenu(menu1,"menu1",menu1Position,false);
                changeMenu(menu3,"menu3",menu3Position,false);
                changeMenu(menu4,"menu4",menu4Position,false);
                changeMenu(menu5,"menu5",menu5Position,false);
                changeMenu(menu6,"menu6",menu6Position,false);
                break;
            case "menu3":
                changeMenu(menu1,"menu1",menu1Position,false);
                changeMenu(menu2,"menu2",menu2Position,false);
                changeMenu(menu4,"menu4",menu4Position,false);
                changeMenu(menu5,"menu5",menu5Position,false);
                changeMenu(menu6,"menu6",menu6Position,false);
                break;
            case "menu4":
                changeMenu(menu1,"menu1",menu1Position,false);
                changeMenu(menu2,"menu2",menu2Position,false);
                changeMenu(menu3,"menu3",menu3Position,false);
                changeMenu(menu5,"menu5",menu5Position,false);
                changeMenu(menu6,"menu6",menu6Position,false);
                break;
            case "menu5":
                changeMenu(menu1,"menu1",menu1Position,false);
                changeMenu(menu2,"menu2",menu2Position,false);
                changeMenu(menu3,"menu3",menu3Position,false);
                changeMenu(menu4,"menu4",menu4Position,false);
                changeMenu(menu6,"menu6",menu6Position,false);
                break;
            case "menu6":
                changeMenu(menu1,"menu1",menu1Position,false);
                changeMenu(menu2,"menu2",menu2Position,false);
                changeMenu(menu3,"menu3",menu3Position,false);
                changeMenu(menu4,"menu4",menu4Position,false);
                changeMenu(menu5,"menu5",menu5Position,false);
                break;
            default:
                changeMenu(menu1,"menu1",menu1Position,false);
                changeMenu(menu2,"menu2",menu2Position,false);
                changeMenu(menu3,"menu3",menu3Position,false);
                changeMenu(menu4,"menu4",menu4Position,false);
                changeMenu(menu5,"menu5",menu5Position,false);
                changeMenu(menu6,"menu6",menu6Position,false);
                break;
        }
    }

    private boolean isInit=true;
    //隐藏非焦点顶部模块菜单
    private void hideTopChange(String top){
        switch (top){
            case "top1":
                changeTop(top2,"top2",top2Position,false);
                changeTop(top3,"top3",top3Position,false);
                isInit=true;
                break;
            case "top2":
                changeTop(top1,"top1",top1Position,false);
                changeTop(top3,"top3",top3Position,false);
                isInit=true;
                break;
            case "top3":
                changeTop(top2,"top2",top2Position,false);
                changeTop(top1,"top1",top1Position,false);
                isInit=true;
                break;
            default:
                if (isInit){
//                    changeTop(top2,"top2",top2Position,false);
//                    changeTop(top1,"top1",top1Position,false);
//                    changeTop(top3,"top3",top3Position,false);
                    loadMenuTop(false);
                    isInit=false;
                }
//                if (topStatus==TOP_A){
//                    changeTop(top2,"top2",top2Position,false);
//                    changeTop(top3,"top3",top3Position,false);
//                }else if (topStatus==TOP_B){
//                    changeTop(top3,"top3",top3Position,false);
//                    changeTop(top1,"top1",top1Position,false);
//                }else{
//                    changeTop(top1,"top1",top1Position,false);
//                    changeTop(top2,"top2",top2Position,false);
//                }


                break;
        }
    }
    //按钮点击事件（点击屏幕）
    private void btnEven(){
        findViewById(R.id.ll_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("----click",clickName);
                switch (clickName){
                    case "top1change":
                    case "top1":
                        numChange=0;
                        if (selectTopId!=0){
                            selectTopId=0;
                            initData();
                            thisPage=1;
                            App.e("当前页为："+thisPage);
                            //移除掉先前的view
                            for (int i=0;i<mdAbsViewList.size();i++){
                                Log.e("dd","移除MenuView："+i);
                                getVRLibrary().removePlugin(mdAbsViewList.get(i));
                            }
                            for (int i=0;i<mdAbsTopViewList.size();i++){
                                Log.e("dd","移除TopView："+i);
                                getVRLibrary().removePlugin(mdAbsTopViewList.get(i));
                            }
                            for (int i=0;i<mdAbsViewPageList.size();i++){
                                Log.e("dd","移除pageView："+i);
                                getVRLibrary().removePlugin(mdAbsViewPageList.get(i));
                            }
                            loadMenuTop(false);
                            updataMenu(false);
                        }
                        break;
                    case "top2change":
                    case "top2":
                        numChange=0;
                        if (selectTopId!=1){
                            selectTopId=1;
                            initData();
                            thisPage=1;
                            App.e("当前页为："+thisPage);
                            //移除掉先前的view
                            for (int i=0;i<mdAbsViewList.size();i++){
                                Log.e("dd","移除MenuView："+i);
                                getVRLibrary().removePlugin(mdAbsViewList.get(i));
                            }
                            for (int i=0;i<mdAbsTopViewList.size();i++){
                                Log.e("dd","移除TopView："+i);
                                getVRLibrary().removePlugin(mdAbsTopViewList.get(i));
                            }
                            for (int i=0;i<mdAbsViewPageList.size();i++){
                                Log.e("dd","移除pageView："+i);
                                getVRLibrary().removePlugin(mdAbsViewPageList.get(i));
                            }

                            loadMenuTop(false);
                            updataMenu(false);
                        }
//                            changeTop(top2,"top2",top2Position,true);
//                            hideTopChange("top2");
                        break;
                    case "top3change":
                    case "top3":
                        numChange=0;
                        if (selectTopId!=2){
                            selectTopId=2;
                            initData();
                            thisPage=1;
                            App.e("当前页为："+thisPage);
                            //移除掉先前的view
                            for (int i=0;i<mdAbsViewList.size();i++){
                                Log.e("dd","移除MenuView："+i);
                                getVRLibrary().removePlugin(mdAbsViewList.get(i));
                            }
                            for (int i=0;i<mdAbsTopViewList.size();i++){
                                Log.e("dd","移除TopView："+i);
                                getVRLibrary().removePlugin(mdAbsTopViewList.get(i));
                            }
                            for (int i=0;i<mdAbsViewPageList.size();i++){
                                Log.e("dd","移除pageView："+i);
                                getVRLibrary().removePlugin(mdAbsViewPageList.get(i));
                            }

                            loadMenuTop(false);
                            updataMenu(false);
                        }
//                            changeTop(top3,"top3",top3Position,true);
//                            hideTopChange("top3");
                        break;
                    case "menu1change":
                    case "menu1":
                        int set =thisPage*resOnePageNum-resOnePageNum;
                        App.e("选中了："+JsonUtil.getMenuData(selectTopId).get(set));

                        VideoPlayerActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_WEB));
                        break;
                    case "menu2change":
                    case "menu2":
                        VideoPlayerActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_WEB));
                        break;
                    case "menu3change":
                    case "menu3":
                        VideoPlayerActivity.startVideo(ExMenuActivity.this, Uri.parse("http://om4nwud6z.bkt.clouddn.com/urlvideo.mp4?attname=&e=1506064759&token=1z9s_GAMHFa23N_E15oSlfgtA3gDLDMl5qyo9MmT:ZltfhDg1HGTdNw4r6WAtrOVfNl8"));
                        break;
                    case "menu4change":
                    case "menu4":
                        VideoPlayerActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_WEB));
                        break;
                    case "menu5change":
                    case "menu5":
                        VideoPlayerActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_WEB));
                        break;
                    case "menu6change":
                    case "menu6":
                        VideoPlayerActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_WEB));
                        break;

                    case "leftbtn":
                        numChange=0;
                        //上一页按钮
                        if (thisPage!=1){
                            thisPage=thisPage-1;
                            App.e("当前页为："+thisPage);
                            //移除掉先前的view
                            for (int i=0;i<mdAbsViewList.size();i++){
                                Log.e("dd","移除MenuView："+i);
                                getVRLibrary().removePlugin(mdAbsViewList.get(i));
                            }
                            updataMenu(false);
                        }
                        break;
                    case "rightbtn":
                        numChange=0;
                        //下一页按钮
                        if (thisPage<resAllPage){
                            thisPage=thisPage+1;
                            App.e("当前页为："+thisPage);
                            //移除掉先前的view
                            for (int i=0;i<mdAbsViewList.size();i++){
                                Log.e("dd","移除MenuView："+i);
                                getVRLibrary().removePlugin(mdAbsViewList.get(i));
                            }
                            updataMenu(false);
                        }
                        break;
                }
            }
        });
        //重定位菜单
        findViewById(R.id.ll_view).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getVRLibrary().removePlugins();
                initMenu(true);
                return true;
            }
        });
    }

    //视线聚焦时事件
    private void btnLooking(){
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
//                            case "menu6":
////                                ExVideoActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_C));
//                                getVRLibrary().resetEyePick();
//                                break;
//                        }
//                        Log.e("click", "看中了——view " + hotspot.getTag());

                    clickName=hotspot.getTag();
                    switch (hotspot.getTag()==null?"":hotspot.getTag()) {
                        case "menu1":
                            changeMenu(imgViewList.get(0),"menu1",positions[0],true);
                            hideMenuChange("menu1");
                            break;
                        case "menu2":
                            changeMenu(imgViewList.get(1),"menu2",positions[1],true);
                            hideMenuChange("menu2");
                            break;
                        case "menu3":
                            changeMenu(imgViewList.get(2),"menu3",positions[2],true);
                            hideMenuChange("menu3");
                            break;
                        case "menu4":
                            changeMenu(imgViewList.get(3),"menu4",positions[3],true);
                            hideMenuChange("menu4");
                            break;
                        case "menu5":
                            changeMenu(imgViewList.get(4),"menu5",positions[4],true);
                            hideMenuChange("menu5");
                            break;
                        case "menu6":
                            changeMenu(imgViewList.get(5),"menu6",positions[5],true);
                            hideMenuChange("menu6");
                            break;
                        case "top1":
//                            changeTop(top1,"top1",top1Position,true);
//                            hideTopChange("top1");
                            break;
                        case "top2":
//                            changeTop(top2,"top2",top2Position,true);
//                            hideTopChange("top2");
                            break;
                        case "top3":
//                            changeTop(top3,"top3",top3Position,true);
//                            hideTopChange("top3");
                            break;
                        case "leftbtn":
//                            changeTop(top3,"top3",top3Position,true);
//                            hideTopChange("top3");
                            break;
                        case "rightbtn":
//                            changeTop(top3,"top3",top3Position,true);
//                            hideTopChange("top3");
                            break;
                    }
//                        Log.e("click", "正在看——view " + hotspot.getTag());
                    isChangeHover=true;
                    changeHover();
                } else {
//                    Log.e("vr", "看空白位置nothing2");
                    clickName="";
                    isChangeHover=false;
                    changeHover();
                    hideMenuChange("all");
//                    hideTopChange("all");
                }
                String text = hotspot == null ? "nop" : String.format(Locale.CHINESE, "%s  %fs", hotspot.getTitle(), (System.currentTimeMillis() - hitTimestamp) / 1000.0f);
                hotspotText.setText(text);

            }
        });
    }

    ImageView topLogo,menu1,menu2,menu3,menu4,menu5,menu6;
//    TextView menu6;
    TextView text1,text2,text3;
    TextView top1,top2,top3,pageNum;
    TextView tvTime;
    ImageView imgWifi,leftBtn,rightBtn;
    ImageView imgpPage1,imgpPage2,imgpPage3,imgpPage4,imgpPage5,imgpPage6;
    List<ImageView> imgViewList;        //生成的图片View（无定位）
    List<ImageView> imgPageList;        //生成的页码数图片View（无定位）

//    List<MDViewBuilder> mdViewBuilderList;
    //初始化View
//    private void initView(){
//        initTopBarView();//顶部工具栏
//        initTopView();//顶部菜单选项
//        initMenuView();//菜单6个
//    }

    //顶部工具栏
    private void initTopBarView(){
        //loge
        topLogo = new ImageView(this);
        topLogo.setBackgroundResource(R.drawable.ic_home_logo);
        //時間
        tvTime = new TextView(this);
//        top1.setBackgroundResource(R.drawable.met);
        tvTime.setText(TimeUtil.getDateForMenu());
        tvTime.setTextSize(4);
        tvTime.setTextColor(getResources().getColor(R.color.menu_top_nor));
        //wifi
        imgWifi = new ImageView(this);
        imgWifi.setBackgroundResource(R.drawable.wifi);

        //左右
        leftBtn = new ImageView(this);
        leftBtn.setBackgroundResource(R.drawable.ic_left);
        rightBtn = new ImageView(this);
        rightBtn.setBackgroundResource(R.drawable.ic_right);
    }

    private String strTop1="海绵",strTop2="宝宝",strTop3="派大星";
    //顶部菜单选项
    private void initTopView(){
        mdAbsTopViewList=new ArrayList<>();
        top1 = new TextView(this);
        top1.setText(strTop1);
        top1.setTextSize(8);
        top1.setGravity(Gravity.CENTER_HORIZONTAL);
        if (selectTopId==0){
            top1.setTextColor(getResources().getColor(R.color.menu_top_pass));
        }else{
            top1.setTextColor(getResources().getColor(R.color.menu_top_nor));
        }
        top2 = new TextView(this);
        top2.setText(strTop2);
        top2.setTextSize(8);
        top2.setGravity(Gravity.CENTER_HORIZONTAL);
        if (selectTopId==1){
            top2.setTextColor(getResources().getColor(R.color.menu_top_pass));
        }else{
            top2.setTextColor(getResources().getColor(R.color.menu_top_nor));
        }
        top3 = new TextView(this);
        top3.setText(strTop3);
        top3.setTextSize(8);
        top3.setGravity(Gravity.CENTER_HORIZONTAL);
        if (selectTopId==2){
            top3.setTextColor(getResources().getColor(R.color.menu_top_pass));
        }else{
            top3.setTextColor(getResources().getColor(R.color.menu_top_nor));
        }
    }
    //6菜单初始化及页码数
    private void initMenuView(){
        imgViewList = new ArrayList<>();
        mdAbsViewList = new ArrayList<>();

        imgPageList = new ArrayList<>();
        mdAbsViewPageList = new ArrayList<>();
            //当总页数只有一页时，加载六个菜单
            if (resAllPage==1){
                for (int i=0;i<resAllNum;i++){
                    ImageView menu = new ImageView(this);
//                    menu.setBackgroundResource(R.drawable.ych);
                    setMenuImg(menu,dataMap.get(i).get("pic"));
                    imgViewList.add(menu);
                }
            }else{//多于两页时:
                // 当最后一页时
                if (thisPage==resAllPage){
                        //根据最后一页的数量生成view
                        for (int i=0;i<lastPageNum;i++){
                                ImageView menu = new ImageView(this);
//                                menu.setBackgroundResource(R.drawable.ych);
                            setMenuImg(menu,dataMap.get(i).get("pic"));
                                imgViewList.add(menu);
                        }
                }//第一页或者中间页，显示 6
                else{
                    for (int i=0;i<resOnePageNum;i++){
                            ImageView menu = new ImageView(this);
                        setMenuImg(menu,dataMap.get(i).get("pic"));
//                            menu.setBackgroundResource(R.drawable.ych);
                            imgViewList.add(menu);
                    }
                }
            }
        //页数显示
//        pageNum = new TextView(this);
//        pageNum.setText(thisPage+"/"+resAllPage);
//        pageNum.setTextSize(8);
//        pageNum.setGravity(Gravity.CENTER_HORIZONTAL);
//        pageNum.setTextColor(getResources().getColor(R.color.menu_top_nor));

        for (int i=1;i<=resAllPage;i++){
            Log.e("生成：",i+"个");
            ImageView page = new ImageView(this);
            if (thisPage==i){
                page.setImageResource(R.drawable.ic_page_pass);
            }else{
                page.setImageResource(R.drawable.ic_page);
            }
            imgPageList.add(page);
        }

//        if (resAllNum<6){
//            menu1 = new ImageView(this);
//            menu1.setBackgroundResource(R.drawable.ych);
////            setMenuImg(menu1,"http://down1.cnmo.com/app/a141/haimianbaobao46.jpg");
//            imgViewList.add(menu1);
//
//            menu2 = new ImageView(this);
//            menu2.setBackgroundResource(R.drawable.ych);
//            imgViewList.add(menu2);
//
//            menu3 = new ImageView(this);
//            menu3.setBackgroundResource(R.drawable.met);
//            imgViewList.add(menu3);
//
//            menu4 = new ImageView(this);
//            menu4.setBackgroundResource(R.drawable.met);
//            imgViewList.add(menu4);
//
//            menu5 = new ImageView(this);
//            menu5.setBackgroundResource(R.drawable.met);
//            imgViewList.add(menu5);
//
//            menu6 = new ImageView(this);
//            menu6.setBackgroundResource(R.drawable.met);
//            imgViewList.add(menu6);
//
////            menu6 = new TextView(this);
////            menu6.setBackgroundResource(R.drawable.img_page);
//////        menu6.setText("下一页  ");
////            menu6.setGravity(Gravity.BOTTOM|Gravity.RIGHT);
////            menu6.setTextColor(getResources().getColor(R.color.vrwhile));
////            menu6.setTextSize(12);
//        }
    }

    //加载网络图片
    private void setMenuImg(ImageView img,String string){
        Picasso.with(this)
                .load(string)
                .resize(300, 200)
                .placeholder(R.drawable.img_empty)
                .error(R.drawable.img_page)
                .into(img, new Callback() {
                    @Override
                    public void onSuccess() {
//                        App.e("加载成功");
//                        updataMenu(menu1);//暫時无法自动更新
                        numChange+=1;
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    /**
     *
     * @param isChangePlace 是否更新显示位置，否则只更新内容
     */
    private MDViewBuilder builder1,builder2,builder3,builder4,builder5,builder6;
    //初始化所有View，一般用于重定位菜单，局部更新view的话另外的方法
    public void initMenu(boolean isChangePlace){
        App.e("当前页："+thisPage);
        initMenuView();//菜单6个
        loadTopBar(isChangePlace);//加载videoBar
        loadMenuTop(isChangePlace);//加载顶部菜单栏

        for (int i = 0;i<imgViewList.size();i++){
//            Log.e("size",i+"");
            if (imgViewList.get(i)!=null){
                mdViewBuilders[i]
                        .provider(imgViewList.get(i), 300/*view width*/, 200/*view height*/)
                        .size(3, 3)
                        .position(positions[i])
                        .title(menus[i])
                        .tag(menus[i]);
                MDAbsView mdView1 = new MDView(mdViewBuilders[i]);
                if (isChangePlace){
                    mdView1.rotateToCamera();
                }
                plugins.add(mdView1);
                mdAbsViewList.add(mdView1);
                getVRLibrary().addPlugin(mdView1);
            }
        }
        //页码
        for (int i = 0;i<imgPageList.size();i++){
            Log.e("page:","page has "+i);
            mdViewPageBuilders[i].provider(imgPageList.get(i), 80, 30)
                    .size(1, 0.30f)
                    .position(pagePositions[i])
                    .title(pages[i])
                    .tag(pages[i]);
            MDAbsView mdViewPage4 = new MDView(mdViewPageBuilders[i]);
            if (isChangePlace){
                mdViewPage4.rotateToCamera();
            }
            //哆此一步为了清空之前的view，否则会残留view
            getVRLibrary().removePlugin(getVRLibrary().findViewByTag(pages[i]));
            plugins.add(mdViewPage4);
            mdAbsViewPageList.add(mdViewPage4);
            getVRLibrary().addPlugin(mdViewPage4);
        }
    }

    //更新6菜单及页码显示
    public void updataMenu(boolean isChangePlace){
        App.e("当前页："+thisPage);
        initMenuView();//菜单6个及页码初始化
//        loadTopBar(isChangePlace);//加载videoBar
//        loadMenuTop(isChangePlace);//加载顶部菜单栏

        for (int i = 0;i<imgViewList.size();i++){
//            Log.e("size",i+"");
            if (imgViewList.get(i)!=null){
                mdViewBuilders[i] = MDViewBuilder.create()
                        .provider(imgViewList.get(i), 300/*view width*/, 200/*view height*/)
                        .size(3, 3)
                        .position(positions[i])
                        .title(menus[i])
                        .tag(menus[i]);
                MDAbsView mdView1 = new MDView(mdViewBuilders[i]);
                if (isChangePlace){
                    mdView1.rotateToCamera();
                }
                plugins.add(mdView1);
                mdAbsViewList.add(mdView1);
                getVRLibrary().addPlugin(mdView1);
            }
        }
        //页码
        for (int i = 0;i<imgPageList.size();i++){
            Log.e("page:","updata : page has "+i);
            MDViewBuilder topBuild4 = MDViewBuilder.create();
            topBuild4.provider(imgPageList.get(i), 80, 30);
            topBuild4.size(1, 0.30f);
            topBuild4.position(pagePositions[i]);
            topBuild4.title(pages[i]);
            topBuild4.tag(pages[i]);
            MDAbsView mdViewPage4 = new MDView(topBuild4);
            if (isChangePlace){
                mdViewPage4.rotateToCamera();
            }
            //哆此一步为了清空之前的view，否则会残留view
            getVRLibrary().removePlugin(getVRLibrary().findViewByTag(pages[i]));
            plugins.add(mdViewPage4);
            mdAbsViewPageList.add(mdViewPage4);
            getVRLibrary().addPlugin(mdViewPage4);
        }
    }

    private void updataMenu(ImageView view){
        handler.sendEmptyMessage(1);
    }

    //頂部菜单选项
    public void loadMenuTop(boolean isChange){
        initTopView();//顶部菜单选项
        //菜单一
        MDViewBuilder topBuild1 = MDViewBuilder.create();
        topBuild1.provider(top1, 80, 50);
        if (selectTopId==0){
            topBuild1.size(1.2f, 1.0f);
        }else{
            topBuild1.size(1, 0.80f);
        }
        topBuild1.position(top1Position);
        topBuild1.title("top1");
        topBuild1.tag("top1");
        MDAbsView mdViewTop1 = new MDView(topBuild1);
        //菜单二
        MDViewBuilder topBuild2 = MDViewBuilder.create();
        topBuild2.provider(top2, 80, 50);
        if (selectTopId==1){
            topBuild2.size(1.2f, 1.0f);
        }else{
            topBuild2.size(1, 0.80f);
        }
        topBuild2.position(top2Position);
        topBuild2.title("top2");
        topBuild2.tag("top2");
        MDAbsView mdViewTop2 = new MDView(topBuild2);
        //菜单三
        MDViewBuilder topBuild3 = MDViewBuilder.create();
        topBuild3.provider(top3, 80, 50);
        if (selectTopId==2){
            topBuild3.size(1.2f, 1.0f);
        }else{
            topBuild3.size(1, 0.80f);
        }
        topBuild3.position(top3Position);
        topBuild3.title("top3");
        topBuild3.tag("top3");
        MDAbsView mdViewTop3 = new MDView(topBuild3);


        if (isChange){
            mdViewTop1.rotateToCamera();
            mdViewTop2.rotateToCamera();
            mdViewTop3.rotateToCamera();
        }

        plugins.add(mdViewTop1);
        plugins.add(mdViewTop2);
        plugins.add(mdViewTop3);

        mdAbsTopViewList.add(mdViewTop1);
        mdAbsTopViewList.add(mdViewTop2);
        mdAbsTopViewList.add(mdViewTop3);
        getVRLibrary().addPlugin(mdViewTop1);
        getVRLibrary().addPlugin(mdViewTop2);
        getVRLibrary().addPlugin(mdViewTop3);
    }
    //加载顶部工具ToolBar（时间，logo，wifi，上一页下一页）
    private void loadTopBar(boolean isChangePlace){
        initTopBarView();//顶部工具栏
        //logo
        MDViewBuilder builderLoge = MDViewBuilder.create()
                .provider(topLogo, 60, 60)
                .size(1, 1)
                .position(topLogoPosition)
                .title("Logo")
                .tag("Logo");
        MDAbsView mdViewLogo = new MDView(builderLoge);
        plugins.add(mdViewLogo);
        if (isChangePlace){
            mdViewLogo.rotateToCamera();
        }
        getVRLibrary().addPlugin(mdViewLogo);

        //时间显示
        tvTime.setText(TimeUtil.getDateForMenu());
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
        }else{
            getVRLibrary().removePlugin(getVRLibrary().findViewByTag("toptime"));
        }
        getVRLibrary().addPlugin(mdViewTopTime);
        //wifi信号显示
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
        //上一页btn
        MDViewBuilder topleft = MDViewBuilder.create()
                .provider(leftBtn, 50, 50)
                .size(1.5f, 1.5f)
                .position(leftPosition)
                .title("leftbtn")
                .tag("leftbtn");
        MDAbsView mdViewLeft = new MDView(topleft);
        plugins.add(mdViewLeft);
        if (isChangePlace){
            mdViewLeft.rotateToCamera();
        }
        getVRLibrary().addPlugin(mdViewLeft);

        //下一页btn
        MDViewBuilder topright = MDViewBuilder.create()
                .provider(rightBtn, 50, 50)
                .size(1.5f, 1.5f)
                .position(rightPosition)
                .title("rightbtn")
                .tag("rightbtn");
        MDAbsView mdViewRight = new MDView(topright);
        plugins.add(mdViewRight);
        if (isChangePlace){
            mdViewRight.rotateToCamera();
        }
        getVRLibrary().addPlugin(mdViewRight);
    }

    //循环的更新时间
    private Runnable updataTimeRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("updata","updata time ");
            loadTopBar(false);
            handler.postDelayed(updataTimeRunnable,60000);
        }
    };

    private boolean toChangeBack=true;
    private boolean toChange=true;
    private boolean toChangeBack2=true;
    private boolean toChange2=true;
    private boolean toChangeBack3=true;
    private boolean toChange3=true;
    private ArrayList<String> cats = new ArrayList();
    private ArrayList<ArrayList<String>> navs = new ArrayList();
    private String curCat;
    private String curPage;

    /**
     * 被看到的时候
     * @param view 传入的顶部选项view
     * @param viewName  顶部选项view的tag
     * @param position  坐标信息
     * @param change 是否改变状态
     */
    private void changeTop(TextView view, String viewName, MDPosition position, boolean change){
        if (view==null){
//            Log.e("view","textView更新");
            view=new TextView(this);
        }
        if (change){
            //改变状态
            switch (viewName){
                case "top1":
                    if (toChange){
                        toChange=false;
                        toChangeBack=true;
//                        Log.e("view","更新");
                        view.setText(strTop1);
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
                        mdAbsTopViewList.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
                case "top2":
                    if (toChange2){
                        toChange2=false;
                        toChangeBack2=true;
//                        Log.e("view","更新");
                        view.setText(strTop2);
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
                        mdAbsTopViewList.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
                case "top3":
                    if (toChange3){
                        toChange3=false;
                        toChangeBack3=true;
//                        Log.e("view","更新");
                        view.setText(strTop3);
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
                        mdAbsTopViewList.add(mdViewTop1);
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
//                        Log.e("view","还原更新");
//                        view.setText(strTop1);
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
                        mdAbsTopViewList.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
                case "top2":
                    if (toChangeBack2){
                        toChangeBack2=false;
                        toChange2=true;
//                        Log.e("view","还原更新");
//                        view.setText(strTop2);
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
                        mdAbsTopViewList.add(mdViewTop1);
                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
                case "top3":
                    if (toChangeBack3){
                        toChangeBack3=false;
                        toChange3=true;
//                        Log.e("view","还原更新");
//                        view.setText(strTop3);
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
                        mdAbsTopViewList.add(mdViewTop1);
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
    private boolean toChangeMenu5=true;
    private boolean toChangeMenuBack5=true;
    private boolean toChangeMenu6=true;
    private boolean toChangeMenuBack6=true;
    private MDViewBuilder menuBuilder;
    //6菜单的放大放小切换
    private void changeMenu(View view, String viewName, MDPosition position, boolean change){
//        if (view==null){
//            view = new ImageView(this);
//            view.setBackgroundResource(R.drawable.img_page);
//        }
        if (change){
            //更新状态（放大）
            switch (viewName){
                case "menu1":
                    if (toChangeMenu1){
                        toChangeMenu1=false;
                        toChangeMenuBack1=true;
//                        Log.e("view","更新");
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
//                        Log.e("view","更新");
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
                case "menu3":
                    if (toChangeMenu3){
                        toChangeMenu3=false;
                        toChangeMenuBack3=true;
//                        Log.e("view","更新");
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
                case "menu5":
                    if (toChangeMenu5){
                        toChangeMenu5=false;
                        toChangeMenuBack5=true;
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
                case "menu6":
                    if (toChangeMenu6){
                        toChangeMenu6=false;
                        toChangeMenuBack6=true;
//                        Log.e("view","更新");
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
            //还原状态（实际上是去除之前生成的放大view，减少view刷新）
            switch (viewName){
                case "menu1":
                    if (toChangeMenuBack1){
                        toChangeMenuBack1=false;
                        toChangeMenu1=true;
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName+"change"));
//                        //菜单一
//                        MDViewBuilder topBuild1 = MDViewBuilder.create()
//                                .provider(view, 300/*view width*/, 200/*view height*/)
//                                .size(3, 3)
//                                .position(position)
//                                .title(viewName)
//                                .tag(viewName);
//                        MDAbsView mdViewTop1 = new MDView(topBuild1);
//                        plugins.add(mdViewTop1);
//                        getVRLibrary().addPlugin(mdViewTop1);
                    }
                    break;
                case "menu2":
                    if (toChangeMenuBack2){
                        toChangeMenuBack2=false;
                        toChangeMenu2=true;
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName+"change"));
                    }
                    break;
                case "menu3":
                    if (toChangeMenuBack3){
                        toChangeMenuBack3=false;
                        toChangeMenu3=true;
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName+"change"));
                    }
                    break;
                case "menu4":
                    if (toChangeMenuBack4){
                        toChangeMenuBack4=false;
                        toChangeMenu4=true;
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName+"change"));
                    }
                    break;
                case "menu5":
                    if (toChangeMenuBack5){
                        toChangeMenuBack5=false;
                        toChangeMenu5=true;
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName+"change"));
                    }
                    break;
                case "menu6":
                    if (toChangeMenuBack6){
                        toChangeMenuBack6=false;
                        toChangeMenu6=true;
                        getVRLibrary().removePlugin(getVRLibrary().findViewByTag(viewName+"change"));
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