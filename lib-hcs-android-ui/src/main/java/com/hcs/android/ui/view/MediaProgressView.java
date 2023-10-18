package com.hcs.android.ui.view;

import android.content.Context;
import android.view.ViewGroup;

import com.hcs.android.ui.player.SimplePlayer;

/**
 * 简易的进度条
 * 先用进度条做一个最简单的能量变化条，哈哈
 */
public class MediaProgressView extends AbstractSimpleProgressView {


    private SimplePlayer mSimplePlayer;

    public MediaProgressView(Context context, ViewGroup viewGroup){
        super(context, viewGroup);
    }

    public void setSimplePlayer(SimplePlayer simplePlayer){
        mSimplePlayer = simplePlayer;
    }
    /**
     * 获取进度条的最大值
     * @return 最大值
     */
    public int getMaxValue(){
        if(mSimplePlayer != null){
            return Long.valueOf(mSimplePlayer.getDuration()).intValue();
        }else {
            return 0;
        }
    }

    /**
     * 获取进度条的当前值
     * @return 当前值
     */
    public int getCurrentValue(){
        if(mSimplePlayer != null){
            return Long.valueOf(mSimplePlayer.getCurrentPosition()).intValue();
        }else {
            return 0;
        }
    }

    /**
     * 获取定时器刷新周期，单位毫秒
     * 这里写死没500ms刷新一次
     * @return 刷新周期
     */
    public int getRefreshPeriod(){
        return 500;
    }
}
