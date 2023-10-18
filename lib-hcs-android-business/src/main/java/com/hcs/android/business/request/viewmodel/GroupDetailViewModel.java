package com.hcs.android.business.request.viewmodel;


import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.entity.RequestBindGroup;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.entity.MulticastGroup;
import com.hcs.android.business.entity.MulticastGroupModel;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import org.simple.eventbus.EventBus;
import java.util.List;


/**
 * 分区详情
 */
public class GroupDetailViewModel {
    private MulticastGroupModel mMulticastGroupModel;
    public void setMulticastGroupModel(MulticastGroupModel MulticastGroupModel){
        this.mMulticastGroupModel = MulticastGroupModel;
    }
    public MulticastGroupModel getMulticastGroupModel(){
        return mMulticastGroupModel;
    }


    public GroupDetailViewModel(){
        EventBus.getDefault().register(this);
        this.mMulticastGroupModel = new MulticastGroupModel();
    }

    public void loadGroupDetail(String deviceId,int groupSn){
        MulticastGroupModel multicastGroupModel = WorkManager.getInstance().getMulticastGroupModelFromCache(deviceId,groupSn);
        if(multicastGroupModel != null){
            mMulticastGroupModel = JsonUtils.toObject(JsonUtils.toJsonString(multicastGroupModel),MulticastGroupModel.class);
        }
    }

    public List<PlaceModel> getPlaceList(){
        return WorkManager.getInstance().getPlaceListByGroupFromCache(mMulticastGroupModel.getMulticastGroup().getGroupSn());
    }

    public void changeGroupInfo(){
        RequestDTO<ResponseList<MulticastGroup>> requestDTO = new RequestDTO<>(WorkManager.getInstance().getSelfInfo(),CommandEnum.REQ_UPDATE_GROUP_INFO.getId());
        ResponseList<MulticastGroup> multicastGroupList = new ResponseList<>(mMulticastGroupModel.getMulticastGroup());
        requestDTO.setData(multicastGroupList);
        WorkManager.getInstance().sendInnerRequest(JsonUtils.toJsonString(requestDTO));
    }

    public void changeBindGroup(Integer groupSn,List<String> placeUidList){
        RequestDTO<RequestBindGroup> requestDTO = new RequestDTO<>(WorkManager.getInstance().getSelfInfo(),CommandEnum.REQ_BIND_GROUP.getId());
        RequestBindGroup requestBindGroup = new RequestBindGroup();
        requestBindGroup.setGroupSn(groupSn);
        if(WorkManager.getInstance().getSelfInfo().getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue()){
            //如果自己就是主机，则天自己的号码
            requestBindGroup.setParentNo(WorkManager.getInstance().getSelfInfo().getDevice().getPhoneNo());
        }else{
            //否则填父号码
            requestBindGroup.setParentNo(WorkManager.getInstance().getSelfInfo().getDevice().getParentNo());
        }
        requestBindGroup.setPlaceUidList(placeUidList);
        requestDTO.setData(requestBindGroup);
        WorkManager.getInstance().sendInnerRequest(JsonUtils.toJsonString(requestDTO));
    }
}
