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
public abstract class ExVideoActivity extends Activity {

    private static final String TAG = "ExVideoActivity";

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

    // load resource from android drawable only.
    private MDVRLibrary.IImageLoadProvider mAndroidProvider = new AndroidProvider(this);

    private List<MDAbsPlugin> plugins = new LinkedList<>();

    private MDPosition logoPosition = MDMutablePosition.newInstance().setY(-8.0f).setYaw(-90.0f);
    private MDPosition backPosition = MDPosition.newInstance().setZ(-8.0f).setX(-1.6f).setY(-5.0f).setYaw(-45.0f);
    private MDPosition playPosition = MDPosition.newInstance().setZ(-8.0f).setX(-0.6f).setY(-5.0f).setYaw(-45.0f);

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


        Log.e("vido","asdff");
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


        getVRLibrary().setEyePickChangedListener(new MDVRLibrary.IEyePickListener() {
            @Override
            public void onHotspotHit(IMDHotspot hotspot, long hitTimestamp) {
                if (hotspot!=null){
//                    if (System.currentTimeMillis() - hitTimestamp > 1000)
                    clickName=hotspot.getTag();
                 switch (hotspot.getTag()==null?"":hotspot.getTag()) {
                            case "ivClose":

                                break;
                            case "ivPlay":
                                playBtn(true);
                                break;
                        }

                        Log.e("vr","nothing2");
                        playBtn(false);
                }

            }
        });


    }

    //初始化页面模块
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
        mdView.rotateToCamera();
        mdView2.rotateToCamera();
        plugins.add(mdView);
        plugins.add(mdView2);
        getVRLibrary().addPlugin(mdView);
        getVRLibrary().addPlugin(mdView2);
    }

    private View ivPlay,ivClose;
    public void playBtn(boolean change){
        if (ivPlay==null){
            Log.e("view","textView更新");
            ivPlay=new ImageView(this);
            ivPlay.setBackgroundResource(R.drawable.ic_pause);
        }
        if (change){
            //暂停播放
            ivPlay.setBackgroundResource(R.drawable.ic_play);
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
        }else{
            ivPlay.setBackgroundResource(R.drawable.ic_pause);
//            getVRLibrary().removePlugin(getVRLibrary().findViewByTag("ivPlaying"));
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

//    MDViewBuilder builder;
//    MDAbsView mdView;
//    View view;
//    public void addTwo(int width,int height){
//        view = new ImageView(this);
//        view.setBackgroundResource(R.drawable.img_page);
//        builder = MDViewBuilder.create()
//                .provider(view, width, height)
//                .size(3, 2)
//                .position(MDPosition.newInstance().setZ(-8.0f).setX(1.6f))
//                .title("md view")
//                .tag("B")
//                ;
////        builder.listenClick(new MDVRLibrary.ITouchPickListener() {
////            @Override
////            public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
////                Log.e("vr","two_listenclick....");
////            }
////        });
//        mdView = new MDView(builder);
//        mdView.rotateToCamera();
//        plugins.add(mdView);
//        getVRLibrary().addPlugin(mdView);
//    }

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
        Log.e("click","video destory");
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