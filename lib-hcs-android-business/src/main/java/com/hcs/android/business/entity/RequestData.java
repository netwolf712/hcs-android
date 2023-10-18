package com.hcs.android.business.entity;

import com.hcs.android.business.constant.CommandEnum;

/**
 * 请求数据
 */
public class RequestData {
    public RequestData(){

    }
    public RequestData(CommandEnum commandEnum,Long commandIndex,String rawData){
        this.commandEnum = commandEnum;
        this.commandIndex = commandIndex;
        this.rawData = rawData;
    }
    /**
     * 命令类型
     */
    private CommandEnum commandEnum;

    public CommandEnum getCommandEnum() {
        return commandEnum;
    }

    public void setCommandEnum(CommandEnum commandEnum) {
        this.commandEnum = commandEnum;
    }

    /**
     * 命令索引
     */
    private Long commandIndex;

    public Long getCommandIndex() {
        return commandIndex;
    }

    public void setCommandIndex(Long commandIndex) {
        this.commandIndex = commandIndex;
    }

    /**
     * 原始命令数据
     */
    private String rawData;

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }
}
