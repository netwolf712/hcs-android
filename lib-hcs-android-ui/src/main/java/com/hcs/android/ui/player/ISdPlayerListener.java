package com.hcs.android.ui.player;

/**
 * 播放器的一些回调操作
 */
public interface ISdPlayerListener {
    /**
     * 给视图界面的回调
     */
    interface OnViewEvent{
        /**
         * 全屏缩放
         * @param fullScreen 是否全屏
         */
        void onFullScreen(boolean fullScreen);
    }

    /**
     * 播放器状态改变事件
     */
    interface OnPlayerStatusChange{

        /**
         * 播放器初始化完成
         */
        void onPlayerReady(ISdPlayer sdPlayer);

        /**
         * 播放器正在开始播放
         * 在开启START_ON_PREPARED才会被触发
         * 用户主动调用start时不会被触发
         */
        void onPlayerStartPlay(ISdPlayer sdPlayer);

        /**
         * 播放器播放完成
         */
        void onPlayerCompletion(ISdPlayer sdPlayer);

        /**
         * 定位到指定位置开始播放时触发
         */
        void onSeekCompleted(ISdPlayer sdPlayer);
    }
}
