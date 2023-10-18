package com.hcs.android.business.request.model;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.Constant;
import com.hcs.android.business.entity.HandoverLog;
import com.hcs.android.business.service.HandoverLogService;

/**
 * 交班留言纯请求模块
 */
public class HandoverLogRequestModel {

    /**
     * 将消息状态改为已读
     */
    public void readMessage(@NonNull HandoverLog handoverLog){
        handoverLog.setState(Constant.MESSAGE_READ);
        HandoverLogService.getInstance().updateHandoverLog(handoverLog);
    }

    /**
     * 删除消息
     */
    public void deleteMessage(HandoverLog handoverLog){
        new Thread(()->{
            HandoverLogService.getInstance().deleteHandoverLog(handoverLog);
        }).start();
    }
}
