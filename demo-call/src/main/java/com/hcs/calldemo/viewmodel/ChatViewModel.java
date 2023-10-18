package com.hcs.calldemo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hcs.android.call.LinphoneService;
import com.hcs.android.call.api.ChatHelper;
import com.hcs.android.call.api.IChatMessageListener;
import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.RobustTimer;
import com.hcs.android.common.util.RobustTimerTask;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.ToastUtil;
import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;
import com.hcs.android.ui.util.UIThreadUtil;
import com.hcs.calldemo.R;
import com.hcs.calldemo.entity.ChatMessageBo;
import com.hcs.calldemo.factory.CommonViewModel;

import org.linphone.core.ChatMessage;
import org.linphone.core.ChatRoom;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends BaseRefreshViewModel<ChatMessageBo, CommonViewModel> implements IChatMessageListener {
    private RobustTimer mTimer;
    private boolean mWaiting = false;

    private ChatRoom mCurrentChatRoom = null;
    /**
     * 远端号码
     */
    private String remoteNumber;
    public String getRemoteNumber(){
        return remoteNumber;
    }
    public void setRemoteNumber(String remoteNumber){
        this.remoteNumber = remoteNumber;
    }

    /**
     * 要发送的消息
     */
    private String message;
    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }

    public ChatViewModel(@NonNull Application application, CommonViewModel model) {
        super(application, model);
        mTimer = new RobustTimer(true);
        remoteNumber = "";
        message = "";
    }

    private List<ChatMessageBo> convertChatMessageList(ChatMessage[] chatMessages){
        List<ChatMessageBo>  chatMessageList = new ArrayList<>();
        if (chatMessages != null && chatMessages.length > 0) {
            for (ChatMessage chatMessage : chatMessages) {
                ChatMessageBo chatMessageBo = new ChatMessageBo(chatMessage);
                chatMessageList.add(chatMessageBo);
            }
        }
        return chatMessageList;
    }
    @Override
    public void refreshData() {
        postShowNoDataViewEvent(true);
        if(!LinphoneService.isReady()){
            waitLinphoneReady();
            return;
        }
        ChatHelper.setChatListener(this);
        postShowNoDataViewEvent(false);
        mList.clear();

        if(StringUtil.isEmpty(remoteNumber)){
           //如果为空则获取所有聊天室的内容
            ChatRoom[] chatRooms = ChatHelper.getChatRooms();
            if(chatRooms != null && chatRooms.length > 0){
                for(ChatRoom chatRoom : chatRooms){
                    int len = chatRoom.getHistorySize();
                    if(len > 0){
                        ChatMessage[] messages = chatRoom.getHistoryRange(0,len);
                        mList.addAll(convertChatMessageList(messages));
                    }
                }
            }
        }else {
            int messageLen = ChatHelper.getChatHistoryCount(remoteNumber);
            if (messageLen > 0) {
                ChatMessage[] chatMessages = ChatHelper.getChatHistory(remoteNumber, 0, messageLen);
                mList.addAll(convertChatMessageList(chatMessages));
            }
        }
        postStopRefreshEvent();
    }

    @Override
    public void loadMore() {
        refreshData();
    }

    private void waitLinphoneReady(){
        if(mWaiting){
            return;
        }
        RobustTimerTask timerTask = new RobustTimerTask() {
            @Override
            public void run() {
                if(!LinphoneService.isReady()){
                    return;
                }
                mWaiting = false;
                mTimer.cancel();
                UIThreadUtil.runOnUiThread(()->{
                    refreshData();
                });
            }
        };
        mWaiting = true;
        mTimer.schedule(timerTask, 30, 30);
    }

    public void setMessage(){
        if(StringUtil.isEmpty(remoteNumber)){
            ToastUtil.showToast(BaseApplication.getAppContext().getResources().getString(R.string.chat_notice_remote_number_can_not_empty));
            return;
        }
        if(StringUtil.isEmpty(message)){
            ToastUtil.showToast(BaseApplication.getAppContext().getResources().getString(R.string.chat_notice_message_can_not_empty));
            return;
        }
        if((mCurrentChatRoom = ChatHelper.sendMessage(remoteNumber,message)) == null){
            ToastUtil.showToast(BaseApplication.getAppContext().getResources().getString(R.string.chat_notice_remote_number_not_existed));
        }else{
            ToastUtil.showToast(BaseApplication.getAppContext().getResources().getString(R.string.chat_notice_send_success));
            refreshData();
        }
    }

    /**
     * 收到聊天信息
     * @param chatRoom 聊天室
     * @param chatMessage 聊天内容
     */
    @Override
    public void onChatMessageReceived(ChatRoom chatRoom, ChatMessage chatMessage){
        ChatMessageBo chatMessageBo = new ChatMessageBo(chatMessage);
        mList.add(chatMessageBo);
    }
}
