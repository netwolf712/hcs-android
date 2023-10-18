package com.hcs.android.business.entity;



import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.alibaba.fastjson.annotation.JSONField;
import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.StateEnum;
import com.hcs.android.common.network.NetConfig;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.StringUtil;

import java.util.List;
import java.util.Map;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.library.baseAdapters.BR;

import org.jetbrains.annotations.Contract;
/**
 * 设备信息
 */
public class DeviceModel extends BaseObservable {

    public DeviceModel(){
        this.device = new Device();
        this.otherCallList = new ObservableArrayList<>();
        //this.callModel = new CallModel();
    }
    public DeviceModel(Device device){
        this.device = device;
        this.otherCallList = new ObservableArrayList<>();
        //this.callModel = new CallModel();
    }

    private Device device;
    public Device getDevice(){
        return device;
    }
    public void setDevice(Device device){
        this.device = device;
    }

    /**
     * 服务器地址
     */
    private String serviceAddress;
    public String getServiceAddress(){
        return serviceAddress;
    }
    public void setServiceAddress(String serviceAddress){
        this.serviceAddress = serviceAddress;
    }

    /**
     * 服务器端口
     * 默认为80
     */
    private Integer servicePort = 80;
    public Integer getServicePort(){
        return servicePort;
    }
    public void setServicePort(Integer servicePort){
        this.servicePort = servicePort;
    }

    /**
     * 一键呼叫号码
     */
    private String directCallNo;

    public String getDirectCallNo() {
        return directCallNo;
    }

    public void setDirectCallNo(String directCallNo) {
        this.directCallNo = directCallNo;
    }

    /**
     * 获取服务器的URL
     * @return IP+port，当port=80时不用额外加上
     */
    public String getServiceURL(){
        if(StringUtil.isEmpty(serviceAddress)){
            return "";
        }
        if(servicePort != null && servicePort != 80){
            return serviceAddress + ":" + servicePort;
        }else{
            return serviceAddress;
        }
    }

    /**
     * 是否从服务器获取到最新数据并更新
     */
    private boolean updated;
    public boolean isUpdated(){
        return updated;
    }
    public void setUpdated(boolean updated){
        this.updated = updated;
    }

    /**
     * 上级主机列表
     * <级别序号，主机号码>
     */
    private Map<Integer,String> parentMap;
    public Map<Integer,String> getParentMap(){
        return parentMap;
    }
    public void setParentMap(Map<Integer,String> parentMap){
        this.parentMap = parentMap;
    }

    /**
     * 设备状态
     */
    private StateEnum state;
    @Bindable
    public StateEnum getState(){
        return state;
    }
    public void setState(StateEnum state){
        this.state = state;
        notifyPropertyChanged(BR.state);
    }
    /**
     * 实际的电话号码为设备序列号
     */
    public String getPhoneNumber(){
        return getDevice().getDeviceId();
    }

    /**
     * 呼叫状态
     * 这里只存放视频、普通呼叫
     */
    @Bindable
    private CallModel callModel;
    public CallModel getCallModel(){
        return callModel;
    }
    public void setCallModel(CallModel callModel){
        this.callModel = callModel;
        notifyPropertyChanged(BR.callModel);
    }

    /**
     * 呼叫状态
     * 其它类型的呼叫
     */
    @Bindable
    private ObservableArrayList<CallModel> otherCallList;
    public ObservableArrayList<CallModel> getOtherCallList() {
        return otherCallList;
    }
    public void setOtherCallList(ObservableArrayList<CallModel> otherCallList) {
        this.otherCallList = otherCallList;
        notifyPropertyChanged(BR.otherCallList);
    }

    /**
     * 当前网络配置
     */
    private NetConfig netConfig;
    public NetConfig getNetConfig(){
        return netConfig;
    }
    public void setNetConfig(NetConfig netConfig){
        this.netConfig = netConfig;
    }

    /**
     * 分区号
     */
    private String sectionNo;
    public String getSectionNo(){
        return sectionNo;
    }
    public void setSectionNo(String sectionNo){
        this.sectionNo = sectionNo;
    }

    /**
     * 病区名称
     */
    private String sectionName;
    public String getSectionName(){
        return sectionName;
    }
    public void setSectionName(String sectionName){
        this.sectionName = sectionName;
    }

    /**
     * 分机最近一次请求配置时的数据索引
     * 方便主机判断是否重发回复数据
     */
    private Long lastReqGetConfigIndex;
    public Long getLastReqGetConfigIndex() {
        return lastReqGetConfigIndex;
    }
    public void setLastReqGetConfigIndex(Long lastReqGetConfigIndex) {
        this.lastReqGetConfigIndex = lastReqGetConfigIndex;
    }

    /**
     * 绑定的病床、门口、病区的序列号
     */
    @JSONField(serialize = false)
    public String getBindPhoneNo(){
        return getBindPhoneNo(DeviceTypeEnum.findById(getDevice().getDeviceType()),Integer.parseInt(getDevice().getPhoneNo()),getDevice().getParentNo());
    }

    @NonNull
    @Contract(pure = true)
    public static String getBindPhoneNo(DeviceTypeEnum deviceTypeEnum, int bindSn, String parentNo){
        //护士站主机不用标上级
        if(deviceTypeEnum == DeviceTypeEnum.NURSE_STATION_MASTER){
            parentNo = "-";
        }
        return parentNo + '-' + deviceTypeEnum.getName() + "-" + bindSn;
    }

    @NonNull
    public static String getMasterPhoneNo(String masterPhoneNo){
        return getBindPhoneNo(DeviceTypeEnum.NURSE_STATION_MASTER,Integer.parseInt(masterPhoneNo),"-");
    }

    /**
     * 将设备状态转换为字符串形式
     */
    public static String convertState(StateEnum state){
        if(state != null) {
            return state.getDisplayName(BusinessApplication.getAppContext());
        }
        return StateEnum.UNKNOWN.getDisplayName(BusinessApplication.getAppContext());
    }
    /**
     * 判断设备信息是否有改变
     */
    public boolean compare(Device device){
        //直接转换成字符串一通判断即可
        return StringUtil.equalsIgnoreCase(JsonUtils.toJsonString(getDevice()),JsonUtils.toJsonString(device));
    }
}
