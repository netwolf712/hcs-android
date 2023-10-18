package com.hcs.android.ui.player;

import android.view.Surface;

/**
 * 自定义播放器的基础接口
 * 嫌GSYVideoPlayer封装得太繁琐，结果自己也越封装越复杂了（小丑竟是我自己=。=！）
 */
public interface ISdPlayer {
    /**
     * 开始播放
     * 暂停、停止状态下还是需要调用它的
     */
    void start();

    /**
     * 获取视频的实际宽
     * @return 宽度
     */
    int getVideoWidth();

    /**
     * 获取视频的实际高
     * @return 高度
     */
    int getVideoHeight();

    /**
     * 暂停
     */
    void pause();

    /**
     * 重置
     */
     void reset();

    /**
     * 释放视频源
     */
    void release();

    /**
     * 没有stop？
     * 没错，真没有
     */

    /**
     * 开始播放
     * @param filePath 文件路径
     */
    void startPlay(String filePath);

    /**
     * 重新开始播放
     * @param filePath 文件路径
     */
    void restart(String filePath);

    /**
     * 添加视频铺设对象
     * @param surface 接收视频视频的对象
     */
    void setSurface(Surface surface);

    /**
     * 获取播放器当前状态
     * @return 自定义的简易状态
     */
    PlayStatusEnum getCurrentStatus();

    /**
     * 添加播放器状态监听器
     * @param onPlayerStatusChange
     */
    void setOnPlayerStatusChange(ISdPlayerListener.OnPlayerStatusChange onPlayerStatusChange);
}
