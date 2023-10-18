package com.hcs.android.business.manager;

import android.os.Message;

import androidx.annotation.NonNull;

import com.hcs.android.business.WorkHandler;
import com.hcs.android.business.constant.WorkMessageEnum;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.RequestDTO;

public class HandlerBase {
    private WorkHandler mWorkHandler;
    public HandlerBase(WorkHandler workHandler){
        mWorkHandler = workHandler;
    }
    private void sendMessage(Message message){
        if(mWorkHandler != null){
            mWorkHandler.sendMessage(message);
        }
    }

    private void sendMessage(@NonNull WorkMessageEnum w){
        Message message = new Message();
        message.what = w.getMessage();
        sendMessage(message);
    }
    /**
     * 通知已经登录成功
     */
    public void sendLoginOK(){
        sendMessage(WorkMessageEnum.MESSAGE_LOGIN_OK);
    }

    /**
     * 通知读取设备列表成功
     */
    public void sendReadDeviceListOK(){
        sendMessage(WorkMessageEnum.READ_DEVICE_LIST_OK);
    }

    public void sendMulticast(Object data){
        Message message = new Message();
        message.what = WorkMessageEnum.SEND_MULTICAST.getMessage();
        message.obj = data;
        sendMessage(message);
    }

    public void updateLinphoneFriend(DeviceModel data){
        Message message = new Message();
        message.what = WorkMessageEnum.UPDATE_LINPHONE_FRIEND.getMessage();
        message.obj = data;
        sendMessage(message);
    }
}
