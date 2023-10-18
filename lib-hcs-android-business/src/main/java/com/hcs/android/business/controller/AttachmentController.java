package com.hcs.android.business.controller;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.business.constant.UpdateTypeEnum;
import com.hcs.android.business.entity.Attachment;
import com.hcs.android.business.entity.Patient;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.entity.RequestAttachmentList;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestPlaceList;
import com.hcs.android.business.entity.RequestUpdateDict;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.business.service.AttachmentService;
import com.hcs.android.business.service.DictService;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.server.web.AjaxResult;

/**
 * 附件
 */
@CommandMapping
public class AttachmentController {
    /**
     * 请求更新附件
     */
    @CommandId("req-update-attachment")
    public AjaxResult updateAttachment(String str){
        RequestDTO<ResponseList<Attachment>> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,ResponseList.class,Attachment.class});
        ResponseList<Attachment> responseList = requestDTO.getData();
        if(responseList.getUpdateType() == UpdateTypeEnum.UPDATE_FORCE.getValue()){
            AttachmentService.getInstance().deleteAll();
        }
        if(!StringUtil.isEmpty(responseList.getList())){
            for(Attachment attachment : responseList.getList()){
                if(responseList.getUpdateType() != UpdateTypeEnum.UPDATE_DELETE.getValue()) {
                    AttachmentService.getInstance().updateAttachment(attachment);
                }else{
                    AttachmentService.getInstance().deleteAttachment(attachment);
                }
            }
        }
        return AjaxResult.success("");
    }

    /**
     * 请求获取附件列表
     */
    @CommandId("req-list-attachment")
    public AjaxResult getAttachmentList(String str){
        RequestDTO<RequestAttachmentList> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestAttachmentList.class});
        if(requestDTO.getData() != null) {
            RequestAttachmentList requestAttachmentList = requestDTO.getData();
            ResponseList<Attachment> attachmentResponseList = new ResponseList<>(AttachmentService.getInstance().getAttachmentList(requestAttachmentList.getUse(), (requestAttachmentList.getCurrentPage() - 1) * requestAttachmentList.getPageSize(), requestAttachmentList.getPageSize()));
            return AjaxResult.success("",attachmentResponseList);
        }else {
            return AjaxResult.error("bad params");
        }
    }
}
