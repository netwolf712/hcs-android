package com.hcs.android.business.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.hcs.android.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class MulticastGroupModel extends BaseObservable {

    public MulticastGroupModel(){

    }
    public MulticastGroupModel(int groupSn,String groupNo,String address,int port){
        MulticastGroup multicastGroup = new MulticastGroup();
        multicastGroup.setGroupSn(groupSn);
        multicastGroup.setGroupNo(groupNo);
        setMulticastGroup(multicastGroup);
        setMulticastAddress(address);
        setMulticastPort(port);
    }
    /**
     * 所有分区
     */
    public static final int WHOLE_GROUP = -1;
    
    private MulticastGroup multicastGroup;

    public MulticastGroup getMulticastGroup() {
        return multicastGroup;
    }

    public void setMulticastGroup(MulticastGroup multicastGroup) {
        this.multicastGroup = multicastGroup;
    }

    /**
     * 媒体指针
     * 指向正在播放的底层句柄
     * 通过此值是否为null确定当前通道是否正在广播
     */
    private volatile long streamPtr = -1;

    public long getStreamPtr() {
        return streamPtr;
    }

    public void setStreamPtr(long streamPtr) {
        this.streamPtr = streamPtr;
    }

    /**
     * 组播地址
     */
    private String multicastAddress;

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public void setMulticastAddress(String multicastAddress) {
        this.multicastAddress = multicastAddress;
    }

    /**
     * 组播端口
     */
    private Integer multicastPort;

    public Integer getMulticastPort() {
        return multicastPort;
    }

    public void setMulticastPort(Integer multicastPort) {
        this.multicastPort = multicastPort;
    }

    public List<String> getPlayFileList(){
        if(!StringUtil.isEmpty(getMulticastGroup().getFileList())){
            List<Object> objectList = StringUtil.CutStringWithChar(getMulticastGroup().getFileList(),';');
            if(!StringUtil.isEmpty(objectList)){
                List<String> fileList = new ArrayList<>();
                for(Object obj : objectList){
                    fileList.add(obj.toString());
                }
                return fileList;
            }
        }
        return null;
    }

    /**
     * 当前播放的索引
     * 作为主机时会用到
     */
    private Integer currentPlayIndex = 0;

    public Integer getCurrentPlayIndex() {
        return currentPlayIndex;
    }

    public void setCurrentPlayIndex(Integer currentPlayIndex) {
        this.currentPlayIndex = currentPlayIndex;
    }

    /**
     * 是否正在播放音频
     */
    public boolean playing;
    @Bindable
    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
        notifyPropertyChanged(BR.playing);
    }

    /**
     * 是否正在讲话
     */
    private boolean talking;
    @Bindable
    public boolean isTalking() {
        return talking;
    }

    public void setTalking(boolean talking) {
        this.talking = talking;
        notifyPropertyChanged(BR.talking);
    }

    /**
     * 声卡名称
     * 广播讲话时不能为空
     */
    private String soundCardName;

    public String getSoundCardName() {
        return soundCardName;
    }

    public void setSoundCardName(String soundCardName) {
        this.soundCardName = soundCardName;
    }

    /**
     * 自动广播的时间段
     * time_slot_id对应的实体
     */
    private TimeSlot timeSlot;

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }
}
