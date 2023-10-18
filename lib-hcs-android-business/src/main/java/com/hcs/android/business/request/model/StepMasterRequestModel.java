package com.hcs.android.business.request.model;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.AttachmentUseEnum;
import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.StepMasterTypeEnum;
import com.hcs.android.business.entity.Attachment;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.RequestAttachmentList;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestStepMasterList;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.entity.ResponseStepMaster;
import com.hcs.android.business.entity.StepMaster;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.entity.ObservableData;
import com.hcs.android.server.web.AjaxResult;

import java.util.List;

/**
 * 继任主机
 */
public class StepMasterRequestModel {

    /**
     * 当前页面，永远是第一页
     */
    private final int CURRENT_PAGE = 1;
    /**
     * 页面大小，足够大
     */
    private final int PAGE_SIZE = 10000;
    @NonNull
    public ObservableData<List<StepMaster>> getStepMasterList(Integer stepMasterType,String masterDeviceId){
        ObservableData<List<StepMaster>> listObservable = new ObservableData<>();
        DeviceModel deviceModel = WorkManager.getInstance().getSelfInfo();
        RequestDTO<RequestStepMasterList> requestDTO = new RequestDTO<>(deviceModel, CommandEnum.REQ_LIST_STEP_MASTER.getId());
        RequestStepMasterList requestStepMasterList = new RequestStepMasterList();
        requestStepMasterList.setStepMasterType(stepMasterType);
        requestStepMasterList.setMasterDeviceId(masterDeviceId);
        requestStepMasterList.setCurrentPage(CURRENT_PAGE);
        requestStepMasterList.setPageSize(PAGE_SIZE);
        requestDTO.setData(requestStepMasterList);
        ObservableData<AjaxResult> resultObservableData = WorkManager.getInstance().sendInnerRequestObservable(JsonUtils.toJsonString(requestDTO));
        resultObservableData.addObserver((observable, o) -> {
            if(o instanceof AjaxResult){
                AjaxResult res = (AjaxResult) o;
                if(res.getData() != null){
                    ResponseStepMaster stepMasterResponseList = JsonUtils.toObject(JsonUtils.toJsonString(res.getData()),ResponseStepMaster.class);
                    listObservable.setT(stepMasterResponseList.getList());
                }
            }
        });
        return listObservable;
    }

    /**
     * 更新主机信息
     */
    public void updateStepMasters(Integer stepMasterType,List<StepMaster> stepMasterList){
        DeviceModel deviceModel = WorkManager.getInstance().getSelfInfo();
        RequestDTO<ResponseList<StepMaster>> requestDTO = new RequestDTO<>(deviceModel,CommandEnum.REQ_UPDATE_STEP_MASTER.getId());
        ResponseList<StepMaster> stepMasterResponseList = new ResponseList<>(stepMasterList);
        requestDTO.setData(stepMasterResponseList);
        WorkManager.getInstance().sendInnerRequest(JsonUtils.toJsonString(requestDTO));

    }
}
