package com.hcs.android.business.service;

import androidx.annotation.NonNull;

import com.hcs.android.business.dao.DataBaseHelper;
import com.hcs.android.business.dao.DeviceDao;
import com.hcs.android.business.entity.Device;
import com.hcs.android.business.entity.DeviceModel;

import java.util.List;

/**
 * 设备服务
 */
public class DeviceService {
    private final DeviceDao mDeviceDao;

    private static DeviceService mInstance = null;
    public static DeviceService getInstance(){
        if(mInstance == null){
            synchronized (DeviceService.class){
                if(mInstance == null) {
                    mInstance = new DeviceService();
                }
            }
        }
        return mInstance;
    }

    public DeviceService(){
        mDeviceDao = DataBaseHelper.getInstance().deviceDao();
    }

    /**
     * 更新或添加
     */
    public void updateDevice(@NonNull DeviceModel deviceModel){
        Device oldDevice = mDeviceDao.findOne(deviceModel.getDevice().getDeviceId());
        deviceModel.getDevice().setUpdateTime(System.currentTimeMillis());
        if(oldDevice != null){
            deviceModel.getDevice().setUid(oldDevice.getUid());
            mDeviceDao.update(deviceModel.getDevice());
        }else {
            mDeviceDao.insert(deviceModel.getDevice());
        }
    }

    public void updateHeartBeatTime(@NonNull DeviceModel deviceModel){
        deviceModel.getDevice().setHeartBeatTime(System.currentTimeMillis());
        mDeviceDao.updateHeartBeatTime(deviceModel.getDevice().getDeviceId(),deviceModel.getDevice().getHeartBeatTime());
    }

    /**
     * 查找某个主机下的所有离线设备
     * @param parentNo 主机号码
     * @param offlineTime 作为离线判断的时间长度
     */
    public List<Device> getOfflineDeviceList(@NonNull String parentNo,long offlineTime){
        long offlineTimePoint = System.currentTimeMillis() - offlineTime;
        return mDeviceDao.getOfflineDeviceList(parentNo,offlineTimePoint);
    }

    public List<Device> getDeviceList(){
        return mDeviceDao.getAll();
    }

    /**
     * 查找某个主机的所有下级分机
     * @param parentNo 主机号码
     * @return 设备列表
     */
    public List<Device> getDeviceListByParentNo(@NonNull String parentNo){
        return mDeviceDao.getDeviceListByParentNo(parentNo);
    }

    /**
     * 查找某个主机的某个位置的的所有下级分机
     * @param parentNo 主机号码
     * @param parentPlaceUid 父位置uid
     * @return 设备列表
     */
    public List<Device> getDeviceListByParentPlace(@NonNull String parentNo,@NonNull String parentPlaceUid){
        return mDeviceDao.getDeviceListByParentPlace(parentNo,parentPlaceUid);
    }

    /**
     * 查找某个主机的某个位置上挂载的分机
     * @param parentNo 主机号码
     * @param placeUid 位置uid
     * @return 设备列表
     */
    public List<Device> getDeviceListByPlace(@NonNull String parentNo,@NonNull String placeUid){
        return mDeviceDao.getDeviceListByPlace(parentNo,placeUid);
    }

    public void deleteDevice(@NonNull DeviceModel deviceModel){
        mDeviceDao.delete(deviceModel.getDevice());
    }

    public Device getDevice(@NonNull String deviceId){
        return mDeviceDao.findOne(deviceId);
    }
}
