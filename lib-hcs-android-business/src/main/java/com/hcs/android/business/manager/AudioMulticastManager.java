package com.hcs.android.business.manager;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.PlayTypeEnum;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.entity.MulticastGroupModel;
import com.hcs.android.business.util.GroupUtil;
import com.hcs.android.call.api.IMulticastPlayListener;
import com.hcs.android.call.api.MulticastHelper;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.ISimpleCustomer;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.ui.util.UIThreadUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 音频广播管理器
 */
public class AudioMulticastManager implements IMulticastPlayListener {

    private final Context mContext;

    /**
     * 只播放一次
     */
    private final static int PLAY_ONCE = -1;
    /**
     * 单曲循环
     */
    private final static int SINGLE_LOOP_FOREVER = 0;


    /**
     * 正在播放的分组列表
     */
    private final Map<Long, MulticastGroupModel> mPlayingMap = new HashMap<>();

    /**
     * 播放结束监听器
     */
    private ISimpleCustomer<MulticastGroupModel> mPlayFinishListener;

    /**
     * 全区组播
     */
    private MulticastGroupModel mGlobalMulticastGroupModel;

    /**
     * 是否主机
     * 主机是发送广播
     * 分机是接收广播
     */
    private final boolean mIsMasterDevice;


    /**
     * 是否已经准备好
     */
    private boolean mReady;
    public void setReady(){
        mReady = true;
        setMulticastPlayListener();
    }
    public boolean isReady(){
        return mReady;
    }

    private AudioMulticastManager(){
        mContext = BusinessApplication.getAppContext();
        mIsMasterDevice = DeviceFactory.getInstance().getDeviceType(mContext) == DeviceTypeEnum.NURSE_STATION_MASTER;
    }

