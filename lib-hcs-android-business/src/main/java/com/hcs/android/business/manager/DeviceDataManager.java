package com.hcs.android.business.manager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableArrayList;

import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.service.DeviceService;
import com.hcs.android.common.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 设备数据管理器
 * 对设备列表的统一管理
 * 否则变量太多容易出错
 */
public class DeviceDataManager {
    private ObservableArrayList<DeviceModel> mDeviceList;
    /**
     * deviceId,DeviceModel
     */
    private final Map<String,DeviceModel> mDeviceIdMap;
    /**
     * PhoneNo,List&lt;DeviceModel&gt;
     * 可能会导致分机号重复的情况
     * 所以得用list
     */
    private final Map<String,List<DeviceModel>> mDevicePhoneMap;

    /**
     * PlaceUid,List&lt;DeviceModel&gt;
     * 可能会导致分机号重复的情况
     * 所以得用list
     */
    private final Map<String,List<DeviceModel>> mDevicePlaceMap;

    /**
     * 同步专用对象
     */
    protected final Object mSynObj = new Object();

    public DeviceDataManager(){
        mDeviceList = new ObservableArrayList<>();
        mDeviceIdMap = new HashMap<>();
        mDevicePhoneMap = new HashMap<>();
        mDevicePlaceMap = new HashMap<>();
    }

    /**
     * 将设备插入各种map
     */
    private void addToMaps(DeviceModel deviceModel){
        addToIdMap(deviceModel);
        addToPhoneNoMap(deviceModel);
        addToPlaceMap(deviceModel);
    }

    /**
     * 将设备从各种map移除
     */
    private void removeFromMaps(DeviceModel deviceModel){
        removeFromIdMap(deviceModel);
        removeFromPhoneNoMap(deviceModel);
        removeFromPlaceMap(deviceModel);
    }

    /**
     * 清空所有map的数据
     */
    private void clearMaps(){
        synchronized (mSynObj) {
            mDeviceIdMap.clear();
            mDevicePhoneMap.clear();
            mDevicePlaceMap.clear();
        }
    }
    /**
     * 根据序列号在设备列表中找到相应设备
     */
    @Nullable
    private DeviceModel findDeviceById(List<DeviceModel> deviceModelList, String deviceId){
        if(StringUtil.isEmpty(deviceModelList) || StringUtil.isEmpty(deviceId)){
            return null;
        }
        for(DeviceModel deviceModel : deviceModelList){
            if(StringUtil.equalsIgnoreCase(deviceModel.getDevice().getDeviceId(),deviceId)){
                return deviceModel;
            }
        }
        return null;
    }
    /**
     * mDevicePhoneMap的相关操作
     */
    private void addToMap(@NonNull Map<String,List<DeviceModel>> deviceMap, String key, DeviceModel deviceModel){
        synchronized (mSynObj) {
            if(StringUtil.isEmpty(key)){
                return;
            }
            List<DeviceModel> deviceModelList = deviceMap.get(key);
            if(deviceModelList == null){
                deviceModelList = new ArrayList<>();
                deviceModelList.add(deviceModel);
                deviceMap.put(key,deviceModelList);
            }else{
                for(DeviceModel tmp1 : deviceModelList){
                    //如果有相同的号码，则删除之前的
                    if(StringUtil.equalsIgnoreCase(tmp1.getBindPhoneNo(),deviceModel.getBindPhoneNo())){
                        deviceModelList.remove(tmp1);
                        break;
                    }
                }
                DeviceModel tmp = findDeviceById(deviceModelList,deviceModel.getDevice().getDeviceId());
                if(tmp == null){
                    //如果之前没有，则插入
                    //如果已经有了，则不要重复插入
                    deviceModelList.add(deviceModel);
                }
            }
        }
    }
    private void removeFromMap(@NonNull Map<String,List<DeviceModel>> deviceMap, String key, DeviceModel deviceModel){
        synchronized (mSynObj) {
            List<DeviceModel> deviceModelList = deviceMap.get(key);
            if(!StringUtil.isEmpty(deviceModelList)){
                DeviceModel tmp = findDeviceById(deviceModelList,deviceModel.getDevice().getDeviceId());
                if(tmp != null){
                    //如果能找到，则移除
                    deviceModelList.remove(tmp);
                }
            }
        }
    }
    /**
     * mDevicePhoneMap的相关操作
     */
    public void addToPhoneNoMap(DeviceModel deviceModel){
        if(deviceModel == null || deviceModel.getDevice() == null){
            return;
        }
        addToMap(mDevicePhoneMap, deviceModel.getBindPhoneNo(),deviceModel);
    }
    public void removeFromPhoneNoMap(DeviceModel deviceModel){
        if(deviceModel == null || deviceModel.getDevice() == null){
            return;
        }
        removeFromMap(mDevicePhoneMap, deviceModel.getBindPhoneNo(), deviceModel);
    }
    public List<DeviceModel> findInPhoneNoMap(String phoneNo){
        synchronized (mSynObj){
            return mDevicePhoneMap.get(phoneNo);
        }
    }

