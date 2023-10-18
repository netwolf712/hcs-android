package com.hcs.android.business.request.viewmodel;


import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.Constant;
import com.hcs.android.business.entity.HandoverLog;
import com.hcs.android.business.service.HandoverLogService;
import com.hcs.android.common.record.MediaRecord;
import com.hcs.android.common.util.DateUtil;
import com.hcs.android.common.util.FileUtil;

import org.simple.eventbus.EventBus;

import java.util.Date;


/**
 * 交班详情
 */
public class HandoverLogDetailViewModel {
    /**
     * 视频录制的分辨率
     * 设置720*480 25fps无法正确使用摄像头
     */
    private final static int VIDEO_RATION_WIDTH = 176;
    private final static int VIDEO_RATION_HEIGHT = 144;
    /**
     * 视频录制的刷新率
     */
    private final static int VIDEO_FRQ = 20;

    private final Object mSynObj = new Object();
    private HandoverLog mHandoverLog;

    /**
     * 音频文件的后缀名
     */
    public final static String AUDIO_SUFFIX = ".wav";
    /**
     * 视频文件的后缀名
     */
    public final static String VIDEO_SUFFIX = ".mp4";


    public void setHandoverLog(HandoverLog handoverLog){
        this.mHandoverLog = mHandoverLog;
    }
    public HandoverLog getHandoverLog(){
        return mHandoverLog;
    }

    /**
     * 音视频录制控件
     */
    private MediaRecord mMediaRecord;
    private boolean mIsRecording = false;

    public HandoverLogDetailViewModel(){
        EventBus.getDefault().register(this);
        this.mHandoverLog = new HandoverLog();
        mMediaRecord = new MediaRecord();
    }

    /**
     * 获取文件目录
     */
    public String getRecordFileDir(){
        return FileUtil.getAppFileDir() + "/handoverLog/"
                + DateUtil.formatDate(new Date(),DateUtil.FormatType.yyyyMMdd);
    }

    /**
     * 获取录制文件的存放目录
     */
    @NonNull
    public String getRecordFilePath(int type){
        return getRecordFileDir()
                + "/" + DateUtil.formatDate(new Date(),DateUtil.FormatType.yyyyMMdd_HHmmss)
                + (type == Constant.APPEND_FILE_AUDIO ? AUDIO_SUFFIX : VIDEO_SUFFIX) ;
    }


    /**
     * 保存录制的文件
     * @param filePath 文件路径
     * @param userName 创建者姓名
     * @param description 备注
     * @param type 文件类型，视频、音频
     */
    public void saveRecord(String filePath,String userName,String description,int type){
        mHandoverLog.setUpdateTime(System.currentTimeMillis());
        mHandoverLog.setAppendPath(filePath);
        mHandoverLog.setType(type);
        mHandoverLog.setUserName(userName);
        mHandoverLog.setDescription(description);
        mHandoverLog.setState(Constant.MESSAGE_UNREAD);
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        retr.setDataSource(filePath);
        mHandoverLog.setDuration(Integer.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        HandoverLogService.getInstance().updateHandoverLog(mHandoverLog);
    }
    /**
     * 录制视频
     * @param activity activity
     * @param surfaceView 显示预览的view
     */
    public void startRecordVideo(Activity activity, SurfaceView surfaceView,String filePath) {
        synchronized (mSynObj) {
            if (mIsRecording) {
                return;
            }
            mMediaRecord.recordVideo(activity, surfaceView, filePath, VIDEO_RATION_WIDTH, VIDEO_RATION_HEIGHT, VIDEO_FRQ);
            mIsRecording = true;
        }
    }

    /**
     * 打开视频预览
     */
    public void prepareVideo(Activity activity, SurfaceView surfaceView){
        synchronized (mSynObj){
            mMediaRecord.openCamera(activity,surfaceView);
        }
    }

    public void stopRecord(){
        synchronized (mSynObj) {
            if (mIsRecording) {
                mMediaRecord.stopRecording();
                mIsRecording = false;
            }
        }
    }
}
