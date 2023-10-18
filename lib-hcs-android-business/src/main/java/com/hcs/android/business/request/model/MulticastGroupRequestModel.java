package com.hcs.android.business.request.model;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.PlayTypeEnum;
import com.hcs.android.business.entity.MulticastGroupModel;
import com.hcs.android.business.manager.AudioMulticastManager;
import com.hcs.android.business.manager.AutoMulticastManager;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.business.service.MulticastGroupService;
import com.hcs.android.server.entity.ObservableData;

import org.simple.eventbus.EventBus;

import java.util.List;

/**
 * 分区放音
 */
public class MulticastGroupRequestModel{

    private Integer mCurrentPage = 0;
    public void setCurrentPage(Integer currentPage) {
        this.mCurrentPage = currentPage;
    }
    public Integer getCurrentPage() {
        return mCurrentPage;
    }

    private Integer mPageSize = 30;
    public void setPageSize(Integer pageSize) {
        this.mPageSize = pageSize;
    }
    public Integer getPageSize() {
        return mPageSize;
    }

    //private Integer mTotalSize;

    public MulticastGroupRequestModel() {
        //注册事件管道
        EventBus.getDefault().register(this);
    }

    public List<MulticastGroupModel> getList() {
        return WorkManager.getInstance().getMulticastGroupListFromCache(true);
    }

    public List<MulticastGroupModel> getTimeSlotGroupList() {
        return WorkManager.getInstance().getMulticastGroupListFromCache(false);
    }
    /**
     * 配置播放文件列表
     * @param multicastGroupModel 分区
     * @param files 播放文件列表，多个文件用半角;隔开
     */
    public void setPlayFiles(@NonNull MulticastGroupModel multicastGroupModel, String files){
        multicastGroupModel.getMulticastGroup().setFileList(files);
        new Thread(()->{
            MulticastGroupService.getInstance().updateMulticastGroup(multicastGroupModel.getMulticastGroup());
        }).start();
    }

    /**
     * 配置播放方式
     * @param multicastGroupModel 分区
     * @param playTypeEnum 播放方式
     */
    public void setPlayType(@NonNull MulticastGroupModel multicastGroupModel, @NonNull PlayTypeEnum playTypeEnum){
        multicastGroupModel.getMulticastGroup().setPlayType(playTypeEnum.getValue());
        new Thread(()->{
            MulticastGroupService.getInstance().updateMulticastGroup(multicastGroupModel.getMulticastGroup());
        }).start();

    }

    /**
     * 配置音量
     * @param multicastGroupModel 分区
     * @param volume 音量
     */
    public void setPlayVolume(@NonNull MulticastGroupModel multicastGroupModel, int volume){
        multicastGroupModel.getMulticastGroup().setVolume(volume);
        new Thread(()->{
            MulticastGroupService.getInstance().updateMulticastGroup(multicastGroupModel.getMulticastGroup());
        }).start();
    }

    /**
     * 开始播放
     */
    public void startPlay(@NonNull MulticastGroupModel multicastGroupModel){
        WorkManager.getInstance().startAudioMulticast(multicastGroupModel);
    }

    /**
     * 是否正在播放
     */
    public boolean isPlaying(@NonNull MulticastGroupModel multicastGroupModel){
        return AudioMulticastManager.getInstance().isPlaying(multicastGroupModel);
    }
    /**
     * 停止播放
     */
    public void stopPlay(@NonNull MulticastGroupModel multicastGroupModel){
        WorkManager.getInstance().stopAudioMulticast(multicastGroupModel);
    }

    /**
     * 正在讲话
     */
    public void startTalk(@NonNull MulticastGroupModel multicastGroupModel){
        WorkManager.getInstance().startTalkMulticast(multicastGroupModel);
    }

    /**
     * 停止讲话
     */
    public void stopTalk(@NonNull MulticastGroupModel multicastGroupModel){
        WorkManager.getInstance().stopTalkMulticast(multicastGroupModel);
    }


    /**
     * 获取用于全局播放的文件列表
     */
    public List<String> getGlobalMulticastFileList(){
        return AudioMulticastManager.getInstance().getGlobalMulticastFileList();
    }

    /**
     * 保存全局播放的文件列表
     */
    public void setGlobalMulticastFileList(List<String> fileList){
        AudioMulticastManager.getInstance().setGlobalMulticastFileList(fileList);
    }

    /**
     * 获取全局广播的音量
     */
    public int getGlobalMulticastVolume(){
        return AudioMulticastManager.getInstance().getGlobalMulticastVolume();
    }

    /**
     * 设置全局广播的音量
     */
    public void setGlobalMulticastVolume(int volume){
        AudioMulticastManager.getInstance().setGlobalMulticastVolume(volume);
    }

    /**
     * 获取全局广播的播放模式
     */
    public int getGlobalMulticastPlayType(){
        return AudioMulticastManager.getInstance().getGlobalMulticastPlayType();
    }

    /**
     * 设置全局广播的音量
     */
    public void setGlobalMulticastPlayType(int playType){
        AudioMulticastManager.getInstance().setGlobalMulticastPlayType(playType);
    }

    /**
     * 开始全局喊话
     */
    public void startGlobalTalkMulticast(){
        //开始喊话
        startTalk(AudioMulticastManager.getInstance().getGlobalMulticast());
    }

    /**
     * 开始全局广播
     */
    public void startGlobalAudioMulticast(){
        //开始广播
        startPlay(AudioMulticastManager.getInstance().getGlobalMulticast());
    }

    /**
     * 停止全局广播
     */
    public void stopGlobalAudioMulticast(){
        stopPlay(AudioMulticastManager.getInstance().getGlobalMulticast());
    }

    /**
     * 停止全局喊话
     */
    public void stopGlobalTalkMulticast(){
        stopTalk(AudioMulticastManager.getInstance().getGlobalMulticast());
    }

    /**
     * 获取广播对象
     * @param masterDeviceId 主机id
     * @param groupSn 分区id
     * @return 广播对象
     */
    public MulticastGroupModel getMulticastGroupModel(String masterDeviceId,int groupSn){
        if(groupSn != -1) {
            return WorkManager.getInstance().getMulticastGroupModelFromCache(masterDeviceId, groupSn);
        }else{
            return AudioMulticastManager.getInstance().getGlobalMulticast();
        }
    }

    /**
     * 修改/添加定时广播对象
     */
    public ObservableData<MulticastGroupModel> changeAutoMulticastGroupModel(@NonNull MulticastGroupModel multicastGroupModel){
        ObservableData<MulticastGroupModel> observableData = new ObservableData<>();
        if(multicastGroupModel.getTimeSlot() == null){
            return observableData;
        }
        new Thread(()->{
            AutoMulticastManager.getInstance().updateAutoMulticastGroupModel(multicastGroupModel);
            observableData.setT(multicastGroupModel);
        }).start();
        return observableData;
    }

    /**
     * 删除定时广播对象
     */
    public void removeAutoMulticastGroupModel(@NonNull MulticastGroupModel multicastGroupModel){
        new Thread(()->{
            AutoMulticastManager.getInstance().removeAutoMulticastGroupModel(multicastGroupModel.getMulticastGroup().getUid());
        }).start();
    }
}

