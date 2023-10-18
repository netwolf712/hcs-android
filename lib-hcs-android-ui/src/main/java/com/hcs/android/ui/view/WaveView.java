package com.hcs.android.ui.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.app.ActivityCompat;

import com.hcs.android.common.util.PermissionCheckUtil;
import com.hcs.android.ui.R;
import com.hcs.android.ui.util.UIThreadUtil;
import com.hcs.android.ui.wavelibrary.draw.WaveCanvas;
import com.hcs.android.ui.wavelibrary.utils.SamplePlayer;
import com.hcs.android.ui.wavelibrary.utils.SoundFile;
import com.hcs.android.ui.wavelibrary.view.WaveSurfaceView;
import com.hcs.android.ui.wavelibrary.view.WaveformView;

import java.io.File;

/**
 * 声音波形图
 */
public class WaveView extends RelativeLayout {

    /**
     * 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
     */
    private static final int FREQUENCY = 44100;
    /**
     * 设置单声道声道
     */
    private static final int CHANNELCONGIFIGURATION = AudioFormat.CHANNEL_IN_MONO;
    /**
     * 音频数据格式：每个样本16位
     */
    private static final int AUDIOENCODING = AudioFormat.ENCODING_PCM_16BIT;
    /**
     * 音频获取源
     */
    public final static int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;

    private final WaveSurfaceView mWaveSurface;
    private final WaveformView mWaveForm;
    /**
     * 录音最小buffer大小
     */
    private int mRecBufSize;
    private AudioRecord mAudioRecord;
    private WaveCanvas mWaveCanvas;
    private Activity mActivity;

    /**
     * 播放模式
     */
    public final static boolean WAVE_MODE_PLAY = true;

    /**
     * 录制模式
     */
    public final static boolean WAVE_MODE_RECORD = false;
    /**
     * 播放模式还是录制模式
     * true为播放模式
     * false录制模式
     */
    private boolean mPlayMode = false;
    /**
     * 样本播放器
     */
    private SamplePlayer mPlayer;

