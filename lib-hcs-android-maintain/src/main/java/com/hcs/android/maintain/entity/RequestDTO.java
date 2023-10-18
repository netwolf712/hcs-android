package com.hcs.android.maintain.entity;


/**
 * 请求参数
 */
public class RequestDTO<T> {
    public RequestDTO(){

    }
    /**
     * 根据deviceModel创建请求头
     */
    public RequestDTO(String commandId){
        setCommandId(commandId);
    }

    /**
     * 命令id
     */
    private String commandId;
    public String getCommandId(){
        return commandId;
    }
    public void setCommandId(String commandId){
        this.commandId = commandId;
    }

    /**
     * 详细数据内容
     * 根据不同的信令需要进行定义
     */
    private T data;
    public T getData(){
        return data;
    }
    public void setData(T data){
        this.data = data;
    }
}
