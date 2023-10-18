package com.hcs.android.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hcs.android.common.util.StringUtil;
import com.hcs.android.onvif.onvif.OnvifHelper;
import com.hcs.android.onvif.onvif.OnvifManager;
import com.hcs.android.onvif.ptzControl.PtzControlView;
import com.hcs.android.ui.R;
import com.hcs.android.ui.player.SimplePlayer;
import com.hcs.android.ui.util.UIThreadUtil;

/**
 * netwolf
 */
public class IPCameraView extends LinearLayout {

    /**
     * 云台控制器显示模式-隐藏
     */
    private final static int PTZ_SHOW_MODE_HIDDEN = 0;
    /**
     * 云台控制器显示模式-显示
     */
    private final static int PTZ_SHOW_MODE_SHOW = 1;
    /**
     * 云台控制器显示模式-自动（支持PTZ时显示，不支持时隐藏）
     */
    private final static int PTZ_SHOW_MODE_AUTO = 2;
    /**
     * 播放器
     */
    private final SimplePlayer mSimplePlayer;

    /**
     * 存放播放器的布局
     */
    private FrameLayout mPlayerLayout;

    private final PtzControlView mPtzController;

    private OnvifManager mOnvifManager;

    private final int mPtzShowMode;

    /**
     * 需要调整的默认位置
     */
    private Double mDefaultPositionX;
    private Double mDefaultPositionY;
    private Double mDefaultZoom;
    public IPCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_ip_camera,this);
        mSimplePlayer = new SimplePlayer(context);
        mSimplePlayer.createView(context);
        mPlayerLayout = findViewById(R.id.video_layout);
        mPlayerLayout.addView(mSimplePlayer.getPlayerView());
        mPtzController = findViewById(R.id.ptzController);

        //属性
        @SuppressLint("Recycle")
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.IPCameraView);
        mPtzShowMode = array.getInt(R.styleable.IPCameraView_PTZShowMode,PTZ_SHOW_MODE_HIDDEN);
        if(mPtzShowMode == PTZ_SHOW_MODE_HIDDEN || mPtzShowMode == PTZ_SHOW_MODE_AUTO){
            mPtzController.setVisibility(GONE);
        }else if(mPtzShowMode == PTZ_SHOW_MODE_SHOW){
            mPtzController.setVisibility(VISIBLE);
        }
    }

    public void openCamera(String host,String username,String password,Double x,Double y,Double zoomX){
        mDefaultPositionX = x;
        mDefaultPositionY = y;
        mDefaultZoom = zoomX;
        openCamera(host, username, password);
    }
    public void openCamera(String host,String username,String password){
        OnvifHelper.buildManager(getContext(),host,username,password, onvifManager -> {
            mOnvifManager = onvifManager;
            UIThreadUtil.runOnUiThread(()->{
                if(mOnvifManager.hasPTZ()) {
                    if(mPtzShowMode == PTZ_SHOW_MODE_AUTO) {
                        mPtzController.setVisibility(VISIBLE);
                    }
                    if(mDefaultPositionX != null && mDefaultPositionY != null && mDefaultZoom != null){
                        mOnvifManager.sendImmediately(mDefaultPositionX,mDefaultPositionY,mDefaultZoom);
                    }
                    mPtzController.setCallBack(((view, b, i) -> {
                        if (b) {
                            mOnvifManager.startPTZ(i);
                        } else {
                            mOnvifManager.stopPTZ();
                        }
                    }));
                }else{
                    if(mPtzShowMode == PTZ_SHOW_MODE_AUTO) {
                        //不支持云台的话直接隐藏
                        mPtzController.setVisibility(GONE);
                    }
                }
                String streamUrl = mOnvifManager.getFirstVideoStream();
                if(!StringUtil.isEmpty(streamUrl)){
                    mSimplePlayer.startSdPlayer(streamUrl);
                }
            });

        });
    }

    /**
     * 关闭摄像头
     */
    public void closeCamera(){
        if(mSimplePlayer != null){
            mSimplePlayer.releasePlay();
        }
        if(mOnvifManager != null){
            mOnvifManager.close();
        }
    }
}