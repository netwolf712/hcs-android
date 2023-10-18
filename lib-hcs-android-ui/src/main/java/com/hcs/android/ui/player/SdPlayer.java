package com.hcs.android.ui.player;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.view.Surface;


import androidx.annotation.NonNull;

import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkLibLoader;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.MediaInfo;

/**
 * 对ijkplayer的简易封装
 */
public class SdPlayer implements ISdPlayer, IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener, IjkMediaPlayer.OnSeekCompleteListener,IjkMediaPlayer.OnBufferingUpdateListener {

    /**
     * 指令-准备
     */
    private final static int COMMAND_PREPARE = 1;
    /**
     * 指令-设置Surface
     */
    private final static int COMMAND_SET_SURFACE = 2;

    /**
     * 指令-释放Surface
     */
    private final static int COMMAND_RELEASE_SURFACE = 3;

    /**
     * 指令-释放播放器
     */
    private final static int COMMAND_RELEASE_PLAYER = 4;
    /**
     * 消息处理
     */
    private final PlayerHandler mPlayerHandler;
    private static IjkLibLoader mIjkLibLoader;

    private IjkMediaPlayer mIjkMediaPlayer;

    private List<IjkOptionItem> mOptionItemList;

    private Surface mSurface;

    private final Context mContext;

    /**
     * 文件描述符，与mPlayList二者取其一
     */
    private FileDescriptor mFd;
    private Map<String,String> mHeadData;

    /**
     * 已经播放的百分比
     */
    private int mPlayedPercent = 0;
    /**
     * 播放器状态改变监听器
     */
    private ISdPlayerListener.OnPlayerStatusChange mOnPlayerStatusChange;

    /**
     * 当前状态
     */
    private PlayStatusEnum mCurrentStatus;

    /**
     * 当前需要播放的文件
     */
    private String mPlayFile;

    private final Object mSynObj = new Object();
    public SdPlayer(Context context,List<IjkOptionItem> optionList) {
        mContext = context;
        mPlayerHandler = new PlayerHandler(Looper.getMainLooper());
        mOptionItemList = optionList;
        mIjkMediaPlayer = createIjkMediaPlayer();

        mCurrentStatus = PlayStatusEnum.STANDBY;
    }

    public static void setmIjkLibLoader(IjkLibLoader mIjkLibLoader) {
        SdPlayer.mIjkLibLoader = mIjkLibLoader;
    }

    /**
     * 创建一个新的媒体播放器
     * @return 媒体播放器
     */
    @NonNull
    private IjkMediaPlayer createIjkMediaPlayer(){
        IjkMediaPlayer ijkMediaPlayer = (mIjkLibLoader == null) ? new IjkMediaPlayer() : new IjkMediaPlayer(mIjkLibLoader);
        ijkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        ijkMediaPlayer.setOnCompletionListener(this);
        ijkMediaPlayer.setOnBufferingUpdateListener(this);
        ijkMediaPlayer.setOnSeekCompleteListener(this);
        initIJKOption(ijkMediaPlayer, mOptionItemList);
        IjkMediaPlayer.native_setLogLevel(1);
        return ijkMediaPlayer;
    }