    /**
     * 只从列表中取第一个
     */
    public DeviceModel findOneInPhoneNoMap(String phoneNo){
        List<DeviceModel> deviceModelList = findInPhoneNoMap(phoneNo);
        if(StringUtil.isEmpty(deviceModelList)){
            return null;
        }
        return deviceModelList.get(0);
    }

    /**
     * mDevicePlaceMap的相关操作
     */
    public void addToPlaceMap(DeviceModel deviceModel){
        if(deviceModel == null || deviceModel.getDevice() == null){
            return;
        }
        addToMap(mDevicePlaceMap, deviceModel.getDevice().getPlaceUid(),deviceModel);
    }
    public void removeFromPlaceMap(DeviceModel deviceModel){
        if(deviceModel == null || deviceModel.getDevice() == null){
            return;
        }
        removeFromMap(mDevicePlaceMap, deviceModel.getDevice().getPlaceUid(), deviceModel);
    }
    public List<DeviceModel> findInPlaceMap(String placeUid){
        synchronized (mSynObj){
            return mDevicePlaceMap.get(placeUid);
        }
    }

    /**
     * 只从列表中取第一个
     */
    public DeviceModel findOneInPlaceMap(String placeUid){
        List<DeviceModel> deviceModelList = findInPlaceMap(placeUid);
        if(StringUtil.isEmpty(deviceModelList)){
            return null;
        }
        return deviceModelList.get(0);
    }

    /**
     * mDeviceIdMap的相关操作
     */
    private void addToIdMap(DeviceModel deviceModel){
        if(deviceModel == null || deviceModel.getDevice() == null){
            return;
        }
        synchronized (mSynObj) {
            mDeviceIdMap.put(deviceModel.getDevice().getDeviceId(), deviceModel);
        }
    }
    private void removeFromIdMap(DeviceModel deviceModel){
        if(deviceModel == null || deviceModel.getDevice() == null){
            return;
        }
        synchronized (mSynObj) {
            mDeviceIdMap.remove(deviceModel.getDevice().getDeviceId());
        }
    }
    public DeviceModel findInIdMap(String deviceId){
        synchronized (mSynObj){
            return mDeviceIdMap.get(deviceId);
        }
    }

    /**
     * 将设备数据插入基础列表
     */
    public void addDeviceModel(DeviceModel deviceModel){
        if(deviceModel == null || deviceModel.getDevice() == null){
            return;
        }
        synchronized (mSynObj){
            DeviceModel tmp = findDeviceById(mDeviceList,deviceModel.getDevice().getDeviceId());
            if(tmp == null){
                //首先要查重
                mDeviceList.add(deviceModel);
                addToMaps(deviceModel);
            }
        }
    }

    /**
     * 将设备从基础列表移除
     */
    public void removeDeviceModel(DeviceModel deviceModel){
        if(deviceModel == null || deviceModel.getDevice() == null){
            return;
        }
        synchronized (mSynObj){
            DeviceModel tmp = findDeviceById(mDeviceList,deviceModel.getDevice().getDeviceId());
            if(tmp != null){
                mDeviceList.remove(tmp);
                removeFromMaps(tmp);
            }
        }
    }

    public List<DeviceModel> getDeviceList(){
        synchronized (mSynObj) {
            return mDeviceList;
        }
    }

    public void clearDeviceList(){
        synchronized (mSynObj){
            mDeviceList.clear();
            clearMaps();
        }
    }

    /**
     * 从设备列表中清除未更新的设备
     * 同时从数据库中删除
     */
    public void removeUnUpdatedDevices(){
        synchronized (mSynObj){
            Iterator<DeviceModel> it = mDeviceList.iterator();
            while (it.hasNext()){
                DeviceModel deviceModel = it.next();
                if(!deviceModel.isUpdated()){
                    it.remove();
                    removeFromMaps(deviceModel);
                    DeviceService.getInstance().deleteDevice(deviceModel);
                }
            }
        }
    }

}
