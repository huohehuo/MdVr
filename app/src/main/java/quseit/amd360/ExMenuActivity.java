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
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asha.vrlib.MDDirectorCamUpdate;
import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.MDHotspotBuilder;
import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.model.MDRay;
import com.asha.vrlib.model.MDViewBuilder;
import com.asha.vrlib.model.position.MDMutablePosition;
import com.asha.vrlib.plugins.MDAbsPlugin;
import com.asha.vrlib.plugins.hotspot.IMDHotspot;
import com.asha.vrlib.plugins.hotspot.MDAbsHotspot;
import com.asha.vrlib.plugins.hotspot.MDAbsView;
import com.asha.vrlib.plugins.hotspot.MDSimpleHotspot;
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
    private MDPosition onePosition = MDPosition.newInstance().setZ(-8.0f).setX(-1.6f);
    private MDPosition twoPosition = MDPosition.newInstance().setZ(-8.0f).setX(1.6f);
    private MDPosition threePosition = MDPosition.newInstance().setZ(-8.0f).setX(1.6f).setY(-2.2f);
    private MDPosition fourPosition = MDPosition.newInstance().setZ(-8.0f).setX(-1.6f).setY(-2.2f);

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

        final List<View> hotspotPoints = new LinkedList<>();
        hotspotPoints.add(findViewById(R.id.hotspot_point1));
        hotspotPoints.add(findViewById(R.id.hotspot_point2));

        //添加底部logo
        findViewById(R.id.button_add_plugin_logo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MDHotspotBuilder builder = MDHotspotBuilder.create(mImageLoadProvider)
                        .size(4f, 4f)
                        .provider(activity, R.drawable.moredoo_logo)
                        .title("logo")
                        .position(logoPosition)
                        .listenClick(new MDVRLibrary.ITouchPickListener() {
                            @Override
                            public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
                                Toast.makeText(ExMenuActivity.this, "click logo", Toast.LENGTH_SHORT).show();
                            }
                        });
                MDAbsHotspot hotspot = new MDSimpleHotspot(builder);
                plugins.add(hotspot);
                getVRLibrary().addPlugin(hotspot);
                Toast.makeText(ExMenuActivity.this, "add plugin logo", Toast.LENGTH_SHORT).show();
            }
        });

        //按顺序去掉模块
        findViewById(R.id.button_remove_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (plugins.size() > 0) {
                    MDAbsPlugin plugin = plugins.remove(plugins.size() - 1);
                    getVRLibrary().removePlugin(plugin);
                }
            }
        });

        //添加热点logo
        findViewById(R.id.button_add_hotspot_front).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MDHotspotBuilder builder = MDHotspotBuilder.create(mImageLoadProvider)
                        .size(4f, 4f)
                        .provider(activity, R.drawable.moredoo_logo)
                        .title("front logo")
                        .tag("tag-front")
                        .position(MDPosition.newInstance().setZ(-12.0f).setY(-1.0f));
                MDAbsHotspot hotspot = new MDSimpleHotspot(builder);
                hotspot.rotateToCamera();
                plugins.add(hotspot);
                getVRLibrary().addPlugin(hotspot);
            }
        });


        final TextView hotspotText = (TextView) findViewById(R.id.hotspot_text);
        final TextView directorBriefText = (TextView) findViewById(R.id.director_brief_text);
        final TextView poni1 = (TextView) findViewById(R.id.hotspot_point1);
        final TextView poni2 = (TextView) findViewById(R.id.hotspot_point2);

        getVRLibrary().setEyePickChangedListener(new MDVRLibrary.IEyePickListener() {
            @Override
            public void onHotspotHit(IMDHotspot hotspot, long hitTimestamp) {
                if (hotspot != null) {
                    if (System.currentTimeMillis() - hitTimestamp > 2000) {
                        if ("A".equals(hotspot.getTag())) {
                            Log.e("vr", "选中——view1");
                            ExVideoActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_A));
//                            MD360PlayerActivity.startVideo(MD360PlayerActivity.this,Uri.parse("http://cache.utovr.com/201508270528174780.m3u8"));
                        } else if ("B".equals(hotspot.getTag())) {
                            Log.e("vr", "选中——view2");
                            ExVideoActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_WEB));
                        } else if ("C".equals(hotspot.getTag())) {
                            ExVideoActivity.startVideo(ExMenuActivity.this, Uri.parse(Config.VIDEO_C));
                        } else if ("D".equals(hotspot.getTag())) {
//                            text.setTextColor(getResources().getColor(R.color.colorPrimary));
                        }
                    } else {
                        if (hotspot.getTag()==null){
                            return;
                        }
                        switch (hotspot.getTag()) {
                            case "A":

                                break;
                            case "B":
                                getVRLibrary().findViewByTag("B");
//                                setView(R.drawable.dome_pic);
//                                addTwo(R.drawable.dome_pic);
//                                isok=true;
                                break;
                        }
                        Log.e("vr", "看中——view " + hotspot.getTag());
                    }
