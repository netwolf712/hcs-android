package com.hcs.android.onvif.onvif;

import android.content.Context;
import android.util.Log;


import com.hcs.android.common.util.log.KLog;
import com.hcs.android.onvif.onvifBean.Digest;
import com.hcs.android.onvif.onvifBean.OnvifDevice;
import com.hcs.android.onvif.ptzControl.PtzControlView;
import com.hcs.android.onvif.util.Gsoap;
import com.hcs.android.onvif.util.HttpUtil;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Author ： BlackHao
 * Time : 2018/2/6 16:14
 * Description : onvif 云台 RelativeMove
 */

public class OnvifPtzThread extends Thread {

    private double x, y;
    private double zoomX;
    //运行标识
    private boolean runTag = true;
    //转动方向标识
    private int direction = 0;
    //设备信息
    private OnvifDevice device;
    //Context
    private Context context;

    private final Object mSynObj = new Object();
    /**
     * 是否正在缩放
     */
    private boolean isZooming = false;

    public OnvifPtzThread(OnvifDevice device, Context context) {
        this.device = device;
        this.context = context;
        this.x = 0;
        this.y = 0;
        this.zoomX = 0;
    }

    @Override
    public void run() {
        super.run();
        while (runTag) {
            try {
                synchronized (mSynObj) {
                    if (!isZooming) {
                        switch (direction) {
                            case PtzControlView.LEFT:
                                if (x - 0.1 >= -1) {
                                    x = x - 0.1;
                                    Log.e("OnvifPtzThread", sendPostString());
                                }
                                break;
                            case PtzControlView.RIGHT:
                                if (x + 0.1 <= 1) {
                                    x = x + 0.1;
                                    Log.e("OnvifPtzThread", sendPostString());
                                }
                                break;
                            case PtzControlView.TOP:
                                if (y + 0.1 <= 1) {
                                    y = y + 0.1;
                                    Log.e("OnvifPtzThread", sendPostString());
                                }
//                        if (zoomX + 0.1 <= 1) {
//                            zoomX = zoomX + 0.1;
//                            Log.e("OnvifPtzThread", sendPostString());
//                        }
                                break;
                            case PtzControlView.BOTTOM:
                                if (y - 0.1 >= -1) {
                                    y = y - 0.1;
                                    Log.e("OnvifPtzThread", sendPostString());
                                }
//                        if (zoomX - 0.1 >= 0) {
//                            zoomX = zoomX - 0.1;
//                            Log.e("OnvifPtzThread", sendPostString());
//                        }
                                break;
                        }
                    } else {
                        //缩放操作只需执行一次
                        sendPostString();
                        isZooming = false;
                        return;
                    }
                }
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过用户名/密码/assets 文件获取对应需要发送的String
     *
     * @return 需要发送的 string
     */
    private String sendPostString() throws Exception {
        //读取文件内容
        String postString = "";
        InputStream is = context.getAssets().open("relativeMove.xml");
        byte[] postData = new byte[is.available()];
        if (is.read(postData) > 0) {
            postString = new String(postData, StandardCharsets.UTF_8);
        }
        //获取digest
        Digest digest = Gsoap.getDigest(device.getUsername(), device.getPassword());
        //需要digest
        if (digest != null) {
            postString = String.format(postString, digest.getUserName(),
                    digest.getEncodePsw(), digest.getNonce(), digest.getCreatedTime(),
                    device.getProfiles().get(0).getToken(), x + "", y + "", zoomX + "");
        }
//        Log.e("OnvifPtzThread", postString);
        return HttpUtil.postRequest(device.getPtzUrl(), postString);
    }

    public void setDirection(int direction) {
        synchronized (mSynObj) {
            this.direction = direction;
            isZooming = false;
        }
    }

    public void finish() {
        runTag = false;
    }

    public void setDevice(OnvifDevice device) {
        this.device = device;
    }

    public void setZoomX(double zoomX){
        synchronized (mSynObj) {
            this.zoomX = zoomX;
            isZooming = true;
        }

    }

    /**
     * 立即发送位置转换信息
     */
    public void sendImmediately(double x,double y,double zoomX){
        synchronized (mSynObj) {
            this.x = x;
            this.y = y;
            this.zoomX = zoomX;
            new Thread(() -> {
                try {
                    sendPostString();
                }catch (Exception e){
                    KLog.e(e);
                }
            }).start();
        }
    }
}
