package com.hcs.android.business.request.viewmodel;


import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.EventBusConstant;
import com.hcs.android.business.entity.IPCamera;
import com.hcs.android.business.entity.Place;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.entity.RequestBindPlace;
import com.hcs.android.business.entity.RequestBindPlaceParent;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestUpdateCapability;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.ISimpleCustomer;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.StringUtil;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.List;


/**
 * 位置详情
 */
public class PlaceDetailViewModel {

    /**
     * 摄像头编号监听器
     */
    private ISimpleCustomer<IPCamera> mIPCListener;
    public void setIPCListener(ISimpleCustomer<IPCamera> ipcListener){
        mIPCListener = ipcListener;
    }
    private PlaceModel mPlaceModel;
    public void setPlaceModel(PlaceModel placeModel){
        this.mPlaceModel = placeModel;
    }
    public PlaceModel getPlaceModel(){
        return mPlaceModel;
    }


    public PlaceDetailViewModel(){
        EventBus.getDefault().register(this);
        this.mPlaceModel = new PlaceModel();
    }

    public void loadPlaceDetail(String placeUid){
        PlaceModel placeModel = WorkManager.getInstance().getPlaceModelFromCache(placeUid);
        if(placeModel != null){
            mPlaceModel = JsonUtils.toObject(JsonUtils.toJsonString(placeModel),PlaceModel.class);
        }
    }

    public void changePlaceInfo(){
        RequestDTO<ResponseList<Place>> requestDTO = new RequestDTO<>(WorkManager.getInstance().getSelfInfo(),CommandEnum.REQ_UPDATE_PLACE_INFO.getId());
        ResponseList<Place> placeList = new ResponseList<>(mPlaceModel.getPlace());
        requestDTO.setData(placeList);
        WorkManager.getInstance().sendInnerRequest(JsonUtils.toJsonString(requestDTO));
    }


    /**
     * 更改位置间的绑定关系
     * @param parentUid 父位置uid
     * @param placeList 子位置uid列表
     */
    public void changeBindPlace(String parentUid,List<String> placeList){
        RequestDTO<RequestBindPlace> requestDTO = new RequestDTO<>(WorkManager.getInstance().getSelfInfo(),CommandEnum.REQ_BIND_PLACE.getId());
        RequestBindPlace requestBindPlace = new RequestBindPlace();
        requestBindPlace.setParentUid(parentUid);
        if(WorkManager.getInstance().getSelfInfo().getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue()){
            //如果自己就是主机，则天自己的号码
            requestBindPlace.setParentNo(WorkManager.getInstance().getSelfInfo().getDevice().getPhoneNo());
        }else{
            //否则填父号码
            requestBindPlace.setParentNo(WorkManager.getInstance().getSelfInfo().getDevice().getParentNo());
        }
        requestBindPlace.setPlaceUidList(placeList);
        requestDTO.setData(requestBindPlace);
        WorkManager.getInstance().sendInnerRequest(JsonUtils.toJsonString(requestDTO));
    }

    /**
     * 位置主动请求修改绑定父位置
     * @param parentPlaceNo 父位置编号
     * @param parentPlaceType 父位置类型
     */
    public void changeBindParent(String parentPlaceNo,int parentPlaceType){
        RequestDTO<RequestBindPlaceParent> requestDTO = new RequestDTO<>(WorkManager.getInstance().getSelfInfo(),CommandEnum.REQ_BIND_PLACE_PARENT.getId());
        RequestBindPlaceParent requestBindPlaceParent = new RequestBindPlaceParent();
        requestBindPlaceParent.setParentPlaceNo(parentPlaceNo);
        requestBindPlaceParent.setParentPlaceType(parentPlaceType);
        if(WorkManager.getInstance().getSelfInfo().getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue()){
            //如果自己就是主机，则天自己的号码
            requestBindPlaceParent.setParentNo(WorkManager.getInstance().getSelfInfo().getDevice().getPhoneNo());
        }else{
            //否则填父号码
            requestBindPlaceParent.setParentNo(WorkManager.getInstance().getSelfInfo().getDevice().getParentNo());
        }
        requestBindPlaceParent.setPlaceUid(mPlaceModel.getPlace().getUid());
        requestDTO.setData(requestBindPlaceParent);
        WorkManager.getInstance().sendInnerRequest(JsonUtils.toJsonString(requestDTO));
    }

    /**
     * 添加/更改IPC信息
     * 或者删除IPC信息,
     * @param ipCamera IPC信息，为null表示删除
     */
    public void changeIPCameraInfo(IPCamera ipCamera){
        RequestDTO<RequestUpdateCapability> requestDTO = new RequestDTO<>(WorkManager.getInstance().getSelfInfo(), CommandEnum.REQ_UPDATE_CAPABILITY.getId());
        RequestUpdateCapability requestUpdateCapability = new RequestUpdateCapability();
        requestUpdateCapability.setIpCamera(ipCamera);
        requestUpdateCapability.setHasIPC(ipCamera != null);
        requestUpdateCapability.setMasterDeviceId(mPlaceModel.getPlace().getMasterDeviceId());
        requestUpdateCapability.setPlaceUid(mPlaceModel.getPlace().getUid());
        requestDTO.setData(requestUpdateCapability);
        WorkManager.getInstance().sendInnerRequest(JsonUtils.toJsonString(requestDTO));
    }

    /**
     * 摄像头更新
     * 只有是当前节点的摄像头更新才通知界面
     */
    @Subscriber(tag = EventBusConstant.HANDLE_IPC_UPDATED, mode = ThreadMode.MAIN)
    public void handleIPCUpdated(final IPCamera ipCamera) {
        if(mIPCListener == null || mPlaceModel == null || mPlaceModel.getPlace() == null){
            return;
        }
        if(ipCamera != null
                && StringUtil.equalsIgnoreCase(ipCamera.getMasterDeviceId(),mPlaceModel.getPlace().getMasterDeviceId())
                && StringUtil.equalsIgnoreCase(ipCamera.getPlaceUid(),mPlaceModel.getPlace().getUid())){
            mIPCListener.accept(ipCamera);
        }
    }
}