    /**
     * 注册广播监听器
     * 因为是linphone接口，所以得linphone启动完成后才能调用
     */
    private void setMulticastPlayListener(){
        MulticastHelper.setMulticastPlayerListener(this);
    }
    /**
     * 初始化全局广播对象
     * @param masterPhoneNo 主机号（对主机来说就是主机的号码，对分机来说就是直属数据号）
     */
    public void initGlobalAudioMulticast(String masterPhoneNo){
        int globalGroupSn = MulticastGroupModel.WHOLE_GROUP;
        mGlobalMulticastGroupModel = new MulticastGroupModel(globalGroupSn
                ,String.valueOf(globalGroupSn)
                , SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_AUDIO_MULTICAST_ADDRESS,mContext.getString(R.string.default_audio_multicast_address))
                , GroupUtil.getAudioMulticastPort(Integer.parseInt(masterPhoneNo),globalGroupSn));
        mGlobalMulticastGroupModel.getMulticastGroup().setVolume(getGlobalMulticastVolume());
        mGlobalMulticastGroupModel.getMulticastGroup().setPlayType(getGlobalMulticastPlayType());
        mGlobalMulticastGroupModel.getMulticastGroup().setFileList(convertListToString(getGlobalMulticastFileList()));
    }

    private static final class MInstanceHolder {
        @SuppressLint("StaticFieldLeak")
        static final AudioMulticastManager mInstance = new AudioMulticastManager();
    }

    public static AudioMulticastManager getInstance(){
        return MInstanceHolder.mInstance;
    }

    public void setPlayFinishListener(ISimpleCustomer<MulticastGroupModel> playFinishListener){
        mPlayFinishListener = playFinishListener;
    }

    /**
     * 是否正在播放
     * 其实就是查找此分组是否已经在播放列表里了
     */
    public boolean isPlaying(@NonNull MulticastGroupModel multicastGroupModel){
        synchronized (mPlayingMap){
            for(MulticastGroupModel tmp : mPlayingMap.values()){
                if(StringUtil.equalsIgnoreCase(tmp.getMulticastGroup().getDeviceId(),multicastGroupModel.getMulticastGroup().getDeviceId())
                && tmp.getMulticastGroup().getGroupSn().equals(multicastGroupModel.getMulticastGroup().getGroupSn())){
                    return true;
                }
            }
            return false;
        }
    }
    /**
     * 开启主机的播放逻辑
     * 不用再做参数合法性校验
     */
    private void startMasterAudioMulticast(@NonNull MulticastGroupModel multicastGroupModel){
        List<String> fileList = multicastGroupModel.getPlayFileList();
        if(!StringUtil.isEmpty(fileList)) {
            if(multicastGroupModel.getCurrentPlayIndex() == null || multicastGroupModel.getCurrentPlayIndex() >= fileList.size()){
                multicastGroupModel.setCurrentPlayIndex(0);
            }
            //播放文件列表
            String filePath = fileList.get(multicastGroupModel.getCurrentPlayIndex());
            long streamPtr = MulticastHelper.startAudioMulticast(multicastGroupModel.getSoundCardName(), multicastGroupModel.getMulticastAddress(), multicastGroupModel.getMulticastPort(), filePath, multicastGroupModel.getMulticastGroup().getVolume());
            multicastGroupModel.setStreamPtr(streamPtr);
            mPlayingMap.put(streamPtr, multicastGroupModel);
            //如果只有一个文件，则除了顺序播放外，其它都是单曲循环
            //如果设置的就是单曲循环，那就是单曲循环
            if (isSingleLoopForever(multicastGroupModel)) {
                MulticastHelper.enableMulticastLoop(streamPtr, SINGLE_LOOP_FOREVER);
            } else {
                MulticastHelper.enableMulticastLoop(streamPtr, PLAY_ONCE);
            }
        }
    }

    /**
     * 判断是否单曲循环
     */
    private boolean isSingleLoopForever(@NonNull MulticastGroupModel multicastGroupModel){
        List<String> fileList = multicastGroupModel.getPlayFileList();
        if(StringUtil.isEmpty(fileList)){
            return false;
        }
        return fileList.size() == 1 && multicastGroupModel.getMulticastGroup().getPlayType() != PlayTypeEnum.IN_TURN.getValue()
                || multicastGroupModel.getMulticastGroup().getPlayType() == PlayTypeEnum.LOOP_ONE.getValue();
    }
    /**
     * 开始广播
     */
    public void startAudioMulticast(@NonNull MulticastGroupModel multicastGroupModel){
        synchronized (mPlayingMap) {
            // 先把播放状态置为播放中
            // 先占个座吧，因为此时可能已经到了自动广播音乐的时候，但当前正在进行喊话导致通道被占
            // 先占座的好处就是在喊话结束之后自动开启音乐广播功能
            multicastGroupModel.setPlaying(true);
            if(multicastGroupModel.getStreamPtr() != -1
                    || StringUtil.isEmpty(multicastGroupModel.getMulticastAddress())
                    || multicastGroupModel.getMulticastPort() == null){
                return;
            }
            if (mIsMasterDevice) {
                startMasterAudioMulticast(multicastGroupModel);
            } else {
                Long streamPtr = MulticastHelper.startAudioMulticastListen(multicastGroupModel.getSoundCardName(),multicastGroupModel.getMulticastAddress(), multicastGroupModel.getMulticastPort(), null,multicastGroupModel.getMulticastGroup().getVolume());
                multicastGroupModel.setStreamPtr(streamPtr);
                mPlayingMap.put(streamPtr, multicastGroupModel);
            }
        }
    }

    /**
     * 开始语音广播
     */
    public void startTalkMulticast(@NonNull MulticastGroupModel multicastGroupModel){
        if (!mIsMasterDevice) {
            //只有主机才有语音广播的功能
            return;
        }
        synchronized (mPlayingMap) {
            if(StringUtil.isEmpty(multicastGroupModel.getMulticastAddress())
                    || multicastGroupModel.getMulticastPort() == null){
                return;
            }
            if(multicastGroupModel.getStreamPtr() != -1){
                //如果之前还开着，则先关闭
                MulticastHelper.stopAudioMulticast(multicastGroupModel.getStreamPtr());
                mPlayingMap.remove(multicastGroupModel.getStreamPtr());
            }
            multicastGroupModel.setTalking(true);
            //开启喊话数据流
            long streamPtr = MulticastHelper.startAudioMulticast(multicastGroupModel.getSoundCardName(), multicastGroupModel.getMulticastAddress(), multicastGroupModel.getMulticastPort(), null, multicastGroupModel.getMulticastGroup().getVolume());
            multicastGroupModel.setStreamPtr(streamPtr);
            mPlayingMap.put(streamPtr, multicastGroupModel);
        }
    }
    /**
     * 停止广播
     */
    public void stopAudioMulticast(@NonNull MulticastGroupModel multicastGroupModel){
        synchronized (mPlayingMap){
            //先修改音频广播状态
            multicastGroupModel.setPlaying(false);
            if(multicastGroupModel.getStreamPtr() != -1 && !multicastGroupModel.isTalking()){
                //当前数据流指针存在且没在讲话，则说明确实需要关闭媒体流
                MulticastHelper.stopAudioMulticast(multicastGroupModel.getStreamPtr());
                mPlayingMap.remove(multicastGroupModel.getStreamPtr());
                multicastGroupModel.setStreamPtr(-1);
            }
        }
    }

    /**
     * 停止语音广播
     */
    public void stopTalkMulticast(@NonNull MulticastGroupModel multicastGroupModel){
        synchronized (mPlayingMap){
            if(multicastGroupModel.getStreamPtr() != -1 && multicastGroupModel.isTalking()){
                //媒体流存在且确实是在讲话的，则需先关闭媒体流
                MulticastHelper.stopAudioMulticast(multicastGroupModel.getStreamPtr());
                mPlayingMap.remove(multicastGroupModel.getStreamPtr());
                multicastGroupModel.setTalking(false);
                multicastGroupModel.setStreamPtr(-1);
                if(multicastGroupModel.isPlaying()){
                    //如果音频状态是播放中，则应该重启音频
                    startAudioMulticast(multicastGroupModel);
                }
            }
        }
    }

    /**
     * 停止语音和媒体广播
     */
    public void stopMulticast(@NonNull MulticastGroupModel multicastGroupModel){
        synchronized (mPlayingMap){
            if(multicastGroupModel.getStreamPtr() != -1){
                //媒体流存在且确实是在讲话的，则需先关闭媒体流
                MulticastHelper.stopAudioMulticast(multicastGroupModel.getStreamPtr());
                mPlayingMap.remove(multicastGroupModel.getStreamPtr());
                multicastGroupModel.setTalking(false);
                multicastGroupModel.setStreamPtr(-1);
                multicastGroupModel.setPlaying(false);
            }
        }
    }

    @Override
    public void onPlayFinished(long l) {
        KLog.e("==> audio multicast play finished");
        synchronized (mPlayingMap){
            MulticastGroupModel multicastGroupModel = mPlayingMap.get(l);
            if(multicastGroupModel != null){
                List<String> fileList = multicastGroupModel.getPlayFileList();
                if(StringUtil.isEmpty(fileList)){
                    //如果文件列表为空，则直接退出播放
                    mPlayingMap.remove(l);
                    return;
                }
                if(isSingleLoopForever(multicastGroupModel)){
                    //如果单曲循环，则啥也不用处理
                    return;
                }
                switch (PlayTypeEnum.findById(multicastGroupModel.getMulticastGroup().getPlayType())){
                    case IN_TURN:{//顺序播放，放到最后就停止
                        multicastGroupModel.setCurrentPlayIndex(multicastGroupModel.getCurrentPlayIndex() + 1);
                        if(multicastGroupModel.getCurrentPlayIndex() >= fileList.size()){
                            //播放溢出了，则不再播放
                            mPlayingMap.remove(l);
                            UIThreadUtil.runOnUiThread(()->{
                                        synchronized (mPlayingMap) {
                                            multicastGroupModel.setStreamPtr(-1);
                                            multicastGroupModel.setPlaying(false);
                                            if (mPlayFinishListener != null) {
                                                mPlayFinishListener.accept(multicastGroupModel);
                                            }
                                        }
                            });
                            return;
                        }
                        mPlayingMap.remove(l);
                        startMasterAudioMulticast(multicastGroupModel);
                    }break;
                    case LOOP_IN_TURN:{//顺序循环播放，放到最后一个文件后又重头开始
                        multicastGroupModel.setCurrentPlayIndex(multicastGroupModel.getCurrentPlayIndex() + 1);
                        if(multicastGroupModel.getCurrentPlayIndex() >= fileList.size()){
                            //播放溢出了，则归0重新开始
                            multicastGroupModel.setCurrentPlayIndex(0);
                        }
                        mPlayingMap.remove(l);
                        startMasterAudioMulticast(multicastGroupModel);
                    }break;
                    case LOOP_RANDOM:{//随机循环播放
                        //在正常范围内取随机数
                        Random random = new Random(System.currentTimeMillis());
                        int randomIndex = random.nextInt();
                        multicastGroupModel.setCurrentPlayIndex(randomIndex % fileList.size());
                        mPlayingMap.remove(l);
                        startMasterAudioMulticast(multicastGroupModel);
                    }break;
                    default:break;
                }
            }
        }
    }

    /**
     * 将字符串列表转换为字符串形式
     * 多个字符串之间以半角,隔开
     */
    private String convertListToString(List<String> fileList){
        StringBuilder files = new StringBuilder();
        if(!StringUtil.isEmpty(fileList)){
            for(String str : fileList){
                if(files.length() > 0){
                    files.append(",");
                }
                files.append(str);
            }
        }
        return files.toString();
    }
    /**
     * 获取用于全局播放的文件列表
     */
    public List<String> getGlobalMulticastFileList(){
        List<String> fileList = new ArrayList<>();
        String files = SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_AUDIO_MULTICAST_GROUP_WHOLE_FILE_LIST,
                mContext.getString(R.string.default_audio_multicast_group_whole_file_list));
        if(StringUtil.isEmpty(files)){
            return fileList;
        }
        List<Object> objectList = StringUtil.CutStringWithChar(files,',');
        if(!StringUtil.isEmpty(objectList)){
            for(Object o : objectList){
                fileList.add(o.toString());
            }
        }
        return fileList;
    }

    /**
     * 保存全局播放的文件列表
     */
    public void setGlobalMulticastFileList(List<String> fileList){
        String files = convertListToString(fileList);
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_AUDIO_MULTICAST_GROUP_WHOLE_FILE_LIST,files);
        if(mGlobalMulticastGroupModel != null){
            mGlobalMulticastGroupModel.getMulticastGroup().setFileList(files);
        }
    }

    /**
     * 获取全局广播的音量
     */
    public int getGlobalMulticastVolume(){
        return SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PREF_KEY_AUDIO_MULTICAST_GROUP_WHOLE_VOLUME,
                mContext.getResources().getInteger(R.integer.default_audio_multicast_group_whole_volume));
    }

    /**
     * 设置全局广播的音量
     */
    public void setGlobalMulticastVolume(int volume){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_AUDIO_MULTICAST_GROUP_WHOLE_VOLUME,volume);
        if(mGlobalMulticastGroupModel != null){
            mGlobalMulticastGroupModel.getMulticastGroup().setVolume(volume);
        }
    }

    /**
     * 获取全局广播的播放模式
     */
    public int getGlobalMulticastPlayType(){
        return SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PREF_KEY_AUDIO_MULTICAST_GROUP_WHOLE_PLAY_TYPE,
                mContext.getResources().getInteger(R.integer.default_audio_multicast_group_whole_play_type));
    }

    /**
     * 设置全局广播的音量
     */
    public void setGlobalMulticastPlayType(int playType){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_AUDIO_MULTICAST_GROUP_WHOLE_PLAY_TYPE,playType);
        if(mGlobalMulticastGroupModel != null){
            mGlobalMulticastGroupModel.getMulticastGroup().setPlayType(playType);
        }
    }

    /**
     * 获取全局广播配置
     */
    public MulticastGroupModel getGlobalMulticast(){
        return mGlobalMulticastGroupModel;
    }


    /**
     * 暴力清除所有广播
     */
    public void stopAllMulticast(){
        synchronized (mPlayingMap){
            for(MulticastGroupModel multicastGroupModel : mPlayingMap.values()){
                MulticastHelper.stopAudioMulticast(multicastGroupModel.getStreamPtr());
                multicastGroupModel.setStreamPtr(-1);
                multicastGroupModel.setPlaying(false);
                multicastGroupModel.setTalking(false);
            }
            mPlayingMap.clear();
        }
    }
}
