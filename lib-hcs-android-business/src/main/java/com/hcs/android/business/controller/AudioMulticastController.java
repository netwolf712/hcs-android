package com.hcs.android.business.controller;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.business.entity.MulticastGroup;
import com.hcs.android.business.entity.RequestAudioMulticast;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.web.AjaxResult;

/**
 * 语音广播相关
 */
@CommandMapping
public class AudioMulticastController {
    /**
     * 开始广播请求
     */
    @CommandId("req-multicast-start")
    public AjaxResult handleStartAudioMulticast(String str){
        RequestDTO<RequestAudioMulticast> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestAudioMulticast.class});
        WorkManager.getInstance().handleStartAudioMulticast(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 结束广播请求
     */
    @CommandId("req-multicast-stop")
    public AjaxResult handleStopAudioMulticast(String str){
        RequestDTO<RequestAudioMulticast> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestAudioMulticast.class});
        WorkManager.getInstance().handleStopAudioMulticast(requestDTO);
        return AjaxResult.success("");
    }

    /**
     * 请求更新病房信息
     */
    @CommandId("req-update-group-info")
    public AjaxResult updateGroupInfo(String str){
        RequestDTO<ResponseList<MulticastGroup>> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,ResponseList.class,MulticastGroup.class});
        WorkManager.getInstance().updateGroupInfo(requestDTO);
        return AjaxResult.success("");
    }
}
