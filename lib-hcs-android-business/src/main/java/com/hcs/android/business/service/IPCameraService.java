package com.hcs.android.business.service;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.hcs.android.business.dao.DataBaseHelper;
import com.hcs.android.business.dao.IPCameraDao;
import com.hcs.android.business.entity.IPCamera;
import com.hcs.android.common.util.StringUtil;

import java.util.List;

/**
 * IP摄像头
 */
public class IPCameraService {
    private final IPCameraDao mIPCameraDao;
    private static IPCameraService mInstance = null;
    public static IPCameraService getInstance(){
        if(mInstance == null){
            synchronized (IPCameraService.class){
                if(mInstance == null) {
                    mInstance = new IPCameraService();
                }
            }
        }
        return mInstance;
    }

    public IPCameraService(){
        mIPCameraDao = DataBaseHelper.getInstance().ipCameraDao();
    }

    public IPCamera getIPCamera(String masterDeviceId,String placeUid){
        if(masterDeviceId == null || placeUid == null ){
            return null;
        }
        return mIPCameraDao.findOne(masterDeviceId,placeUid);
    }
    public IPCamera getIPCameraById(int uid){
        return mIPCameraDao.findOneById(uid);
    }

    public LiveData<List<IPCamera>> getIPCameraList(int offset, int limit){
        return mIPCameraDao.getAll(limit,offset);
    }

    public List<IPCamera> getIPCameraList(){
        return mIPCameraDao.getAll();
    }

    public Integer getIPCameraCount(){
        return mIPCameraDao.countAll();
    }

    /**
     * 更新或添加
     */
    public void updateIPCamera(@NonNull IPCamera ipCamera){
        IPCamera oldIPCamera = getIPCamera(ipCamera.getMasterDeviceId(),ipCamera.getPlaceUid());
        ipCamera.setUpdateTime(System.currentTimeMillis());
        if(oldIPCamera != null){
            ipCamera.setUid(oldIPCamera.getUid());
            mIPCameraDao.update(ipCamera);
        }else{
            mIPCameraDao.insert(ipCamera);
        }
    }

    public void deleteIPCamera(@NonNull IPCamera ipCamera){
        mIPCameraDao.delete(ipCamera);
    }


    public void deleteAll(){
        mIPCameraDao.deleteAll();
    }

    public void insertAll(List<IPCamera> sectionList){
        if(StringUtil.isEmpty(sectionList)){
            return;
        }
        IPCamera[] ipCameras = sectionList.toArray(new IPCamera[0]);
        mIPCameraDao.insertAll(ipCameras);
    }
}