//                    view.setBackgroundResource(R.drawable.img_page);
                } else {
//                    if (!isok) {
//                        setView(R.drawable.img_page);
//                        addTwo(R.drawable.img_page);
//                        Log.e("test", "33333");
//                    }
                    Log.e("vr", "nothing2");
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

    private boolean isok = false;

    public void addOne() {
//        View view = new HoverView(this);
//        view.setBackgroundColor(0x55FFCC11);
        View view = new ImageView(this);
        view.setBackgroundResource(R.drawable.img_page);
        MDViewBuilder builder = MDViewBuilder.create()
                .provider(view, 300/*view width*/, 200/*view height*/)
                .size(3, 2)
                .position(onePosition)
                .title("md view")
                .tag("A");

        Log.e("getD", onePosition.toString() + "----");
        MDAbsView mdView = new MDView(builder);
        mdView.rotateToCamera();
        plugins.add(mdView);
        getVRLibrary().addPlugin(mdView);
    }


    MDViewBuilder buildertwo;
    MDAbsView mdView;
    View view;

    public void setView(int resid) {
        view = new ImageView(this);
        view.setBackgroundResource(resid);
    }

    public void addTwo(int resid) {
//        setView(R.drawable.img_page);
        view = new ImageView(this);
        view.setBackgroundResource(resid);
            buildertwo = MDViewBuilder.create();
            buildertwo.provider(view, 300, 200);
            buildertwo.size(3, 2);
            buildertwo.position(twoPosition);
            buildertwo.title("md view");
            buildertwo.tag("B");
            mdView = new MDView(buildertwo);
            mdView.rotateToCamera();
            plugins.add(mdView);
            getVRLibrary().addPlugin(mdView);
    }

    public void addTwo2(int resid) {
//        setView(R.drawable.img_page);
        view = new ImageView(this);
        view.setBackgroundResource(resid);

        if (buildertwo == null) {
            buildertwo = MDViewBuilder.create();
            buildertwo.provider(view, 300, 200);
            buildertwo.size(3, 2);
            buildertwo.position(twoPosition);
            buildertwo.title("md view");
            buildertwo.tag("B");
            isok = true;
            mdView = new MDView(buildertwo);
            mdView.rotateToCamera();
            plugins.add(mdView);
            getVRLibrary().addPlugin(mdView);
        } else {
            if (isok) {
//                buildertwo.provider(view, 500, 200);
                buildertwo.size(7, 3);
                Log.e("test", "11111");
                getVRLibrary().removePlugin(mdView);
                mdView = new MDView(buildertwo);
                mdView.rotateToCamera();
                plugins.add(mdView);
                getVRLibrary().addPlugin(mdView);
                isok=false;
            } else {
//                buildertwo.provider(view, 300, 200);
                buildertwo.size(3, 2);
                Log.e("test", "2222");
                getVRLibrary().removePlugin(mdView);
                mdView = new MDView(buildertwo);
                mdView.rotateToCamera();
                plugins.add(mdView);
                getVRLibrary().addPlugin(mdView);
            }
        }
    }

    public void addThree() {
//        View view = new HoverView(this);
//        view.setBackgroundColor(0x55FFCC11);

        View view = new ImageView(this);
        view.setBackgroundResource(R.drawable.img_page);
        MDViewBuilder builder = MDViewBuilder.create()
                .provider(view, 300/*view width*/, 200/*view height*/)
                .size(3, 2)
                .position(threePosition)
                .title("md view")
                .tag("C");
        MDAbsView mdView = new MDView(builder);
        mdView.rotateToCamera();
        plugins.add(mdView);
        getVRLibrary().addPlugin(mdView);
    }

    TextView text;

    public void addFour() {
        text = new TextView(this);
        text.setBackgroundResource(R.drawable.dome_pic);
//        text.setText("菜单");
        text.setTextColor(getResources().getColor(R.color.colorAccent));
        MDViewBuilder builder = MDViewBuilder.create()
                .provider(text, 300/*view width*/, 200/*view height*/)
                .size(3, 2)
                .position(fourPosition)
                .title("md view")
                .tag("D");
        MDAbsView mdView = new MDView(builder);
        mdView.rotateToCamera();
        plugins.add(mdView);
        getVRLibrary().addPlugin(mdView);
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