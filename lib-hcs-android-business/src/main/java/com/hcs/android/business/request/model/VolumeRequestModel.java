package com.hcs.android.business.request.model;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.business.constant.AttachmentUseEnum;
import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.entity.Attachment;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.RequestAttachmentList;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestVolumeSet;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.server.entity.ObservableData;
import com.hcs.android.server.web.AjaxResult;

import java.util.List;

/**
 * 音量
 */
public class VolumeRequestModel {

    /**
     * 获取音量配置
     */
    @NonNull
    public RequestVolumeSet getVolumeSet(){
        return WorkManager.getInstance().getVolumeSet();
    }

    /**
     * 设置音量
     */
    public void updateVolumeSet(RequestVolumeSet requestVolumeSet){
        RequestDTO<RequestVolumeSet> requestDTO = new RequestDTO<>(WorkManager.getInstance().getSelfInfo(),CommandEnum.REQ_UPDATE_VOLUME.getId());
        requestDTO.setData(requestVolumeSet);
        WorkManager.getInstance().handleReqVolumeSet(requestDTO);
    }

    /**
     * 回复出厂设置
     */
    public void useDefaultSet(){
        Context context = BusinessApplication.getAppContext();
        RequestVolumeSet requestVolumeSet = new RequestVolumeSet();
        requestVolumeSet.setMasterSpeakerDayVolume(context.getResources().getInteger(R.integer.default_master_speaker_day_volume));
        requestVolumeSet.setMasterRingDayVolume(context.getResources().getInteger(R.integer.default_master_ring_day_volume));
        requestVolumeSet.setMasterKeyDayVolume(context.getResources().getInteger(R.integer.default_master_key_day_volume));
        requestVolumeSet.setSlaveSpeakerDayVolume(context.getResources().getInteger(R.integer.default_slave_speaker_day_volume));

        requestVolumeSet.setNightVolumeSetEnable(context.getResources().getBoolean(R.bool.default_night_volume_set_enable));

        requestVolumeSet.setMasterSpeakerNightVolume(context.getResources().getInteger(R.integer.default_master_speaker_night_volume));
        requestVolumeSet.setMasterRingNightVolume(context.getResources().getInteger(R.integer.default_master_ring_night_volume));
        requestVolumeSet.setMasterKeyNightVolume(context.getResources().getInteger(R.integer.default_master_key_night_volume));
        requestVolumeSet.setSlaveSpeakerNightVolume(context.getResources().getInteger(R.integer.default_slave_speaker_night_volume));

        updateVolumeSet(requestVolumeSet);
    }
}
