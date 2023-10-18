package com.hcs.android.onvif.onvif;

import android.content.Context;

import androidx.annotation.Nullable;

import com.hcs.android.common.util.ISimpleCustomer;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.onvif.onvifBean.MediaProfile;
import com.hcs.android.onvif.onvifBean.OnvifDevice;

import java.util.ArrayList;
import java.util.List;


public class OnvifManager {

    private final static String ONVIF_SERVER_PATH = "/onvif/device_service";
    private final static String ONVIF_PROTOCOL_HEAD = "http://";
    /**
     * 摄像头地址
     */
    private String mHost;
    /**
     * onvif协议的用户名
     */
    private String mUsername;
    /**
     * onvif协议的密码
     */
    private String mPassword;

    /**
     * 设备管理器
     */
    private OnvifDevice mOnvifDevice;

    /**
     * 云台控制器
     */
    private OnvifPtzThread mPtzController;

    public OnvifManager(String host,String username,String password){
        mHost = host;
        mUsername = username;
        mPassword = password;
        mOnvifDevice = new OnvifDevice(mUsername,mPassword,mHost);
        mOnvifDevice.setServiceUrl(ONVIF_PROTOCOL_HEAD + host + ONVIF_SERVER_PATH);
    }

    public void createDevice(Context context, ISimpleCustomer<OnvifManager> simpleCustomer){
        new GetDeviceInfoThread(mOnvifDevice,context,(isSuccess,device,msg)->{
            if(isSuccess){
                mOnvifDevice = device;
                if(!StringUtil.isEmpty(device.getPtzUrl())){
                    //如果有云台控制功能，则初始化云台控制
                    mPtzController = new OnvifPtzThread(device,context);
                }
                if(simpleCustomer != null){
                    simpleCustomer.accept(this);
                }
            }
        }).start();
    }


    /**
     * 获取视频源列表
     */
    @Nullable
    public List<String> getVideoStreams(){
        if(mOnvifDevice == null){
            return null;
        }
        List<MediaProfile> profiles = mOnvifDevice.getProfiles();
        if(!StringUtil.isEmpty(profiles)){
            List<String> streamList = new ArrayList<>();
            for(MediaProfile profile : profiles){
                if(!StringUtil.isEmpty(profile.getRtspUrl())) {
                    String stream = OnvifUtils.getVideoStream(profile.getRtspUrl(),mUsername,mPassword);
                    streamList.add(stream);
                }
            }
            return streamList;
        }
        return null;
    }

    /**
     * 获取第一个视频流地址
     */
    public String getFirstVideoStream(){
        List<String> videoStreams = getVideoStreams();
        if(!StringUtil.isEmpty(videoStreams)){
            return videoStreams.get(0);
        }
        return "";
    }
    public OnvifDevice getDevice(){
        return mOnvifDevice;
    }

    /**
     * 改变摄像头云台方向
     */
    public void startPTZ(int direction){
        if(mPtzController != null && mPtzController.isInterrupted()){
            mPtzController.setDirection(direction);
            mPtzController.start();
        }
    }

    /**
     * 停止云台控制
     */
    public void stopPTZ(){
        if(mPtzController != null && !mPtzController.isInterrupted()){
            mPtzController.finish();
        }
    }

    /**
     * 云台缩放
     */
    public void zoomPTZ(double zoom){
        if(mPtzController != null){
            mPtzController.setZoomX(zoom);
            if(mPtzController.isInterrupted()){
                mPtzController.start();
            }
        }
    }

    /**
     * 是否支持PTZ
     * @return true 是，false 否
     */
    public boolean hasPTZ(){
        return !StringUtil.isEmpty(mOnvifDevice.getPtzUrl());
    }

    /**
     * 立即发送位置转换信息
     */
    public void sendImmediately(double x,double y,double zoomX){
        if(mPtzController != null){
            mPtzController.sendImmediately(x,y,zoomX);
        }
    }

    public void close(){
        if(mPtzController != null){
            mPtzController.finish();
        }
    }
}