    /**
     * 放音文件加载进度监听器
     */
    private ILoadListener mLoadListener;

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_wave,this);
        mWaveSurface = findViewById(R.id.waveSurface);
        mWaveForm = findViewById(R.id.waveForm);

        mWaveSurface.setLine_off(42);
        //解决surfaceView黑色闪动效果
        mWaveSurface.setZOrderOnTop(true);
        mWaveSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        mWaveForm.setLine_offset(42);
    }

    /**
     * 开始录音
     * @param filePath 存放录音文件的绝对路径
     * @param activity 调用此view的activity
     */
    public void startRecord(String filePath, Activity activity){
        mActivity = activity;
        if(mWaveCanvas != null){
            return;
        }
        mPlayMode = WAVE_MODE_RECORD;

        mRecBufSize = AudioRecord.getMinBufferSize(FREQUENCY,
                CHANNELCONGIFIGURATION, AUDIOENCODING);// 录音组件
        PermissionCheckUtil.checkAndRequestRecordAudioPermissions(activity,accept->{
            mAudioRecord = new AudioRecord(AUDIO_SOURCE,// 指定音频来源，这里为麦克风
                    FREQUENCY, // 16000HZ采样频率
                    CHANNELCONGIFIGURATION,// 录制通道
                    AUDIO_SOURCE,// 录制编码格式
                    mRecBufSize);// 录制缓冲区大小 //先修改

            mWaveCanvas = new WaveCanvas();
            mWaveCanvas.baseLine = mWaveSurface.getHeight() / 2;
            mWaveCanvas.start(mAudioRecord, mRecBufSize, mWaveSurface, filePath, new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    return true;
                }
            });
        });
    }

    /**
     * 暂停
     */
    public void pause(){
        if(mPlayMode == WAVE_MODE_PLAY){
            if(mPlayer != null && mPlayer.isPlaying()){
                mPlayer.pause();
            }
        }else{
            if(mWaveCanvas != null){
                mWaveCanvas.pause();
            }
        }
    }
    /**
     * 继续
     */
    public void resume(){
        if(mPlayMode == WAVE_MODE_PLAY){
            if(mPlayer != null && mPlayer.isPaused()){
                mPlayer.start();
            }
        }else{
            if(mWaveCanvas != null){
                mWaveCanvas.resume();
            }
        }
    }

    public boolean isPaused(){
        if(mPlayMode == WAVE_MODE_PLAY){
            if(mPlayer != null){
                return mPlayer.isPaused();
            }else{
                return false;
            }
        }else{
            if(mWaveCanvas != null){
                return mWaveCanvas.isPaused();
            }else{
                return false;
            }
        }
    }
    /**
     * 是否正在录制
     */
    public boolean isRecording(){
        if(mPlayMode != WAVE_MODE_RECORD){
            return false;
        }
        if(mWaveCanvas == null){
            return false;
        }
        return mWaveCanvas.mIsRecording;
    }

    /**
     * 停止录音
     */
    public void stopRecord(){
        if(mWaveCanvas == null){
            return;
        }
        mWaveCanvas.stop();
        mWaveCanvas = null;
    }

    /**
     * 是否正在播放
     */
    public boolean isPlaying(){
        if(mPlayMode != WAVE_MODE_PLAY){
            return false;
        }
        if(mPlayer == null){
            return false;
        }
        return mPlayer.isPlaying();
    }



    /**
     * 加载要播放的声音文件
     * @param filePath 声音文件
     * @param activity 调用此view的activity
     */
    public void loadAudioFile(String filePath,Activity activity){
        mActivity = activity;
        new Thread(()-> {
            SoundFile soundFile;
            try {
                File file = new File(filePath);
                soundFile = SoundFile.create(file.getAbsolutePath(), null);
                if (soundFile == null) {
                    return;
                }
                mPlayer = new SamplePlayer(soundFile);
            } catch (final Exception e) {
                e.printStackTrace();
                return;
            }
            UIThreadUtil.runOnUiThread(()->{
                finishOpeningSoundFile(soundFile);
                mWaveSurface.setVisibility(View.INVISIBLE);
                mWaveForm.setVisibility(View.VISIBLE);
            });

        }).start();
    }

    /**
     * 文件加载完成
     */
    private void finishOpeningSoundFile(SoundFile soundFile) {
        mWaveForm.setSoundFile(soundFile);
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mWaveForm.recomputeHeights(metrics.density);
        if(mLoadListener != null){
            mLoadListener.onLoadComplete();
        }
    }

    private int mPlayEndMsec;

    /**
     * 消息句柄
     */
    private final int UPDATE_WAV = 100;
    /**
     * 播放音频
     * @param startPosition 开始播放的时间
     * */
    public synchronized void playFile(int startPosition) {
        if (mPlayer == null)
            return;
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            updateTime.removeMessages(UPDATE_WAV);
        }
        int mPlayStartMsec = mWaveForm.pixelsToMillisecs(startPosition);
        mPlayEndMsec = mWaveForm.pixelsToMillisecsTotal();
        mPlayer.setOnCompletionListener(new SamplePlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                mWaveForm.setPlayback(-1);
                updateDisplay();
                updateTime.removeMessages(UPDATE_WAV);
            }
        });
        mPlayer.seekTo(mPlayStartMsec);
        mPlayer.start();
        Message msg = new Message();
        msg.what = UPDATE_WAV;
        updateTime.sendMessage(msg);
    }

    /**
     * 停止播放
     */
    public void stopPlay(){
        if(mPlayer != null){
            mPlayer.stop();
        }
    }

    Handler updateTime = new Handler() {
        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            updateDisplay();
            updateTime.sendMessageDelayed(new Message(), 10);
        };
    };

    /**更新upd
     * ateview 中的播放进度*/
    private void updateDisplay() {
        int now = mPlayer.getCurrentPosition();// nullpointer
        int frames = mWaveForm.millisecsToPixels(now);
        mWaveForm.setPlayback(frames);//通过这个更新当前播放的位置
        if (now >= mPlayEndMsec ) {
            mWaveForm.setPlayFinish(1);
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.pause();
                updateTime.removeMessages(UPDATE_WAV);
            }
        }else{
            mWaveForm.setPlayFinish(0);
        }
        mWaveForm.invalidate();//刷新整个视图
    }

    public void setLoadListener(ILoadListener loadListener){
        mLoadListener = loadListener;
    }

    public interface ILoadListener{
        /**
         * 放音文件加载完成
         */
        void onLoadComplete();
    }
}