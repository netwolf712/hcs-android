package com.hcs.android.business;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.hcs.android.business.constant.WorkMessageEnum;

public class WorkHandler extends Handler {
    private HandlerListener mHandlerListener = null;
    public WorkHandler(Looper looper) {
        super(looper);
    }
    public void setHandlerListener(HandlerListener handlerListener){
        mHandlerListener = handlerListener;
    }
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (WorkMessageEnum.findByInt(msg.what)){
            case MESSAGE_LOGIN_OK:
                if(mHandlerListener != null){
                    mHandlerListener.onLoginOK();
                }
                break;
            case SEND_MULTICAST:
                if(mHandlerListener != null){
                    mHandlerListener.onSendMulticast(msg.obj);
                }
                break;
            case UPDATE_LINPHONE_FRIEND:
                if(mHandlerListener != null){
                    mHandlerListener.onUpdateLinphoneFriend(msg.obj);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 回调监听器
     */
    public interface HandlerListener{
        /**
         * 登录成功
         */
        void onLoginOK();

        /**
         * 获取设备列表成功
         */
        void onReadDeviceListOK();

        /**
         * 发送广播数据
         */
        void onSendMulticast(Object obj);

        /**
         * 更新linphone好友
         */
        void onUpdateLinphoneFriend(Object obj);
    }

}
