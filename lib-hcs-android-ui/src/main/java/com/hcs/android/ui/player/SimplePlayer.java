package com.hcs.android.ui.player;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.Surface;
import android.view.View;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 简易播放器
 */
public class SimplePlayer implements IMediaPlayer.OnVideoSizeChangedListener
        ,ISdPlayerListener.OnPlayerStatusChange{

    private Context mActivityContext;


    /**
     * 提供给外层的播放器状态变化监听器
     */
    private ISdPlayerListener.OnPlayerStatusChange mOnPlayerStatusChange;

    /**
     * 用来接收播放器的图层
     */
    private Surface mSurface;

    /**
     * 来自EGL的上下文
     */
    private GLSurfaceView mView;
    private SimpleGLRenderer mRender;
    /**
     * 播放器
     */
    private volatile SdPlayer mSdPlayer;

    /**
     * 获取上下文 给 PlayerManager用
     */
    public Context getContext(){
        return mContext;
    }

    /**
     * 来自app的上下文
     */
    private Context mContext;

    /**
     * 列表播放的逻辑还是给子播放器吧
     * 毕竟这是跟业务相关的
     */
    private List<String> mPlayList;
    private int mPlayingIndex = 0;


    /**
     * 是否静音
     */
    private boolean mMute = false;

    /**
     * 播放速度
     */
    private float mPlaySpeed = 1f;


    private final Object mSynObj = new Object();

    private void initialise(Context context){
        mContext = context;
        mRender = new SimpleGLRenderer((surface) -> {
            mSurface = surface;
            if(mSdPlayer != null){
                mSdPlayer.setSurface(mSurface);
            }
        });

    }
    public SimplePlayer(Context context){
        initialise(context);
    }


    /**
     * 创建用于界面展示视频的视图
     * @param activityContext 界面上下文
     */
    public void createView(Context activityContext){
        synchronized (mSynObj) {
            mActivityContext = activityContext;
            mView = new GLSurfaceView(mActivityContext);
            mView.setEGLContextClientVersion(2);
            mView.setRenderer(mRender);
        }
    }

    /**
     * 创建mp4、网络视频、图片展示的播放器
     */
    @NonNull
    private SdPlayer createSdPlayer(Surface surface){
        SdPlayer sdPlayer = new SdPlayer(mContext,SdPlayer.getDefaultOption());
        sdPlayer.setVideoSizeChanged(this);
        sdPlayer.setOnPlayerStatusChange(this);
        if(surface != null){
            sdPlayer.setSurface(surface);
        }
        sdPlayer.setNeedMute(mMute);
        sdPlayer.setSpeed(mPlaySpeed);
        return sdPlayer;
    }

    /**
     * 获取播放器的视图
     * @return 播放器视图
     */
    public View getPlayerView(){
        synchronized (mSynObj) {
            return mView;
        }
    }

    /**
     * 关闭视图
     */
    public void releaseView(){
        synchronized (mSynObj) {
            if(mView != null) {
                mView.surfaceDestroyed(null);
                mView = null;
            }
        }
    }


    /**
     * 列表播放
     * @param urlList 播放列表
     */
    private void startSdPlayer(List<String> urlList){
        synchronized (mSynObj) {
            mPlayList = urlList;
            if (!StringUtil.isEmpty(mPlayList)) {
                if (mSdPlayer != null) {
                    mSdPlayer.release();
                }
                mSdPlayer = createSdPlayer(mSurface);
                String filePath = mPlayList.get(mPlayingIndex);
                if (mSdPlayer.getCurrentStatus() != PlayStatusEnum.STANDBY) {
                    mSdPlayer.restart(filePath);
                } else {
                    mSdPlayer.startPlay(filePath);
                }
            }
        }
    }

    /**
     * 单文件播放
     * @param filePath 播放文件
     */
    public void startSdPlayer(String filePath){
        List<String> fileList = new ArrayList<>();
        fileList.add(filePath);
        startSdPlayer(fileList);
    }
    /**
     * 停止播放
     * 停止后无法重新开始，慎用
     */
    public void stop(){
        synchronized (mSynObj) {
            if (mSdPlayer != null) {
                mSdPlayer.release();
                mSdPlayer = null;
            }
        }
    }
    /**
     * 重置
     * 经测试发现是保证当前player能正常播放下一条视频的利器
     * 注意：需要重新设置surface，否则就会只见其声不见其人（但是也没有丢帧打印...）
     */
    public void reset(){
        synchronized (mSynObj) {
            if (mSdPlayer != null) {
                mSdPlayer.reset();
            }
        }
    }
    /**
     * 释放播放器
     * 张源: 不能直接释放IRenderer
     * 需要释放 IEglSurface 来间接释放IRenderer
     */
    public void releasePlay() {
        synchronized (mSynObj) {
            if (mSdPlayer != null) {
                mSdPlayer.release();
            }
            releaseView();
        }
    }


    /**
     * 暂停
     */
    public void pause() {
        synchronized (mSynObj) {
            if (mSdPlayer != null) {
                mSdPlayer.pause();
            }
        }
    }

    /**
     * 获取视频宽度
     */
    public int getVideoWidth(){
        synchronized (mSynObj) {
            if (mSdPlayer != null) {
                return mSdPlayer.getVideoWidth();
            }
            return 0;
        }
    }

    /**
     * 获取视频高度
     */
    public int getVideoHeight(){
        synchronized (mSynObj) {
            if (mSdPlayer != null) {
                return mSdPlayer.getVideoHeight();
            }
            return 0;
        }
    }

    /**
     * 视频是否在播放中
     * @return true 正在播放，false 未在播放
     */
    public boolean isPlaying() {
        synchronized (mSynObj) {
            if (mSdPlayer != null) {
                PlayStatusEnum statusEnum = mSdPlayer.getCurrentStatus();
                return statusEnum == PlayStatusEnum.PLAYING;
            }
            return false;
        }
    }

    public long getDuration(){
        synchronized (mSynObj) {
            if (mSdPlayer != null) {
                return mSdPlayer.getDuration();
            }
            return 0;
        }
    }

    public long getCurrentPosition(){
        synchronized (mSynObj) {
            if (mSdPlayer != null) {
                return mSdPlayer.getCurrentPosition();
            }
            return 0;
        }
    }

    public void seekTo(long position){
        synchronized (mSynObj) {
            if (mSdPlayer != null) {
                mSdPlayer.seekTo(position);
            }
        }
    }
    /**
     * 添加播放器状态监听器
     */
    public void setOnPlayerStatusChange(ISdPlayerListener.OnPlayerStatusChange onPlayerStatusChange){
        mOnPlayerStatusChange = onPlayerStatusChange;
    }

    /**
     * 获取播放器当前状态
     * @return 自定义的简易状态
     */
    public PlayStatusEnum getCurrentStatus(){
        synchronized (mSynObj) {
            if (mSdPlayer != null) {
                return mSdPlayer.getCurrentStatus();
            }
            return PlayStatusEnum.STANDBY;
        }
    }

    /**
     * 播放器初始化完成
     */
    @Override
    public void onPlayerReady(ISdPlayer sdPlayer){
        synchronized (mSynObj) {
            if(mSdPlayer != null) {
                if (!mSdPlayer.equals(sdPlayer)) {
                    //只处理当前播放器
                    return;
                }
                //否则，直接开始播放
                sdPlayer.start();
                if (mOnPlayerStatusChange != null) {
                    mOnPlayerStatusChange.onPlayerStartPlay(sdPlayer);
                }
            }
        }
    }

    /**
     * 播放器正在开始播放
     * 在开启START_ON_PREPARED才会被触发
     * 用户主动调用start时不会被触发
     */
    @Override
    public void onPlayerStartPlay(ISdPlayer sdPlayer){
        synchronized (mSynObj) {
            if(mSdPlayer != null) {
                if (!mSdPlayer.equals(sdPlayer)) {
                    //只处理当前播放器
                    return;
                }
                if (mOnPlayerStatusChange != null) {
                    mOnPlayerStatusChange.onPlayerStartPlay(sdPlayer);
                }
            }
        }
    }

    /**
     * 播放器播放完成
     */
    @Override
    public void onPlayerCompletion(ISdPlayer sdPlayer){
        synchronized (mSynObj) {
            if(mSdPlayer != null) {
                if (!mSdPlayer.equals(sdPlayer)) {
                    //只处理当前播放器
                    return;
                }
                if (mOnPlayerStatusChange != null) {
                    //只有不需要继续播放时才将回调抛给上层
                    mOnPlayerStatusChange.onPlayerCompletion(mSdPlayer);
                }
            }
        }
    }

    /**
     * 定位到指定位置开始播放时触发
     */
    @Override
    public void onSeekCompleted(ISdPlayer sdPlayer){
        synchronized (mSynObj) {
            if(mSdPlayer != null) {
                if (!mSdPlayer.equals(sdPlayer)) {
                    //只处理当前播放器
                    return;
                }
                if (mOnPlayerStatusChange != null) {
                    mOnPlayerStatusChange.onSeekCompleted(mSdPlayer);
                }
            }
        }
    }



    /**
     * 开始播放
     * 暂停、停止状态下还是需要调用它的
     */
    public void start() {
        synchronized (mSynObj) {
            if (mSdPlayer != null) {
                mSdPlayer.start();
            }
        }
    }

    /**
     * 重新开始，附带reset功能
     */
    public void restart(){
        synchronized (mSynObj) {
            if (mSdPlayer != null) {
                if (!StringUtil.isEmpty(mPlayList)) {
                    mPlayingIndex = 0;
                    mSdPlayer.restart(mPlayList.get(mPlayingIndex));
                }
            }
        }
    }


    /**
     * 获取当前正在播放的文件路径
     * 仅对本地mp4、本地图片、本地录像等播放模式有效
     * @return 文件路径（绝对路径）
     */
    public String getPlayingFilePath(){
        synchronized (mSynObj) {
            if (!StringUtil.isEmpty(mPlayList) && mPlayingIndex >= 0 && mPlayingIndex < mPlayList.size()) {
                return mPlayList.get(mPlayingIndex);
            }
            return null;
        }
    }


    /**
     * 将当前播放器作为SdPlayer返回（如果是的话）
     * @return SdPlayer
     */
    public SdPlayer getSdPlayer(){
        return mSdPlayer;
    }




    /**
     * 静音控制
     */
    public void setMute(boolean isMute){
        mMute = isMute;
        mSdPlayer.setNeedMute(isMute);
    }
    public boolean isMute(){
        return mMute;
    }

    /**
     * 播放速度控制
     */
    public void setPlaySpeed(float speed){
        mPlaySpeed = speed;
        mSdPlayer.setSpeed(speed);
    }
    public float getPlaySpeed(){
        return mPlaySpeed;
    }



    /**
     * 指定播放下一个
     * @param playIndex 播放索引
     */
    public void playNext(int playIndex){
        if(!StringUtil.isEmpty(mPlayList)){
            if(playIndex < 0 || playIndex >= mPlayList.size()){
                playIndex = 0;
            }
            mPlayingIndex = playIndex;
        }

        startSdPlayer(mPlayList);

    }

    public int getPlayingIndex(){
        return mPlayingIndex;
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {

    }
}
