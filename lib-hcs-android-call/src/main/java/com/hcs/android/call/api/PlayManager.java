package com.hcs.android.call.api;

import android.view.SurfaceView;

import org.linphone.core.Core;
import org.linphone.core.Player;

/**
 * 播放管理器
 */
public class PlayManager {
    /**
     * 播放器
     */
    private Player mPlayer;
    private IPlayListener mPlayListener;

    public void setPlayListener(IPlayListener playerListener){
        mPlayListener = playerListener;
    }

    /**
     * 文件播放
     * @param filePath 文件路径
     * @param surfaceView 用于显示视频的view，无视频的话可以为null
     */
    public void startPlay(String filePath, SurfaceView surfaceView){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            Core lc = LinphoneManager.getLc();
            if (lc == null) {
                return;
            }
            if (mPlayer == null) {

                mPlayer = lc.createLocalPlayer(null, null, null);
                mPlayer.addListener(player -> {
                    LinphoneManager.getInstance().routeAudioToReceiver();
                    mPlayer.close();
                    mPlayer = null;
                    if (mPlayListener != null) {
                        mPlayListener.onEofReached();
                    }
                });
            } else {
                if (mPlayer.getState() == Player.State.Playing) {
                    return;
                }
            }
            LinphoneManager.getInstance().routeAudioToSpeaker();

            mPlayer.open(filePath);
            mPlayer.start();
        }
    }

    /**
     * 停止播放
     */
    public void stopPlay(){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            if (mPlayer != null) {
                mPlayer.close();
            }
        }
    }

    /**
     * 暂停
     */
    public void pause(){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            if (mPlayer != null && mPlayer.getState() == Player.State.Playing) {
                mPlayer.pause();
            }
        }
    }

    /**
     * 恢复
     */
    public void resume(){
        synchronized (LinphoneManager.getInstance().getSynObj()) {
            if (mPlayer != null && mPlayer.getState() == Player.State.Paused) {
                mPlayer.start();
            }
        }
    }

    /**
     * 是否正在播放中
     */
    public boolean isPlaying(){
        if(mPlayer != null){
            return mPlayer.getState() == Player.State.Playing;
        }
        return false;
    }
    /**
     * 自定义播放监听器
     */
    public interface IPlayListener{
        /**
         * 播放达到指定位置
         */
        void onEofReached();
    }
}
