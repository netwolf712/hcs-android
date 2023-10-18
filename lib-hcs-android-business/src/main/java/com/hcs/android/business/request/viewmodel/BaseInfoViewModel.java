package com.hcs.android.business.request.viewmodel;


import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.InitStateEnum;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.StringUtil;

import org.simple.eventbus.EventBus;


public class BaseInfoViewModel {
    private DeviceModel mDeviceModel;
    public void setDeviceModel(DeviceModel deviceModel){
        mDeviceModel = deviceModel;
    }
    public DeviceModel getDeviceModel(){
        return mDeviceModel;
    }
    public BaseInfoViewModel() {
        EventBus.getDefault().register(this);
        mDeviceModel = JsonUtils.toObject(JsonUtils.toJsonString(WorkManager.getInstance().getSelfInfo()),DeviceModel.class);
        //ObjectUtil.CopyObjToAnotherEx(WorkManager.getInstance().getSelfInfo(),DeviceModel.class,mDeviceModel,DeviceModel.class);
    }

    /**
     * 修改自己的主机、分机号码
     * 修改IP地址设置
     */
    public void changeBaseInfo(){
        if(WorkManager.getInstance().getInitStateEnum() == InitStateEnum.INITIALIZED) {
            RequestDTO<DeviceModel> requestDTO = new RequestDTO<>(mDeviceModel, CommandEnum.REQ_CHANGE_DEVICE_INFO.getId());
            requestDTO.setData(mDeviceModel);
            WorkManager.getInstance().sendInnerRequest(JsonUtils.toJsonString(requestDTO));
        }else{
            WorkManager.getInstance().writeDeviceBaseConfig(mDeviceModel);
        }
    }

    /**
     * 校验验证码
     * (设备id后6位)
     * @param code 验证码
     * @return 是否校验成功
     */
    public boolean checkCode(String code){
        String deviceId = mDeviceModel.getDevice().getDeviceId();
        String srcCode = deviceId.substring(Math.max(deviceId.length() - 4, 0));
        return StringUtil.equalsIgnoreCase(srcCode,code);
    }
}
