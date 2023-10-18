package com.hcs.android.business.manager;

import static android.media.AudioManager.STREAM_RING;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableArrayList;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.business.constant.CallTypeEnum;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.constant.StateEnum;
import com.hcs.android.business.constant.TtsModeEnum;
import com.hcs.android.business.constant.TtsSpeedEnum;
import com.hcs.android.business.entity.CallModel;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.util.PatientUtil;
import com.hcs.android.business.util.PlaceUtil;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.FileUtil;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.TTSUtil;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.ui.player.ISdPlayer;
import com.hcs.android.ui.player.ISdPlayerListener;
import com.hcs.android.ui.player.SimplePlayer;

import java.io.File;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 呼入管理器
 */
public class CallInManager {
    /**
     * 默认的振铃提示音文件名称
     */
    private final String DEFAULT_RING_TONE_FILE_NAME = "leaving_dreams.mkv";
    /**
     * 呼入对象
     */
    private final ObservableArrayList<CallModel> mCallInModels = new ObservableArrayList<>();
    /**
     * 等待发送TTS的对象
     */
    private final Deque<CallModel> mWaitTtsModels = new LinkedList();

    /**
     * 正在通话中的对象
     */
    private CallModel mTalkingModel = null;

    /**
     * 播放管理器
     */
    private final SimplePlayer mPlayManager;

    private final Context mContext;

    /**
     * 呼叫对象改变监听器
     */
    private CallModelChangeListener mCallModelChangeListener;

    private CallInManager(){
        mContext = BusinessApplication.getAppContext();
        loadDefaultRingTone();
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
                playRingTone();
            }

