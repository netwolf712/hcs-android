package com.hcs.android.business.request.model;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.AttachmentUseEnum;
import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.entity.Attachment;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.RequestAttachmentList;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.entity.ObservableData;
import com.hcs.android.server.web.AjaxResult;

import java.util.List;

/**
 * 附件
 */
public class AttachmentRequestModel {

    /**
     * 当前页面，永远是第一页
     */
    private final int CURRENT_PAGE = 1;
    /**
     * 页面大小，足够大
     */
    private final int PAGE_SIZE = 10000;
    @NonNull
    private ObservableData<List<Attachment>> getAttachmentList(String use){
        ObservableData<List<Attachment>> listObservable = new ObservableData<>();
        DeviceModel deviceModel = WorkManager.getInstance().getSelfInfo();
        RequestDTO<RequestAttachmentList> requestDTO = new RequestDTO<>(deviceModel, CommandEnum.REQ_LIST_ATTACHMENT.getId());
        RequestAttachmentList requestAttachmentList = new RequestAttachmentList();
        requestAttachmentList.setUse(use);
        requestAttachmentList.setCurrentPage(CURRENT_PAGE);
        requestAttachmentList.setPageSize(PAGE_SIZE);
        requestAttachmentList.setMasterDeviceId(deviceModel.getDevice().getDeviceId());
        requestDTO.setData(requestAttachmentList);
        ObservableData<AjaxResult> resultObservableData = WorkManager.getInstance().sendInnerRequestObservable(JsonUtils.toJsonString(requestDTO));
        resultObservableData.addObserver((observable, o) -> {
            if(o instanceof AjaxResult){
                AjaxResult res = (AjaxResult) o;
                if(res.getData() != null && res.getData() instanceof ResponseList){
                    ResponseList<Attachment> attachmentResponseList = JsonUtils.toObject(JsonUtils.toJsonString(res.getData()),ResponseList.class,Attachment.class);
                    listObservable.setT(attachmentResponseList.getList());
                }
            }
        });
        return listObservable;
    }
    /**
     * 获取广播数据
     */
    public ObservableData<List<Attachment>> getAudioMulticastFiles(){
        return getAttachmentList(String.valueOf(AttachmentUseEnum.GroupMulticast.getValue()));
    }
    /**
     * 获取铃声数据
     */
    public ObservableData<List<Attachment>> getRingFiles(){
        return getAttachmentList(String.valueOf(AttachmentUseEnum.RING.getValue()));
    }
}
