package com.hcs.commondemo.entity;

import com.hcs.android.common.util.JsonUtils;


/**
 * 聊天信息
 */
public class MulticastMessageBo {
    /**
     * 消息发送方
     */
    private String fromUser;
    public String getFromUser(){
        return fromUser;
    }
    public void setFromUser(String fromUser){
        this.fromUser = fromUser;
    }


    /**
     * 消息发送时间
     */
    private String sendTime;
    public String getSendTime(){
        return sendTime;
    }
    public void setSendTime(String sendTime){
        this.sendTime = sendTime;
    }

    /**
     * 消息内容
     */
    private String message;
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }

    public MulticastMessageBo(){

    }
    public MulticastMessageBo(String data){
        try {
            MulticastMessageBo tmp = JsonUtils.toObject(data,MulticastMessageBo.class);
            if(tmp != null){
                this.setFromUser(tmp.getFromUser());
                this.setMessage(tmp.getMessage());
                this.setSendTime(tmp.getSendTime());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
