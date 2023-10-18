package com.hcs.commondemo.entity;

import android.media.MediaMetadataRetriever;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.hcs.android.common.util.DateUtil;
import com.hcs.android.common.util.FileUtil;
import com.hcs.commondemo.BR;

import java.io.File;
import java.util.Date;

/**
 * 录音录像相关对象
 */
public class RecordBo extends BaseObservable {
    /**
     * 文件路径
     */
    private String filePath;
    public String getFilePath(){
        return filePath;
    }
    public void setFilePath(String filePath){
        this.filePath = filePath;
    }

    /**
     * 文件名称
     */
    private String name;
    @Bindable
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    /**
     * 录制时长
     */
    private String duration;
    public String getDuration(){
        return duration;
    }
    public void setDuration(String duration){
        this.duration = duration;
    }

    /**
     * 开始时间
     */
    private String startTime;
    public String getStartTime(){
        return startTime;
    }
    public void setStartTime(String startTime){
        this.startTime = startTime;
    }

    public void analyzeFile(String filePath){
        this.filePath = filePath;
        this.name = FileUtil.getNameFromFilePath(filePath);

        if(FileUtil.isVideoFile(filePath)) {
            MediaMetadataRetriever retr = new MediaMetadataRetriever();
            retr.setDataSource(filePath);
            this.duration = DateUtil.formatSecondToHourMinuteSecond(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION) == null ? 0L : Long.valueOf(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) / 1000);
        }
        File file = new File(filePath);
        this.startTime = DateUtil.formatDate(new Date(file.lastModified()),DateUtil.FormatType.yyyyMMddHHmmss);
    }
}
