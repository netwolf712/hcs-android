package com.hcs.android.maintain.entity;

import com.hcs.android.maintain.constant.CommandEnum;

/**
 * 请求数据
 */
public class RequestData {
    public RequestData(){

    }
    public RequestData(CommandEnum commandEnum, String rawData){
        this.commandEnum = commandEnum;
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