    private void prepareVideo(){
        synchronized (mSynObj) {
            String url;
            if (!StringUtil.isEmpty(mPlayFile)) {
                url = mPlayFile;

                try {
                    if (!TextUtils.isEmpty(url)) {
                        Uri uri = Uri.parse(url);
                        if (uri.getScheme() != null) {
                            if (uri.getScheme().equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                                RawDataSourceProvider rawDataSourceProvider = RawDataSourceProvider.create(mContext, uri);
                                mIjkMediaPlayer.setDataSource(rawDataSourceProvider);
                            } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                                ParcelFileDescriptor descriptor;
                                try {
                                    descriptor = mContext.getContentResolver().openFileDescriptor(uri, "r");
                                    FileDescriptor fileDescriptor = descriptor.getFileDescriptor();
                                    mIjkMediaPlayer.setDataSource(fileDescriptor);
                                } catch (Exception e) {
                                    KLog.e("prepareVideo failed ", e);
                                }
                            } else {
                                setDataSource(mContext, url, mHeadData);
                            }
                        } else {
                            setDataSource(mContext, url, mHeadData);
                        }
                    } else {
                        setDataSource(mContext, url, mHeadData);
                    }
                } catch (Exception e) {
                    KLog.e("prepare video " + url + " failed", e);
                }
            } else {
                if (mFd != null) {
                    try {
                        mIjkMediaPlayer.setDataSource(mFd);
                    } catch (Exception e) {
                        KLog.e("prepare video with fd failed", e);
                    }

                }
            }

            mIjkMediaPlayer.setScreenOnWhilePlaying(true);
            mIjkMediaPlayer.prepareAsync();
        }
    }
    private void setDataSource(Context context,String url,Map<String,String> headData){
        try{
            mIjkMediaPlayer.setDataSource(context,Uri.parse(url), headData);
        }catch (Exception e){
            KLog.e("set video path " + url + " failed",e);
        }
    }


    private void initIJKOption(IjkMediaPlayer ijkMediaPlayer, List<IjkOptionItem> optionList) {
        if (optionList != null && optionList.size() > 0) {
            for (IjkOptionItem option : optionList) {
                setIjkOption(ijkMediaPlayer,option);
            }
        }
    }

    private void setIjkOption(IjkMediaPlayer ijkMediaPlayer, IjkOptionItem option){
        if(ijkMediaPlayer != null && !StringUtil.isEmpty(option.getValue())){
            if (option.getValueType() == Long.class) {
                ijkMediaPlayer.setOption(option.getCategory(),
                        option.getName(), option.getValueAsLong());
            } else {
                ijkMediaPlayer.setOption(option.getCategory(),
                        option.getName(), option.getValue());
            }
        }
    }

    /**
     * 准备且开始
     */
    private void prepareAndStart(){
        if(mCurrentStatus == PlayStatusEnum.STANDBY) {
            mCurrentStatus = PlayStatusEnum.PREPARING;
            mIjkMediaPlayer.setOnPreparedListener(this);
            prepareVideo();
        }
    }

    private void handleSetSurface(Message msg){
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                if (msg.obj == null) {
                    mSurface = null;
                    mIjkMediaPlayer.setSurface(null);
                } else {
                    mSurface = (Surface) msg.obj;
                    if (mSurface.isValid()) {
                        mIjkMediaPlayer.setSurface(mSurface);
                    }
                }
            }
        }
    }

    /**
     * 添加视频铺设对象
     * @param surface 接收视频视频的对象
     */
    public void setSurface(Surface surface) {
        Message msg = new Message();
        msg.what = COMMAND_SET_SURFACE;
        msg.obj = surface;
        sendMessage(msg);
    }
    /**
     * 开始播放
     * 暂停、停止状态下还是需要调用它的
     */
    public void start() {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                if (mCurrentStatus == PlayStatusEnum.PREPARED || mCurrentStatus == PlayStatusEnum.PAUSED) {
                    mIjkMediaPlayer.start();
                    mCurrentStatus = PlayStatusEnum.PLAYING;
                }
            }
        }
    }


    public void setSpeed(float speed, boolean soundTouch) {
        if (speed > 0) {
            try {
                synchronized (mSynObj) {
                    if (mIjkMediaPlayer != null) {
                        mIjkMediaPlayer.setSpeed(speed);
                    }
                }
            } catch (Exception e) {
                KLog.e("setSpeed failed ",e);
            }
            if (soundTouch) {
                IjkOptionItem option = new IjkOptionItem(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", "1",Long.class);
                List<IjkOptionItem> list = getOptionList();
                if (list != null) {
                    list.add(option);
                } else {
                    list = new ArrayList<>();
                    list.add(option);
                }
                setOptionList(list);
            }

        }
    }

    /**
     * 获取当前的播放速度
     * @return 播放速度
     */
    public float getSpeed(){
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                return mIjkMediaPlayer.getSpeed(0f);
            }
        }
        return 0f;
    }
    public void setNeedMute(boolean needMute) {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                if (needMute) {
                    mIjkMediaPlayer.setVolume(0, 0);
                } else {
                    mIjkMediaPlayer.setVolume(1, 1);
                }
            }
        }
    }

    public void setVolume(float left, float right) {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                mIjkMediaPlayer.setVolume(left, right);
            }
        }
    }


    private void handleReleaseSurface(){
        synchronized (mSynObj) {
            if(mIjkMediaPlayer != null){
                mIjkMediaPlayer.setSurface(null);
            }
            if (mSurface != null) {
                mSurface.release();
                mSurface = null;
            }
        }
    }
    public void releaseSurface() {
        Message message = new Message();
        message.what = COMMAND_RELEASE_SURFACE;
        message.obj = null;
        sendMessage(message);
    }


    private void handleRelease(){
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                mIjkMediaPlayer.setSurface(null);
                mIjkMediaPlayer.release();
                mIjkMediaPlayer = null;
                mCurrentStatus = PlayStatusEnum.STANDBY;
            }
        }
    }

    /**
     * 释放视频源
     */
    public void release() {
        Message message = new Message();
        message.what = COMMAND_RELEASE_PLAYER;
        message.obj = null;
        sendMessage(message);
    }


    public int getBufferedPercentage() {
        return mPlayedPercent;
    }


    public long getNetSpeed() {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                return mIjkMediaPlayer.getTcpSpeed();
            }
        }
        return 0;
    }


    public void setSpeedPlaying(float speed, boolean soundTouch) {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                mIjkMediaPlayer.setSpeed(speed);
                mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", (soundTouch) ? 1 : 0);
            }
        }
    }


    public void stop() {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                mIjkMediaPlayer.stop();
                mCurrentStatus = PlayStatusEnum.STOPPED;
            }
        }
    }


    /**
     * 暂停
     */
    public void pause() {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                mIjkMediaPlayer.pause();
                mCurrentStatus = PlayStatusEnum.PAUSED;
            }
        }
    }


    /**
     * 获取视频的实际宽
     * @return 宽度
     */
    public int getVideoWidth() {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                return mIjkMediaPlayer.getVideoWidth();
            }
        }
        return 0;
    }

    /**
     * 获取视频的实际高
     * @return 高度
     */
    public int getVideoHeight() {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                return mIjkMediaPlayer.getVideoHeight();
            }
        }
        return 0;
    }


    public boolean isPlaying() {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                return mIjkMediaPlayer.isPlaying();
            }
        }
        return false;
    }


    public void seekTo(long time) {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                mIjkMediaPlayer.seekTo(time);
            }
        }
    }


    public long getCurrentPosition() {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                return mIjkMediaPlayer.getCurrentPosition();
            }
        }
        return 0;
    }


    public long getDuration() {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                return mIjkMediaPlayer.getDuration();
            }
        }
        return 0;
    }


    public int getVideoSarNum() {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                return mIjkMediaPlayer.getVideoSarNum();
            }
        }
        return 1;
    }


    public int getVideoSarDen() {
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                return mIjkMediaPlayer.getVideoSarDen();
            }
        }
        return 1;
    }



    public boolean isSurfaceSupportLockCanvas() {
        return true;
    }

    public List<IjkOptionItem> getOptionList() {
        return mOptionItemList;
    }

    public void setOptionList(List<IjkOptionItem> mOptionItemList) {
        this.mOptionItemList = mOptionItemList;
    }

    /**
     * 获取默认配置
     */
    public static List<IjkOptionItem> getDefaultOption(){
        List<IjkOptionItem> ijkOptionItemList = new ArrayList<>();
        for(PlayerOptionEnum playerOptionEnum : PlayerOptionEnum.values()){
            IjkOptionItem ijkOptionItem = new IjkOptionItem(playerOptionEnum.getCategory(),playerOptionEnum.getName(),playerOptionEnum.getDefaultValue(),playerOptionEnum.getValueType());
            ijkOptionItemList.add(ijkOptionItem);
        }
        return ijkOptionItemList;
    }

    /**
     * 设置单个属性
     * @param playerOptionEnum 属性枚举
     * @param value 属性值（全部强制转换成字符串类型）
     */
    public void setOption(PlayerOptionEnum playerOptionEnum,String value){
        IjkOptionItem ijkOptionItem = null;
        for(IjkOptionItem tmp : mOptionItemList){
            if(tmp.getCategory().intValue() == playerOptionEnum.getCategory().intValue() && tmp.getName().equals(playerOptionEnum.getName())){
                ijkOptionItem = tmp;
                break;
            }
        }
        if(ijkOptionItem == null){
            ijkOptionItem = new IjkOptionItem(playerOptionEnum.getCategory(),playerOptionEnum.getName(),value,playerOptionEnum.getValueType());
            mOptionItemList.add(ijkOptionItem);
        }else{
            ijkOptionItem.setValue(value);
        }
        setIjkOption(mIjkMediaPlayer,ijkOptionItem);
    }

    /**
     * 获取属性值
     * @param playerOptionEnum 属性枚举
     * @return 属性值
     */
    public String getOption(PlayerOptionEnum playerOptionEnum){
        if(!StringUtil.isEmpty(mOptionItemList)){
            for(IjkOptionItem ijkOptionItem : mOptionItemList){
                if(ijkOptionItem.getCategory().intValue() == playerOptionEnum.getCategory()
                        && ijkOptionItem.getName().equals(playerOptionEnum.getName())){
                    return ijkOptionItem.getValue();
                }
            }
        }
        return null;
    }

    public void startPrepare(){
        Message msg = new Message();
        msg.what = COMMAND_PREPARE;
        msg.obj = null;
        sendMessage(msg);
    }

    /**
     * 通过文件描述符播放
     * @param fd 文件描述符，不知道行不行
     * 又加回来了
     */
    public void startPlay(FileDescriptor fd){
        mFd = fd;
        mPlayFile = null;
        mHeadData = null;
        startPrepare();
    }

    /**
     * 开始播放
     * @param filePath 文件路径
     */
    public void startPlay(String filePath){
        startPlay(filePath,null);
    }

    public void startPlay(String filePath,Map<String,String> headData){
        mFd = null;
        mPlayFile = filePath;
        mHeadData = headData;
        startPrepare();
    }

    private void sendMessage(Message msg){
        if(mPlayerHandler != null){
            mPlayerHandler.sendMessage(msg);
        }
    }

    private class PlayerHandler extends Handler{
        PlayerHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COMMAND_PREPARE:
                    prepareAndStart();
                    break;
                case COMMAND_SET_SURFACE:
                    handleSetSurface(msg);
                    break;
                case COMMAND_RELEASE_SURFACE:
                    handleReleaseSurface();
                    break;
                case COMMAND_RELEASE_PLAYER:
                    handleRelease();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        if("0".equals(getOption(PlayerOptionEnum.START_ON_PREPARED))) {
            mCurrentStatus = PlayStatusEnum.PREPARED;
            if(mOnPlayerStatusChange != null){
                mOnPlayerStatusChange.onPlayerReady(this);
            }
        }else{
            mCurrentStatus = PlayStatusEnum.PLAYING;
            if(mOnPlayerStatusChange != null){
                mOnPlayerStatusChange.onPlayerStartPlay(this);
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
            if (mIjkMediaPlayer != null) {
                mIjkMediaPlayer.reset();
                mCurrentStatus = PlayStatusEnum.STANDBY;
            }
        }
    }

    /**
     * 重新开始（只针对文件播放）
     * @param filePath 文件路径
     */
    public void restart(String filePath){
        //需要先重置，否则此播放器就费了无法继续播放
        reset();
        mPlayFile = filePath;
        startPrepare();
        synchronized (mSynObj) {
            //需要重新设置surface，否则图像不会更新
            if (mIjkMediaPlayer != null && mSurface.isValid()) {
                mIjkMediaPlayer.setSurface(mSurface);
            }
        }

    }

    /**
     * 强制重新开始（释放播放器重新开始，最霸道的方案了）
     * @param filePath 文件路径
     */
    public void restartForce(String filePath){
        synchronized (mSynObj) {
            //需要先重置，否则此播放器就费了无法继续播放
            mIjkMediaPlayer = createIjkMediaPlayer();
            mIjkMediaPlayer.setSurface(mSurface);
            mPlayFile = filePath;
            startPrepare();
        }
    }
    @Override
    public void onCompletion(IMediaPlayer mp){
        if(mOnPlayerStatusChange != null){
            mOnPlayerStatusChange.onPlayerCompletion(this);
        }

    }
    public void setVideoSizeChanged(IMediaPlayer.OnVideoSizeChangedListener listener){
        if(mIjkMediaPlayer != null){
            mIjkMediaPlayer.setOnVideoSizeChangedListener(listener);
        }
    }

    /**
     * 获取播放器当前状态
     * @return 自定义的简易状态
     */
    public PlayStatusEnum getCurrentStatus(){
        synchronized (mSynObj) {
            return mCurrentStatus;
        }
    }

    /**
     * 定位完成
     */
    @Override
    public void onSeekComplete(IMediaPlayer mp){
        if(mOnPlayerStatusChange != null){
            mOnPlayerStatusChange.onSeekCompleted(this);
        }
    }

    public long getBitRate(){
        if(mIjkMediaPlayer != null){
            return mIjkMediaPlayer.getBitRate();
        }
        return 0;
    }
    /**
     * 视频已经播放的百分比
     * @param mp 播放器
     * @param percent 百分比
     */
    @Override
    public void onBufferingUpdate(IMediaPlayer mp, int percent){
        mPlayedPercent = percent;
    }

    /**
     * 添加播放器状态监听器
     */
    public void setOnPlayerStatusChange(ISdPlayerListener.OnPlayerStatusChange onPlayerStatusChange){
        mOnPlayerStatusChange = onPlayerStatusChange;
    }

    public int getAudioSessionId(){
        if(mIjkMediaPlayer != null){
            return mIjkMediaPlayer.getAudioSessionId();
        }
        return 0;
    }

    /**
     * 获取每秒的解码帧数
     */
    public float getVideoDecodeFramesPerSecond(){
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                return mIjkMediaPlayer.getVideoDecodeFramesPerSecond();
            }
        }
        return 0f;
    }

    /**
     * 获取每秒的播放帧数
     * @return fps
     */
    public float getVideoOutputFramesPerSecond(){
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                return mIjkMediaPlayer.getVideoOutputFramesPerSecond();
            }
        }
        return 0f;
    }

    /**
     * 获取音频采样率
     * @return 采样率
     */
    public int getAudioSampleRate(){
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                MediaInfo mediaInfo = mIjkMediaPlayer.getMediaInfo();
                if (mediaInfo.mMeta != null && mediaInfo.mMeta.mAudioStream != null) {
                    return mediaInfo.mMeta.mAudioStream.mSampleRate;
                }
            }
        }
        return 0;
    }

    /**
     * 获取音频编解码名称
     * @return 编解码简称
     */
    public String getAudioCodecName(){
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                MediaInfo mediaInfo = mIjkMediaPlayer.getMediaInfo();
                if (mediaInfo.mMeta != null && mediaInfo.mMeta.mAudioStream != null) {
                    return mediaInfo.mMeta.mAudioStream.mCodecName;
                }
            }
        }
        return "";
    }

    /**
     * 获取s视频编解码名称
     * @return 编解码简称
     */
    public String getVideoCodecName(){
        synchronized (mSynObj) {
            if (mIjkMediaPlayer != null) {
                MediaInfo mediaInfo = mIjkMediaPlayer.getMediaInfo();
                if (mediaInfo.mMeta != null && mediaInfo.mMeta.mVideoStream != null) {
                    return mediaInfo.mMeta.mVideoStream.mCodecName;
                }
            }
        }
        return "";
    }

    /**
     * 播放的倍数
     * 1表示原始速度，0.5表示0.5倍速
     * @param speed 倍数
     */
    public void setSpeed(float speed){
        if(mIjkMediaPlayer != null){
            if(mIjkMediaPlayer.isPlaying()){
                //播放过程中进行设置
                setSpeedPlaying(speed,false);
            }else {
                //播放之前设置
               setSpeed(speed, false);
            }
        }
    }

    /**
     * 将视频源回退到开始并暂停
     */
    public void backToStartAndPause(){
        if(mIjkMediaPlayer != null){
            seekTo(0);
            pause();
        }
    }

//    /**
//     * 获取实时音量
//     * @return 声音响度(db)
//     */
//    public float getRealtimeVolume(){
//        synchronized (mSynObj) {
//            if (mIjkMediaPlayer != null) {
//                return mIjkMediaPlayer.getAudioPlaybackVolume();
//            }
//        }
//        return 0;
//    }
}