            @Override
            public void onSeekCompleted(ISdPlayer iSdPlayer) {

            }
        });
        TTSUtil.getInstance(mContext).setSpeechFinishListener(str->{
            setTts();
        });
    }

    private static final class MInstanceHolder {
        @SuppressLint("StaticFieldLeak")
        static final CallInManager mInstance = new CallInManager();
    }

    public static CallInManager getInstance(){
        return MInstanceHolder.mInstance;
    }

    /**
     * 设置TTS内容
     */
    private void setTts(){
        synchronized (mWaitTtsModels) {
            //设置音频的数据流为铃声
            AudioManager audioManager = ((AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE));
            audioManager.requestAudioFocus(
                    null, STREAM_RING, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
            //此时可以重新开启tts了
            if (!mWaitTtsModels.isEmpty() && !TTSUtil.getInstance(mContext).isSpeaking()) {
                CallModel tmpModel = mWaitTtsModels.pollFirst();
                if(tmpModel != null) {
                    String txt = getTtsText(tmpModel);
                    if(!StringUtil.isEmpty(txt)) {
                        TTSUtil.getInstance(mContext).speakText(txt);
                    }
                }
            }else if (mWaitTtsModels.isEmpty()
                    && !TTSUtil.getInstance(mContext).isSpeaking()
                    && !mCallInModels.isEmpty()){
                //如果tts队列播放完毕，但呼入队列里还有对象，则播放铃声
                playRingTone();
            }
        }
    }
    /**
     * 判断是否为呼入
     */
    public boolean isCallIn(@NonNull CallModel callModel){
        DeviceModel deviceModel = WorkManager.getInstance().getSelfInfo();
        return !StringUtil.equalsIgnoreCase(callModel.getCallerDeviceId(), deviceModel.getDevice().getDeviceId());
    }

    /**
     * 将呼叫对象加入tts播放队列
     */
    private void addToWaitModels(@NonNull CallModel callModel){
        if (!mWaitTtsModels.contains(callModel)) {
            //是否紧急呼叫，是则插入队头，否则插入队尾
            CallTypeEnum typeEnum = CallTypeEnum.findById(callModel.getCallType());
            if (typeEnum == CallTypeEnum.BLUE_CODE || typeEnum == CallTypeEnum.EMERGENCY) {
                mWaitTtsModels.addFirst(callModel);
            } else {
                mWaitTtsModels.addLast(callModel);
            }
        }
    }

    /**
     * 播放振铃音
     */
    private void playRingTone(){
        String audioPath = getRingTonePath();
        if(!StringUtil.isEmpty(audioPath) && FileUtil.getFileLength(new File(audioPath)) > 0){
            KLog.i("play ring tone ==> " + audioPath);
            mPlayManager.startSdPlayer(audioPath);
        }else{
            KLog.w("can't find ring tone file " + audioPath + ",play failed");
        }
    }
    @Nullable
    private CallModel findInList(List<CallModel> callModelList, CallModel callModel){
        if(StringUtil.isEmpty(callModelList)){
            return null;
        }
        for(CallModel tmp : callModelList){
            if(StringUtil.equalsIgnoreCase(tmp.getCallRef(),callModel.getCallRef())){
                return tmp;
            }
        }
        return null;
    }

    /**
     * 通过主叫id删除呼叫队列
     * @param callerDeviceId 主机id
     * @param callTypeEnum 需要清除的呼叫类型，null表示清除所有
     */
    public void removeCallInModelByCaller(String callerDeviceId,CallTypeEnum callTypeEnum){
        synchronized (mCallInModels){
            Iterator<CallModel> it = mCallInModels.iterator();
            while (it.hasNext()){
                //此处不能用for循环，因为会删除队列成员，得用迭代器，防止索引改变导致循环出错
                CallModel callModel = it.next();
                if(StringUtil.equalsIgnoreCase(callerDeviceId,callModel.getCallerDeviceId())){
                    if(callTypeEnum != null){
                        if(Objects.equals(callModel.getCallType(),callTypeEnum.getValue())){
                            //将此呼叫置为呼叫结束
                            callModel.setState(StateEnum.CALL_END.getValue());
                            updateCallState(callModel);
                        }
                    }else{
                        //将此呼叫置为呼叫结束
                        callModel.setState(StateEnum.CALL_END.getValue());
                        updateCallState(callModel);
                    }
                }
            }
        }
    }
    /**
     * 更新呼叫对象
     */
    public void updateCallState(@NonNull CallModel callModel){
        if(callModel.getState() == StateEnum.CALLING.getValue() && isCallIn(callModel)){
            //正在呼入
            synchronized (mCallInModels){
                if(findInList(mCallInModels,callModel) == null){
                    mCallInModels.add(callModel);
                    if(mCallModelChangeListener != null){
                        mCallModelChangeListener.onAddCallModel(callModel);
                    }
                }else{
                    //已经存在了，不重复处理
                    return;
                }
            }
            synchronized (mWaitTtsModels){
                //当前是否有通话中的对象
                if(mTalkingModel != null){
                    //如果有则先加入播放等待队列，啥事不干
                    addToWaitModels(callModel);
                    return;
                }
                String ttsTxt = getTtsText(callModel);
                //如果tts字符串为空
                if(StringUtil.isEmpty(ttsTxt)){
                    if(!mPlayManager.isPlaying()){
                        //播放呼入提示音
                        playRingTone();
                    }
                    return;
                }
                //如果有tts提示
                //先检查是否在播放铃声，有则打断振铃
                if(mPlayManager.isPlaying()){
                    mPlayManager.stop();
                }
                //如果当前没在通话，且tts队列为空，则直接播放tts
                if (mWaitTtsModels.isEmpty() && !TTSUtil.getInstance(mContext).isSpeaking()){
                    TTSUtil.getInstance(mContext).speakText(ttsTxt);
                }else {
                    addToWaitModels(callModel);
                }
            }
        }else{
            if(callModel.getState() == StateEnum.TALKING.getValue()){
                //有正在通话中的，停止TTS
                mTalkingModel = callModel;
                TTSUtil.getInstance(mContext).stop();
                mPlayManager.stop();
            }else{
                if(mTalkingModel != null && mTalkingModel.equals(callModel)){
                    //如果是这个通话中的模块，且当前状态不是通话了，则给它置null
                    mTalkingModel = null;
                    setTts();
                }
            }
            //如果不是呼入状态，则需判断是否存在于当前队列，是则弹出
            synchronized (mCallInModels){
                CallModel tmp = findInList(mCallInModels,callModel);
                if(tmp != null){
                    mCallInModels.remove(tmp);
                    if(mCallModelChangeListener != null){
                        mCallModelChangeListener.onRemoveCallModel(tmp);
                    }
                    if(mCallInModels.size() == 0){
                        mPlayManager.stop();
                    }
                }
            }
            synchronized (mWaitTtsModels){
                for(CallModel tmp : mWaitTtsModels){
                    if(StringUtil.equalsIgnoreCase(tmp.getRemoteDevice().getDevice().getDeviceId(),callModel.getRemoteDevice().getDevice().getDeviceId())){
                        mWaitTtsModels.remove(tmp);
                        break;
                    }
                }
            }
        }
    }
    /**
     * 获取提示音路径
     */
    public String getRingTonePath(){
        String filePath = SettingsHelper.getInstance(mContext).getString(PreferenceConstant.RING_TONE_PATH,mContext.getString(R.string.default_ring_tone_path));
        File file = new File(filePath);
        if(file.exists() && file.length() > 0){
            return filePath;
        }
        return getDefaultRingTonePath();
    }

    /**
     * 设置提示音路径
     */
    public void setRingTonePath(String path){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.RING_TONE_PATH,path);
    }


    /**
     * 获取tts播放模式
     */
    public int getTtsMode(){
        return SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.TTS_MODE,mContext.getResources().getInteger(R.integer.default_tts_mode));
    }

    /**
     * 设置tts播放模式
     */
    public void setTtsMode(int mode){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.TTS_MODE,mode);
    }


    /**
     * 设置TTS语速
     * @param speed TtsSpeedEnum对应的value
     */
    public void setTtsSpeed(int speed){
        TTSUtil.getInstance(mContext).setSpeedRate(TtsSpeedEnum.findById(speed).getValue());
    }

    /**
     * 获取TTS语速
     * @return TtsSpeedEnum对应的value
     */
    public int getTtsSpeed(){
        TtsSpeedEnum speedEnum = TtsSpeedEnum.parseSpeed(TTSUtil.getInstance(mContext).getSpeedRate());
        return speedEnum.getValue();
    }



    /**
     * 获取tts字符串内容
     */
    public String getTtsText(@NonNull CallModel callModel){
        PlaceModel placeModel = callModel.getRemotePlace();
        if(placeModel == null){
            return "";
        }
        String callingStr = mContext.getString(R.string.TTS_CALLING);
        TtsModeEnum ttsModeEnum = TtsModeEnum.findById(getTtsMode());
        String title = "";
        switch (ttsModeEnum){
            case NONE:
                return "";
            case PLACE_NAME:
                title = PlaceUtil.getDisplayNo(placeModel);
                break;
            case PATIENT_NAME:
                title = PatientUtil.getPatientFullName(PlaceUtil.getFirstPatient(placeModel));
                break;
            case PHONE_NUMBER:
                title = mContext.getString(R.string.TTS_PHONE) + callModel.getCaller();
                break;

        }
        CallTypeEnum callTypeEnum = CallTypeEnum.findById(callModel.getCallType());
        return title +
                (callTypeEnum == CallTypeEnum.NORMAL ? callingStr : callTypeEnum.getDisplayName(BusinessApplication.getAppContext())) +
                (StringUtil.isEmpty(callModel.getCause()) ? "" : callModel.getCause());
    }

    public ObservableArrayList<CallModel> getCallInModels(){
        return mCallInModels;
    }

    /**
     * 获取默认的铃声路径
     */
    @NonNull
    private String getDefaultRingTonePath(){
        return mContext.getFilesDir().getAbsolutePath() + "/" + DEFAULT_RING_TONE_FILE_NAME;
    }
    /**
     * 拷贝默认的铃声
     */
    public void loadDefaultRingTone(){
        String filePath = getDefaultRingTonePath();
        try {
            FileUtil.copyIfNotExist(BusinessApplication.getAppContext(), R.raw.leaving_dreams, filePath);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setCallModelChangeListener(CallModelChangeListener callModelChangeListener){
        mCallModelChangeListener = callModelChangeListener;
    }
    /**
     * 呼叫对象改变监听器
     */
    public interface CallModelChangeListener{
        /**
         * 添加呼叫对象
         */
        void onAddCallModel(@NonNull CallModel callModel);

        /**
         * 删除呼叫对象
         */
        void onRemoveCallModel(@NonNull CallModel callModel);
    }
}
