package com.hcs.commondemo.viewmodel;

import android.app.Activity;
import android.view.SurfaceView;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.record.MediaRecord;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.ToastUtil;
import com.hcs.commondemo.R;
import com.hcs.commondemo.config.Config;
import com.hcs.commondemo.entity.RecordBo;

import java.io.File;

public class RecordViewModel {

    /**
     * 只录音频
     */
    public static final int RECORD_AUDIO_ONLY = 0;

    /**
     * 录制音频和视频
     */
    public static final int RECORD_AUDIO_AND_VIDEO = 1;

    private RecordBo recordBo;
    private MediaRecord mMediaRecord;
    public void setRecordBo(RecordBo recordBo){
        this.recordBo = recordBo;
    }
    public RecordBo getRecordBo(){
        return recordBo;
    }

    public RecordViewModel() {
        recordBo = new RecordBo();
        recordBo.setName("1.mp3");
    }

    /**
     * 录制音视频
     * @param type 录制模式，0音频频，1音视频
     */
    public void recordVideo(Activity activity, SurfaceView surfaceView, int type){
        if(mMediaRecord != null){
            ToastUtil.showToast(BaseApplication.getAppContext().getResources().getString(R.string.record_notice_stop_first));
            return;
        }
        if(StringUtil.isEmpty(recordBo.getName())){
            ToastUtil.showToast(BaseApplication.getAppContext().getResources().getString(R.string.record_notice_can_not_empty));
            return;
        }
        String filePath = Config.getRecordPath(BaseApplication.getAppContext()) + "/" + recordBo.getName() + Config.RECORD_EXTENSION_NAME;
        mMediaRecord = new MediaRecord();
        if(type == RECORD_AUDIO_AND_VIDEO) {
            mMediaRecord.recordVideo(activity,surfaceView, Config.getRecordPath(BaseApplication.getAppContext()) + "/" + recordBo.getName() + Config.RECORD_EXTENSION_NAME, 176, 144, 20);
        }else{
            mMediaRecord.recordAudio(activity,filePath);
        }
    }

    /**
     * 预览视频
     */
    public void prepareVideo(Activity activity, SurfaceView surfaceView){
        if(mMediaRecord != null){
            ToastUtil.showToast(BaseApplication.getAppContext().getResources().getString(R.string.record_notice_stop_first));
            return;
        }
        mMediaRecord = new MediaRecord();
        mMediaRecord.openCamera(activity,surfaceView);
    }

    public void stopRecord(){
        if(mMediaRecord != null){
            mMediaRecord.stopRecording();
            mMediaRecord = null;
        }
    }
}

