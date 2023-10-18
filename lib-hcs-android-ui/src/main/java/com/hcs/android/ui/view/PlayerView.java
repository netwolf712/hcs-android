package com.hcs.android.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hcs.android.common.util.TimeUtil;
import com.hcs.android.ui.R;
import com.hcs.android.ui.player.ISdPlayer;
import com.hcs.android.ui.player.ISdPlayerListener;
import com.hcs.android.ui.player.SimplePlayer;
import com.hcs.android.ui.util.UIThreadUtil;

/**
 * netwolf
 */
public class PlayerView extends LinearLayout {

    /**
     * 播放器
     */
    private final SimplePlayer mSimplePlayer;

    private final MediaProgressView mMediaProgressView;

    /**
     * 播放时间
     */
    private final TextView mPlayStatus;

    /**
     * 播放控制按钮
     */
    private final Button mPlayControl;

    /**
     * 暂停控制按钮
     */
    private final Button mPauseControl;

    /**
     * 文件路径
     */
    private String mFilePath;

    /**
     * 是否调用了播放
     * 对暂停、继续有效
     */
    private boolean mIsCallPlay = false;

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_player,this);

        //存放播放器的布局
        FrameLayout mPlayerLayout = findViewById(R.id.video_layout);
        mSimplePlayer = new SimplePlayer(context);
        mSimplePlayer.createView(context);
        mPlayerLayout.addView(mSimplePlayer.getPlayerView());

        mPlayStatus = findViewById(R.id.playStatus);
        mPlayControl = findViewById(R.id.playControl);
        mPauseControl = findViewById(R.id.pauseControl);
        //播放控制器-进度条
        FrameLayout mLayoutDuration = findViewById(R.id.progress_duration);
        mMediaProgressView = new MediaProgressView(context, mLayoutDuration);
        mMediaProgressView.setSimplePlayer(mSimplePlayer);
        mMediaProgressView.setRefreshListener((currentValue,changedByUser)-> UIThreadUtil.runOnUiThread(()->{
            mPlayStatus.setText(String.format("%s/%s", TimeUtil.convertMillisecondToTime(currentValue),TimeUtil.convertMillisecondToTime(mSimplePlayer.getDuration())));
            if(changedByUser){
                //如果是被用户改变，则改变当前播放位置
                mSimplePlayer.seekTo(currentValue);
            }
        }));
        mSimplePlayer.setOnPlayerStatusChange(new ISdPlayerListener.OnPlayerStatusChange() {
            /**
             * 播放器初始化完成
             */
            @Override
            public void onPlayerReady(ISdPlayer sdPlayer){
                //准备好了，可以获取对应信息
                mPlayStatus.setText(String.format("00:00/%s", TimeUtil.convertMillisecondToTime(mSimplePlayer.getDuration())));
                mMediaProgressView.startTimer();
            }

            /**
             * 播放器正在开始播放
             * 在开启START_ON_PREPARED才会被触发
             * 用户主动调用start时不会被触发
             */
            @Override
            public void onPlayerStartPlay(ISdPlayer sdPlayer){
                //准备好了，可以获取对应信息
                mPlayStatus.setText(String.format("00:00/%s", TimeUtil.convertMillisecondToTime(mSimplePlayer.getDuration())));
                mMediaProgressView.startTimer();
            }

            /**
             * 播放器播放完成
             */
            @Override
            public void onPlayerCompletion(ISdPlayer sdPlayer){
                //进入空闲状态
                stopPlay();
            }

            /**
             * 定位到指定位置开始播放时触发
             */
            @Override
            public void onSeekCompleted(ISdPlayer sdPlayer){

            }
        });

        //播放按钮逻辑
        mPlayControl.setOnClickListener(view -> {
            if(mIsCallPlay){
                stopPlay();
            }else {
                startPlay();
            }
        });

        //暂停按钮逻辑
        mPauseControl.setOnClickListener(view -> {
            if(mIsCallPlay){
                if(mSimplePlayer.isPlaying()){
                    pause();
                }else{
                    resume();
                }
            }
        });



        //属性
        @SuppressLint("Recycle") TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PlayerView);

        //如果只显示音频
        //则隐藏视频
        boolean audioOnly = array.getBoolean(R.styleable.PlayerView_audioOnly, false);
        if(audioOnly){
            mPlayerLayout.setVisibility(GONE);
        }

        //如果只显示视频，则隐藏音频
        boolean videoOnly = array.getBoolean(R.styleable.PlayerView_videoOnly, false);
        if(videoOnly){
            findViewById(R.id.llPlayControl).setVisibility(GONE);
        }
    }

    /**
     * 开始播放
     */
    private void startPlay(){
       if(!mIsCallPlay){
           mSimplePlayer.startSdPlayer(mFilePath);
           mPlayControl.setText(R.string.stop);
           mPauseControl.setVisibility(VISIBLE);
           mPauseControl.setText(R.string.pause);
           mIsCallPlay = true;
       }
    }

    private void stopPlay(){
        if(mIsCallPlay){
            mSimplePlayer.reset();
            mPlayControl.setText(R.string.play);
            mPauseControl.setVisibility(GONE);
            mIsCallPlay = false;
        }
    }

    /**
     * 暂停
     */
    private void pause(){
        if(mIsCallPlay) {
            if (mSimplePlayer.isPlaying()) {
                mSimplePlayer.pause();
                mPauseControl.setText(R.string.resume);
            }
        }
    }

    /**
     * 继续
     */
    private void resume(){
        if(mIsCallPlay){
            if(!mSimplePlayer.isPlaying()){
                mSimplePlayer.start();
                mPauseControl.setText(R.string.pause);
            }
        }
    }

    /**
     * 开始播放
     * 供外部调用
     * @param filePath 播放文件
     */
    public void startPlay(String filePath){
        mFilePath = filePath;
        startPlay();
    }

    public void destroy(){
        if(mMediaProgressView != null){
            mMediaProgressView.stop();
        }
        if(mSimplePlayer != null){
            mSimplePlayer.releasePlay();
        }
    }
}