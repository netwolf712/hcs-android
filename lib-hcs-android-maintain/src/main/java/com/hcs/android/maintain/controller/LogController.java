package com.hcs.android.maintain.controller;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.maintain.LogManager;
import com.hcs.android.maintain.entity.RequestDTO;
import com.hcs.android.maintain.entity.RequestGetConfig;
import com.hcs.android.maintain.entity.RequestSetConfig;
import com.hcs.android.server.web.AjaxResult;

import java.util.List;

/**
 * 日志
 */
@CommandMapping
public class LogController {
    /**
     * 请求获取日志配置
     */
    @CommandId("maintain-req-log-get-config")
    public AjaxResult getLogConfig(String str){
        RequestDTO<RequestGetConfig> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestGetConfig.class});
        String logType = null;
        if(requestDTO.getData() != null){
            logType = requestDTO.getData().getLogType();
        }
        return AjaxResult.success("",LogManager.getInstance().getLogConfigList(logType));
    }

    /**
     * 请求设置日志配置
     */
    @CommandId("maintain-req-log-set-config")
    public AjaxResult setLogConfig(String str){
        RequestDTO<List<RequestSetConfig>> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,List.class,RequestSetConfig.class});
        LogManager.getInstance().setLogConfig(requestDTO.getData());
        return AjaxResult.success("");
    }
}
