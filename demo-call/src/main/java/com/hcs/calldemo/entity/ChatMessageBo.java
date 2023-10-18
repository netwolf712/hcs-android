package com.hcs.calldemo.entity;

import com.hcs.android.common.util.DateUtil;

import org.linphone.core.ChatMessage;

import java.util.Date;

/**
 * 聊天信息
 */
public class ChatMessageBo {
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
     * 消息接收方
     */
    private String toUser;
    public String getToUser(){
        return toUser;
    }
    public void setToUser(String toUser){
        this.toUser = toUser;
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

    public ChatMessageBo(){

    }
    public ChatMessageBo(ChatMessage chatMessage){
        try {
            this.setFromUser(chatMessage.getFromAddress().asString());
            this.setToUser(chatMessage.getToAddress().asString());
            this.setSendTime(DateUtil.formatDate(new Date(chatMessage.getTime() * 1000), DateUtil.FormatType.yyyyMMddHHmmss));
            this.setMessage(chatMessage.getTextContent());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
