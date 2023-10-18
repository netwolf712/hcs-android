package com.hcs.android.business.manager;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.constant.StateEnum;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.FileUtil;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.ui.player.ISdPlayer;
import com.hcs.android.ui.player.ISdPlayerListener;
import com.hcs.android.ui.player.SimplePlayer;

import java.io.File;


/**
 * 回铃音管理助手
 */
public class RingBackToneHelper {
    /**
     * 默认的回铃音文件名称
     */
    private final String DEFAULT_RING_BACK_TONE_FILE_NAME = "notes_of_the_optimistic.mkv";
    /**
     * 播放管理器
     */
    private final SimplePlayer mPlayManager;

    private final Context mContext;

    private RingBackToneHelper(){
        mContext = BusinessApplication.getAppContext();
        //拷贝默认的回铃音
        loadDefaultRingBackTone();
        mPlayManager = new SimplePlayer(mContext);
        //循环播放
        mPlayManager.setOnPlayerStatusChange(new ISdPlayerListener.OnPlayerStatusChange() {
            @Override
            public void onPlayerReady(ISdPlayer iSdPlayer) {

            }

            @Override
            public void onPlayerStartPlay(ISdPlayer iSdPlayer) {

            }

            @Override
            public void onPlayerCompletion(ISdPlayer iSdPlayer) {
                playRingBackTone();
            }

            @Override
            public void onSeekCompleted(ISdPlayer iSdPlayer) {

            }
        });
    }

    private static final class MInstanceHolder {
        @SuppressLint("StaticFieldLeak")
        static final RingBackToneHelper mInstance = new RingBackToneHelper();
    }

    public static RingBackToneHelper getInstance(){
        return RingBackToneHelper.MInstanceHolder.mInstance;
    }

    /**
     * 获取提示音路径
     */
    public String getRingBackTonePath(){
        String filePath = SettingsHelper.getInstance(mContext).getString(PreferenceConstant.RING_BACK_TONE_PATH,mContext.getString(R.string.default_ring_back_tone_path));
        File file = new File(filePath);
        if(file.exists() && file.length() > 0){
            return filePath;
        }
        return getDefaultRingBackTonePath();
    }

    /**
     * 设置提示音路径
     */
    public void setRingBackTonePath(String path){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.RING_BACK_TONE_PATH,path);
    }

    /**
     * 播放回铃音
     */
    public void playRingBackTone(){
        String audioPath = getRingBackTonePath();
        if(!StringUtil.isEmpty(audioPath) && FileUtil.getFileLength(new File(audioPath)) > 0){
            KLog.i("play ring back tone ==> " + audioPath);
            mPlayManager.startSdPlayer(audioPath);
        }else{
            KLog.w("can't find ring back tone file " + audioPath + ",play failed");
        }
    }

    /**
     * 停止播放回铃音
     */
    public void stopRingBackTone(){
        mPlayManager.stop();
    }

    /**
     * 设置回铃音播放的观察对象
     */
    public void setPlayObserver(DeviceModel deviceModel){
        deviceModel.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if(propertyId == BR.state){
                    if(deviceModel.getState() != null && deviceModel.getState() == StateEnum.CALLING){
                        if(deviceModel.getCallModel() != null && StringUtil.equalsIgnoreCase(deviceModel.getDevice().getDeviceId(),deviceModel.getCallModel().getCallerDeviceId())){
                            //正在呼叫中，且该设备是主叫，则播放回铃音
                            playRingBackTone();
                        }
                    }else{
                        //否则停止播放回铃音
                        stopRingBackTone();
                    }
                }
            }
        });
    }

    /**
     * 获取默认的回铃音路径
     */
    @NonNull
    private String getDefaultRingBackTonePath(){
        return mContext.getFilesDir().getAbsolutePath() + "/" + DEFAULT_RING_BACK_TONE_FILE_NAME;
    }
    /**
     * 拷贝默认的回铃音
     */
    public void loadDefaultRingBackTone(){
        String filePath = getDefaultRingBackTonePath();
        try {
            FileUtil.copyIfNotExist(BusinessApplication.getAppContext(), R.raw.notes_of_the_optimistic, filePath);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
