package com.hcs.android.call.api;

import org.linphone.core.Call;
import org.linphone.core.Core;

public class CallEntity {
    /**
     * sdk核心
     */
    private Core lc;

    /**
     * 呼叫对象
     */
    private Call call;

    /**
     * 呼叫状态
     */
    private Call.State state;

    /**
     * 消息内容
     */
    private String message;

    public CallEntity(Core lc,Call call,Call.State state,String message){
        this.lc = lc;
        this.call = call;
        this.state = state;
        this.message = message;
    }
    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public Call.State getState() {
        return state;
    }

    public void setState(Call.State state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Core getLc() {
        return lc;
    }

    public void setLc(Core lc) {
        this.lc = lc;
    }
}
