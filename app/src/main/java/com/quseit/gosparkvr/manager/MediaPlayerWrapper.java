package com.quseit.gosparkvr.manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.Surface;

import com.quseit.gosparkvr.App;
import com.quseit.gosparkvr.util.VideoStateIO;

import java.io.IOException;
import java.io.InputStream;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;

/**
 * Created by hzqiujiadi on 16/4/5.
 * hzqiujiadi ashqalcn@gmail.com
 *
 * http://developer.android.com/intl/zh-cn/reference/android/media/MediaPlayer.html
 * status
 */
public class MediaPlayerWrapper implements IMediaPlayer.OnPreparedListener {
    private VideoStateIO videoStateIO;
    protected IMediaPlayer mPlayer;
    private IjkMediaPlayer.OnPreparedListener mPreparedListener;
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_PREPARING = 1;
    public static final int STATUS_PREPARED = 2;
    public static final int STATUS_STARTED = 3;
    public static final int STATUS_PAUSED = 4;
    public static final int STATUS_STOPPED = 5;
    public int mStatus = STATUS_IDLE;

    public void init(final VideoStateIO videoStateIO){
        this.videoStateIO = videoStateIO;
        mStatus = STATUS_IDLE;
        mPlayer = new IjkMediaPlayer();
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer mp, int what, int extra) {

//                App.e("setOnInfoListener_check"+mp.getMediaInfo().mMeta);
//                Log.e("setOnInfoListener","get:"+mp.getMediaInfo().mMeta.mFormat);
//                Log.e("setOnInfoListener","get:"+mp.getMediaInfo().mMeta.mAudioStream.mTbrNum);
//                Log.e("setOnInfoListener","get:"+mp.getMediaInfo().mMeta.mVideoStream.getFpsInline());
//                Log.e("setOnInfoListener","get:"+mp.getMediaInfo().mMeta.mVideoStream.getBitrateInline());
                return false;
            }
        });

        //当为网络视频时，会有监听，若是本地则为0
        mPlayer.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
//                App.e(""+i);
            }
        });
        mPlayer.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                App.e("seek",iMediaPlayer.getVideoSarDen()+"");
            }
        });
        //播放完毕后
        mPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                videoStateIO.endVideo();
            }
        });

        enableHardwareDecoding();
    }

    private void enableHardwareDecoding(){
        if (mPlayer instanceof IjkMediaPlayer){
            IjkMediaPlayer player = (IjkMediaPlayer) mPlayer;
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 60);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 0);
            player.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        }
    }

    public void setSurface(Surface surface){
        if (getPlayer() != null){
            getPlayer().setSurface(surface);
        }
    }

    public void openRemoteFile(String url){
        try {
            mPlayer.setDataSource(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openAssetFile(Context context, String assetPath) {
        try {
            AssetManager am = context.getResources().getAssets();
            final InputStream is = am.open(assetPath);
            mPlayer.setDataSource(new IMediaDataSource() {
                @Override
                public int readAt(long position, byte[] buffer, int offset, int size) throws IOException {
                    return is.read(buffer, offset, size);
                }

                @Override
                public long getSize() throws IOException {
                    return is.available();
                }

                @Override
                public void close() throws IOException {
                    is.close();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public IMediaPlayer getPlayer() {
        return mPlayer;
    }

    public void prepare() {
        if (mPlayer == null) return;
        if (mStatus == STATUS_IDLE || mStatus == STATUS_STOPPED){
            mPlayer.prepareAsync();
            mStatus = STATUS_PREPARING;
        }
    }

    public void stop(){
        if (mPlayer == null) return;
        if (mStatus == STATUS_STARTED || mStatus ==  STATUS_PAUSED){
            mPlayer.stop();
            mStatus = STATUS_STOPPED;
        }
    }

    public void pause(){
        if (mPlayer == null) return;
        if (mPlayer.isPlaying() && mStatus == STATUS_STARTED) {
            mPlayer.pause();
            mStatus = STATUS_PAUSED;
        }
    }

    public void start(){
        if (mPlayer == null) return;
        if (mStatus == STATUS_PREPARED || mStatus == STATUS_PAUSED){
            mPlayer.start();
            mStatus = STATUS_STARTED;
        }

    }

    public void setPreparedListener(IMediaPlayer.OnPreparedListener mPreparedListener) {
        this.mPreparedListener = mPreparedListener;
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        mStatus = STATUS_PREPARED;
        start();
        if (mPreparedListener != null) mPreparedListener.onPrepared(mp);
    }

    public void resume() {
        start();
    }

    public void destroy() {
        stop();
        if (mPlayer != null) {
            mPlayer.setSurface(null);
            mPlayer.release();
        }
        mPlayer = null;
    }
}
