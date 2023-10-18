package com.hcs.android.business.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableArrayList;

import com.hcs.android.annotation.api.HandlerManager;
import com.hcs.android.business.R;
import com.hcs.android.business.constant.BooleanConstant;
import com.hcs.android.business.constant.CallMediaTypeEnum;
import com.hcs.android.business.constant.CallTypeEnum;
import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.Constant;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.EventBusConstant;
import com.hcs.android.business.constant.PlaceTypeEnum;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.constant.StateEnum;
import com.hcs.android.business.constant.UpdateTypeEnum;
import com.hcs.android.business.entity.BedScreenTemplate;
import com.hcs.android.business.entity.CallModel;
import com.hcs.android.business.entity.Device;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.IPCamera;
import com.hcs.android.business.entity.MulticastGroup;
import com.hcs.android.business.entity.MulticastGroupModel;
import com.hcs.android.business.entity.OperationLog;
import com.hcs.android.business.entity.Patient;
import com.hcs.android.business.entity.PatientModel;
import com.hcs.android.business.entity.Place;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.entity.RequestBedDetail;
import com.hcs.android.business.entity.RequestBedScreenTemplate;
import com.hcs.android.business.entity.RequestBindGroup;
import com.hcs.android.business.entity.RequestBindPlace;
import com.hcs.android.business.entity.RequestBindPlaceParent;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestData;
import com.hcs.android.business.entity.RequestPrivacy;
import com.hcs.android.business.entity.RequestRoomDetail;
import com.hcs.android.business.entity.RequestRoomScreenTemplate;
import com.hcs.android.business.entity.RequestTrust;
import com.hcs.android.business.entity.RequestUpdateCapability;
import com.hcs.android.business.entity.RequestVolumeSet;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.entity.ResponseTrust;
import com.hcs.android.business.entity.RoomScreenTemplate;
import com.hcs.android.business.request.model.RequestHelper;
import com.hcs.android.business.service.DeviceService;
import com.hcs.android.business.service.IPCameraService;
import com.hcs.android.business.service.MulticastGroupService;
import com.hcs.android.business.service.OperationLogService;
import com.hcs.android.business.service.PatientService;
import com.hcs.android.business.service.PlaceService;
import com.hcs.android.business.util.GroupUtil;
import com.hcs.android.business.util.IdCardUtil;
import com.hcs.android.business.util.PlaceUtil;
import com.hcs.android.business.util.VolumeUtil;
import com.hcs.android.call.api.CallManager;
import com.hcs.android.call.api.ChatHelper;
import com.hcs.android.call.api.FriendHelper;
import com.hcs.android.common.network.NetConfig;
import com.hcs.android.common.network.NetworkManager;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.CastUtil;
import com.hcs.android.common.util.ExeCommand;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.Md5Util;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.server.web.AjaxResult;

import org.linphone.core.Call;
import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/**
 * 设备管理器
 */
public abstract class DeviceManager implements CallManager.ICallSimpleListener {

    protected Context mContext;
    /**
     * 分页查询时每页的大小
     */
    private final static int SIZE_PER_PAGE = 200;

    /**
     * 密钥加盐
     * (其实它才是真正的密钥)
     */
    private final static String PASSWORD_SALT = "root13173137";
    /**
     * 本机
     */
    protected DeviceModel mSelf;
    /**
     * 同步专用对象
     */
    protected final Object mSynObj = new Object();

    //位置表
    protected final Map<String, PlaceModel> mPlaceMap = new HashMap<>();

    private final RequestHelper mRequestHelper;

    protected CallManager mCallManager;

    protected HandlerBase mHandlerBase;

    /**
     * 设备数据管理器
     */
    protected DeviceDataManager mDeviceDataManager;

    /**
     * 数据更新管理器
     * （目前只管理强制更新的数据）
     */
    protected UpdateDataHelper<Place> mPlaceUpdateHelper;
    protected UpdateDataHelper<Patient> mPatientUpdateHelper;

    /**
     * web控制器
     */
    protected final HandlerManager mWebHandlerManager;
    private final static String HANDLER_PACKAGE_NAME = "com.hcs.android.business";

    /**
     * 当前是否晚上
     */
    private boolean mCurrentIsNight;


    public DeviceManager(Context context){
        mContext = context;
        mWebHandlerManager = new HandlerManager(HANDLER_PACKAGE_NAME);

        mDeviceDataManager = new DeviceDataManager();
        mRequestHelper = new RequestHelper();
        mCallManager = new CallManager();
        mCallManager.setCallSimpleListener(this);
        mPlaceUpdateHelper = new UpdateDataHelper<>(PlaceService.getInstance());
        mPatientUpdateHelper = new UpdateDataHelper<>(PatientService.getInstance());
        //从本地获取设备列表
        //这个要放在最前面
        //因为最终以从服务器获取的数据为准
        //但服务器返回的时机又存在不确定性，若后面获取，此数据可能将从服务器获取的数据覆盖
        getDeviceListFromLocal();

        //加载继任主机配置
        StepMasterManager.getInstance().loadAllData();

        DeviceModel deviceModel = getSelfInfo();
        //把自己的数据也加入设备列表
        mDeviceDataManager.addDeviceModel(deviceModel);
        //设置音量
        setVolume(getVolumeSet());
        mCurrentIsNight = TimeSlotManager.getInstance().isNight(System.currentTimeMillis());
    }

    /**
     * 获取自己是什么型号的设备
     */
    abstract DeviceTypeEnum getSelfDeviceType();
    /**
     * 重新加载数据
     */
    public void reloadData(){
        mDeviceDataManager.clearDeviceList();
        //但服务器返回的时机又存在不确定性，若后面获取，此数据可能将从服务器获取的数据覆盖
        getDeviceListFromLocal();

        DeviceModel deviceModel = getSelfInfo();
        //把自己的数据也加入设备列表
        mDeviceDataManager.addDeviceModel(deviceModel);
    }
    /**
     * 开启呼叫监听器，只有在linphone初始化成功后才能设置
     * 所以另开方法
     */
    public void startCallListener(){
        mCallManager.startCallListener();
    }
    public void setHandlerBase(HandlerBase handlerBase){
        mHandlerBase = handlerBase;
    }
    public RequestHelper getRequestHelper(){
        return mRequestHelper;
    }
    /**
     * 获取本机信息
     */
    @SuppressLint("HardwareIds")
    protected DeviceModel getSelfInfo(){
        mSelf = getDeviceInfo(mContext);
        RingBackToneHelper.getInstance().setPlayObserver(mSelf);
        return mSelf;
    }

    public DeviceModel getSelf(){
        return mSelf;
    }

    /**
     * 获取本机信息
     */
    @NonNull
    @SuppressLint("HardwareIds")
    public static DeviceModel getDeviceInfo(Context context){
        DeviceModel deviceModel = new DeviceModel();
        //先获取设备类型
        deviceModel.getDevice().setDeviceType(SettingsHelper.getInstance(context).getInt(PreferenceConstant.PREF_KEY_DEVICE_TYPE, DeviceTypeEnum.NURSE_STATION_MASTER.getValue()));
        deviceModel.getDevice().setHardwareVersion("v00");
        deviceModel.getDevice().setSoftwareVersion(getAppVersionName(context));
        deviceModel.getDevice().setDeviceId(android.os.Build.SERIAL);
        deviceModel.getDevice().setSystemVersion(Build.VERSION.RELEASE);
        deviceModel.getDevice().setModule(Build.MODEL);
        deviceModel.getDevice().setKernelVersion(ExeCommand.executeSuCmd("uname -r"));
        deviceModel.getDevice().setUbootVersion(Build.BOOTLOADER);
        NetworkManager networkManager = new NetworkManager();
        NetConfig netConfig = networkManager.getNetConfig(context);
        deviceModel.setNetConfig(netConfig);
        deviceModel.getDevice().setIpAddress(netConfig.getIpAddress());
        deviceModel.getDevice().setMacAddress(netConfig.getHardwareAddress());
        deviceModel.getDevice().setPhoneNo(SettingsHelper.getInstance(context).getString(PreferenceConstant.PREF_KEY_PHONE_NUMBER,""));
        deviceModel.getDevice().setParentNo(SettingsHelper.getInstance(context).getString(PreferenceConstant.PREF_KEY_PARENT_NUMBER,""));
        deviceModel.getDevice().setWholeTitle(SettingsHelper.getInstance(context).getString(PreferenceConstant.PREF_KEY_WHOLE_TITLE,
                context.getString(R.string.default_whole_title)));
        deviceModel.setSectionNo(SettingsHelper.getInstance(context).getString(PreferenceConstant.PREF_KEY_SECTION_NO,context.getString(R.string.default_section_no)));
        deviceModel.setSectionName(SettingsHelper.getInstance(context).getString(PreferenceConstant.PREF_KEY_SECTION_NAME,context.getString(R.string.default_section_name)));
        deviceModel.setServiceAddress(SettingsHelper.getInstance(context).getString(PreferenceConstant.PREF_KEY_SERVICE_ADDRESS,context.getString(R.string.pref_key_service_address)));
        deviceModel.setServicePort(SettingsHelper.getInstance(context).getInt(PreferenceConstant.PREF_KEY_SERVICE_PORT,context.getResources().getInteger(R.integer.default_service_port)));
        deviceModel.setDirectCallNo(SettingsHelper.getInstance(context).getString(PreferenceConstant.PREF_KEY_DIRECT_CALL_NO,context.getString(R.string.default_direct_call_no)));
        return deviceModel;
    }
    /**
     * 获取当前应用的版本号
     */
    public static String getAppVersionName(@NonNull Context context) {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(packInfo).versionName;
    }

    /**
     * 设置本机号码
     */
    public void setSelfNo(String phoneNo){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_PHONE_NUMBER,phoneNo);
        getSelf().getDevice().setPhoneNo(phoneNo);
    }

    /**
     * 设置上级主机号码
     */
    public void setParentNO(String parentNo){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_PARENT_NUMBER,parentNo);
        getSelf().getDevice().setParentNo(parentNo);
    }

    public void setServiceAddress(String serviceAddress,Integer servicePort){
        mSelf.setServiceAddress(serviceAddress);
        if(servicePort != null){
            mSelf.setServicePort(servicePort);
        }
    }
    /**
     * 在本地数据库中获取设备列表
     */
    public void getDeviceListFromLocal(){
        List<Device> deviceList = DeviceService.getInstance().getDeviceList();
        clearDeviceList();
        addDeviceList(deviceList);
    }

    private void getDeviceListFromService(int pageIndex){
        mRequestHelper.getDeviceList(getSelf(),"",pageIndex,SIZE_PER_PAGE,res -> {
            if(res.getData() instanceof ResponseList) {
                ResponseList<Device> deviceResponseList = CastUtil.cast(res.getData());
                if(!StringUtil.isEmpty(deviceResponseList.getList())){
                    updateDeviceList(deviceResponseList.getList());
                    if(deviceResponseList.getTotalLength() > (deviceResponseList.getCurrentPage() + 1) * deviceResponseList.getPageSize()){
                        getDeviceListFromService(pageIndex + 1);
                    }else{
                        //数据获取完毕后将本地有存但服务器没有的设备信息删除
                        removeDevices();
                        if(mHandlerBase != null) {
                            mHandlerBase.sendReadDeviceListOK();
                        }
                    }
                }
            }
        },null);
    }

    /**
     * 从服务器上获取设备列表
     * 服务器上的优先级最高，可以直接覆盖原先的设备列表
     */
    public void getDeviceListFromService(){
        getDeviceListFromService(0);
    }

    /**
     * 获取登录服务器的密码
     */
    public String getSelfPassword(){
        return Md5Util.encrypt(getSelf().getDevice().getDeviceId() + PASSWORD_SALT);
    }

    /**
     * 获取登录时的用户名
     */
    public String getSelfUsername(){
        return getSelf().getDevice().getDeviceId();
    }


    public List<DeviceModel> getDeviceList(){
        return mDeviceDataManager.getDeviceList();
    }

    /**
     * 清除所有设备
     * 不更新数据库
     */
    private void clearDeviceList(){
        mDeviceDataManager.clearDeviceList();
    }



    /**
     * 加入设备列表
     * 不更新数据库（从数据库读取并放入内存）
     */
    private void addDeviceList(List<Device> deviceList){
        if(!StringUtil.isEmpty(deviceList)){
            synchronized (mSynObj) {
                for (Device device : deviceList) {
                    if(!validateDevice(device)){
                        continue;
                    }
                    DeviceModel deviceModel = new DeviceModel(device);
                    //默认状态为不在线
                    deviceModel.setState(StateEnum.OFFLINE);
                    mDeviceDataManager.addDeviceModel(deviceModel);
                }
            }
        }
    }

    /**
     * 从位置节点中删除指定设备
     */
    private void removeDeviceFromPlace(DeviceModel deviceModel){
        PlaceModel placeModel = findPlaceByDevice(deviceModel);
        if(placeModel != null) {
            if (!StringUtil.isEmpty(placeModel.getDeviceModelList())) {
                placeModel.getDeviceModelList().remove(deviceModel);
            }
        }
    }

    /**
     * 将设备加入位置列表
     */
    @Nullable
    private PlaceModel addDeviceToPlace(DeviceModel deviceModel){
        PlaceModel placeModel = findPlaceByDevice(deviceModel);
        if(placeModel != null) {
            if (!StringUtil.isEmpty(placeModel.getDeviceModelList())) {
                for(DeviceModel tmp : placeModel.getDeviceModelList()){
                    if(StringUtil.equalsIgnoreCase(tmp.getDevice().getDeviceId(),deviceModel.getDevice().getDeviceId())){
                        deviceModel.getDevice().setPlaceUid(placeModel.getPlace().getUid());
                        mDeviceDataManager.addToPlaceMap(deviceModel);
                        return placeModel;
                    }
                }
            }
            placeModel.getDeviceModelList().add(deviceModel);
            deviceModel.getDevice().setPlaceUid(placeModel.getPlace().getUid());
            mDeviceDataManager.addToPlaceMap(deviceModel);
            return placeModel;
        }
        return null;
    }

    /**
     * 更新设备信息
     * 同时更新数据库
     * 若之前没有则做插入操作
     */
    protected DeviceModel updateDevice(Device device){
        synchronized (mSynObj){
            if(!validateDevice(device)){
                return null;
            }

            DeviceModel deviceModel = mDeviceDataManager.findInIdMap(device.getDeviceId());
            if(deviceModel != null){
                if(!deviceModel.compare(device)){
                    //数据命令的发送次数不用更新
                    device.setLastDataCommandIndex(deviceModel.getDevice().getLastDataCommandIndex());
                    String oldBindPhoneNo = deviceModel.getBindPhoneNo();
                    String curBindPhoneNo = DeviceModel.getBindPhoneNo(DeviceTypeEnum.findById(device.getDeviceType()),Integer.parseInt(device.getPhoneNo()),device.getParentNo());
                    if(StringUtil.equalsIgnoreCase(oldBindPhoneNo,curBindPhoneNo)){
                        //移除之前的号码映射
                        mDeviceDataManager.removeFromPhoneNoMap(deviceModel);
                        removeDeviceFromPlace(deviceModel);
                        deviceModel.setDevice(device);
                        mDeviceDataManager.addToPhoneNoMap(deviceModel);
                        addDeviceToPlace(deviceModel);
                    }else {
                        deviceModel.setDevice(device);
                        addDeviceToPlace(deviceModel);
                    }
                    //设置更新标记
                    deviceModel.setUpdated(true);
                    DeviceService.getInstance().updateDevice(deviceModel);
                }
            }else{
                //如果找不到，则新增一个
                deviceModel = new DeviceModel(device);
                mDeviceDataManager.addDeviceModel(deviceModel);
                deviceModel.setUpdated(true);
                addDeviceToPlace(deviceModel);
                DeviceService.getInstance().updateDevice(deviceModel);
            }
            return deviceModel;
        }
    }

    /**
     * 获取指定设备的主机
     */
    protected DeviceModel getDeviceParent(@NonNull DeviceModel deviceModel){
        if(mSelf.getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue()){
            //如果对方设备也是主机，则上级主机也顶为自己
            if(deviceModel.getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue()){
                return mSelf;
            }
            if(StringUtil.equalsIgnoreCase(deviceModel.getDevice().getParentNo(),mSelf.getDevice().getPhoneNo())){
                return mSelf;
            }
        }

        //主机就是自己
        if(deviceModel.getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue()){
            return deviceModel;
        }else {
            return getDeviceByPhoneNo(DeviceModel.getMasterPhoneNo(deviceModel.getDevice().getParentNo()));
        }
    }

    /**
     * 更新/插入设备，同时更新数据库
     */
    private void updateDeviceList(List<Device> deviceList){
        if(!StringUtil.isEmpty(deviceList)){
            for(Device device : deviceList){
                updateDevice(device);
            }
        }
    }

    /**
     * 从设备列表中清除未更新的设备
     * 同时从数据库中删除
     */
    private void removeDevices(){
        mDeviceDataManager.removeUnUpdatedDevices();
    }
    /**
     * 设备数据有效性判断
     */
    private boolean validateDevice(@NonNull Device device){
        if(StringUtil.isEmpty(device.getPhoneNo())){
            //电话号码不能为空
            KLog.w("phone number can not be empty (" + JsonUtils.toJsonString(device) + ").");
            return false;
        }
        if(StringUtil.isEmpty(device.getDeviceId())){
            //电话号码不能为空
            KLog.w("device id can not be empty (" + JsonUtils.toJsonString(device) + ").");
            return false;
        }
        return true;
    }

    /**
     * 将设备类型转换为其能挂载的位置类型
     */
    protected PlaceTypeEnum findPlaceTypeByDevice(@NonNull Device device){
        switch (DeviceTypeEnum.findById(device.getDeviceType())){
            case BED_SCREEN:return PlaceTypeEnum.BED;
            case NURSE_STATION_MASTER:return PlaceTypeEnum.STATION;
            case DOOR_CONTROL:return PlaceTypeEnum.SECTION;
            case NURSE_STATION_PANEL:
            case CORRIDOR_SCREEN:return PlaceTypeEnum.CORRIDOR;
            case ROOM_SCREEN:return PlaceTypeEnum.ROOM;
            case SERVICE:
            default:return PlaceTypeEnum.HOSPITAL;
        }
    }

    /**
     * 根据设备配置找到对应的位置
     */
    public PlaceModel findPlaceByDevice(@NonNull DeviceModel deviceModel){
        PlaceTypeEnum placeTypeEnum = findPlaceTypeByDevice(deviceModel.getDevice());
        DeviceModel tmpModel = getDeviceParent(deviceModel);
        if(tmpModel == null){
            return null;
        }
        int slaveNo = Integer.parseInt(deviceModel.getDevice().getPhoneNo());
        if(deviceModel.getDevice().getDeviceType() == DeviceTypeEnum.NURSE_STATION_MASTER.getValue()){
            //如果是主机，则需减去基础数值
            slaveNo -= SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.MASTER_BASE_NO,mContext.getResources().getInteger(R.integer.default_master_base_no));
        }
        String placeUid = PlaceUtil.genPlaceUid(tmpModel.getDevice().getDeviceId(),placeTypeEnum.getName(),slaveNo);
        synchronized (mSynObj){
            return mPlaceMap.get(placeUid);
        }
    }


    /**
     * 更新设备信息
     */
    public void updateDeviceInfo(@NonNull RequestDTO<DeviceModel> requestDTO){
        if(requestDTO.getData() != null) {
            DeviceModel deviceModel = requestDTO.getData();
            if (!StringUtil.equalsIgnoreCase(deviceModel.getDevice().getDeviceId(), mSelf.getDevice().getDeviceId())) {
                //更新此设备对应的位置
                PlaceModel placeModel = findPlaceByDevice(deviceModel);
                if(placeModel != null){
                    deviceModel.getDevice().setPlaceUid(placeModel.getPlace().getUid());
                }
                updateDevice(deviceModel.getDevice());

                //检测此设备是否为请求配置状态，是则说明是刚连上来的，需要清理之前的呼叫
                if(StringUtil.equalsIgnoreCase(requestDTO.getCommandId(),CommandEnum.NOTICE_DEVICE_STARTED.getId())) {
                    DeviceModel tmp = mDeviceDataManager.findInIdMap(deviceModel.getDevice().getDeviceId());
                    if(tmp != null) {
                        clearAllCall(tmp);
                        tmp.setState(StateEnum.ONLINE);
                    }
                }
            } else {
                changeSelfInfo(deviceModel);
            }
        }
    }

    /**
     * 清除所有呼叫
     * 主要用于设备断线、重连时的容错处理
     * @param deviceModel 需要清理的设备
     */
    protected void clearAllCall(@NonNull DeviceModel deviceModel){
        CallInManager.getInstance().removeCallInModelByCaller(deviceModel.getDevice().getDeviceId(),null);
        deviceModel.setCallModel(null);
        deviceModel.getOtherCallList().clear();
    }

    /**
     * 通过主机号码寻找设备
     * @param phoneNo 主机号码（给用户看的那个号码，不是序列号）
     */
    public DeviceModel getDeviceByPhoneNo(String phoneNo){
        //可能会存在重复的情况
        //找到第一个作数
        synchronized (mSynObj){
            return mDeviceDataManager.findOneInPhoneNoMap(phoneNo);
        }
    }

    /**
     * 获取自己的上层主机
     */
    public DeviceModel getParentDevice(){
        return getDeviceByPhoneNo(DeviceModel.getMasterPhoneNo(mSelf.getDevice().getParentNo()));
    }

    /**
     * 通过设备序列号获取设备详情
     * @param deviceId 设备序列号
     */
    public DeviceModel getDeviceDetail(String deviceId){
        return new DeviceModel(DeviceService.getInstance().getDevice(deviceId));
    }

    /**
     * 检查连接状态
     */
    public void checkConnection(){
        KLog.i("do checkConnection");
        int heartBeatSpan = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.HEART_BEAT_SPAN,mContext.getResources().getInteger(R.integer.default_heart_beat_span));
        if(heartBeatSpan > 0) {
            //检查是否连接上主机的
            //心跳包不用太频繁
            if (mSelf.getDevice().getHeartBeatTime() + heartBeatSpan < System.currentTimeMillis()) {
                mSelf.getDevice().setHeartBeatTime(System.currentTimeMillis());
                //发送心跳
                RequestDTO<Object> requestDTO = new RequestDTO<>(mSelf, CommandEnum.REQ_HEART_BEAT.getId());
                mHandlerBase.sendMulticast(requestDTO);
            }
        }
        //找出离线设备，并更新状态
        int offlineTime = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.SLAVE_OFFLINE_TIME,
                mContext.getResources().getInteger(R.integer.default_slave_offline_time));
        long offlineTimePoint = System.currentTimeMillis() - offlineTime;
        synchronized (mSynObj){
            if(!StringUtil.isEmpty(getDeviceList())){
                for(DeviceModel deviceModel : getDeviceList()){
                    if(!StringUtil.equalsIgnoreCase(deviceModel.getDevice().getDeviceId(),mSelf.getDevice().getDeviceId())){
                        if(!Objects.equals(deviceModel.getState(),StateEnum.OFFLINE)
                                && deviceModel.getDevice().getHeartBeatTime() < offlineTimePoint){
                            //只有之前不是离线的才需要进入离线状态
                            clearAllCall(deviceModel);
                            changeDeviceState(deviceModel.getDevice().getDeviceId(),StateEnum.OFFLINE);
                        }
                    }
                }
            }
        }
    }

    /**
     * 检查白天还是黑夜
     */
    public void checkDayOrNight(){
        boolean isNight = TimeSlotManager.getInstance().isNight(System.currentTimeMillis());
        if(mCurrentIsNight != isNight){
            //白天、黑夜产生变化，则执行相关操作
            RequestVolumeSet requestVolumeSet = getVolumeSet();
            if(requestVolumeSet.isNightVolumeSetEnable()){
                setVolume(requestVolumeSet);
            }

            mCurrentIsNight = isNight;
        }
    }

    protected void changeDeviceState(String deviceId, StateEnum stateEnum){
        synchronized (mSynObj){
            DeviceModel tmp = mDeviceDataManager.findInIdMap(deviceId);
            if(tmp != null){
                tmp.setState(stateEnum);
            }
        }
    }
    private void updateLinphoneFriend(DeviceModel deviceModel){
        try {
            FriendHelper.addFriend(deviceModel.getDevice().getDeviceId()
                    , deviceModel.getBindPhoneNo()
                    , deviceModel.getDevice().getIpAddress()
                    , String.valueOf(deviceModel.getDevice().getDeviceType())
                    , deviceModel);
        }catch (Exception e){
            KLog.w(e);
        }
    }
    /**
     * 统一请求预处理
     * 维护设备的在线状态
     */
    public void handleRequestFirst(@NonNull RequestDTO<Object> requestDTO){
        synchronized (mSynObj){
            DeviceModel deviceModel = requestDTO.asDeviceModel();
            //只有离线状态的才需恢复在线状态
            DeviceModel tmp = mDeviceDataManager.findInIdMap(deviceModel.getDevice().getDeviceId());
            if(tmp == null){
                KLog.e("not find deviceId ==> " + deviceModel.getDevice().getDeviceId() + " add new one");
                tmp = updateDevice(deviceModel.getDevice());
                if(tmp == null) {
                    return;
                }else {
                    //新增加的设备，需要通知更新好友列表
                    updateLinphoneFriend(tmp);
                }
            }else{
                //需要比较IP地址是否变化
                //若是则需要通知更新好友列表
                if(!StringUtil.equalsIgnoreCase(deviceModel.getDevice().getIpAddress(),tmp.getDevice().getIpAddress())){
                    tmp.getDevice().setIpAddress(deviceModel.getDevice().getIpAddress());
                    updateLinphoneFriend(tmp);
                }

                PlaceModel placeModel = findPlaceByDevice(deviceModel);
                //需要比较分机、主机号码是否变化，是则更新设备
                //节点位置是否改变
                if(!StringUtil.equalsIgnoreCase(deviceModel.getDevice().getParentNo(),tmp.getDevice().getParentNo())
                || !StringUtil.equalsIgnoreCase(deviceModel.getDevice().getPhoneNo(),tmp.getDevice().getPhoneNo())
                || placeModel != null && !StringUtil.equalsIgnoreCase(placeModel.getPlace().getUid(),tmp.getDevice().getPlaceUid())
                ){
                    tmp = updateDevice(deviceModel.getDevice());
                }

            }
            if(tmp.getState() == StateEnum.OFFLINE){
                tmp.setState(StateEnum.ONLINE);
            }

            //如果超过心跳时间的一半了，则更新心跳时间
            int heartBeatTime = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.HEART_BEAT_SPAN,mContext.getResources().getInteger(R.integer.default_heart_beat_span));
            tmp.getDevice().setHeartBeatTime(System.currentTimeMillis());
            if(tmp.getDevice().getHeartBeatTime() == null || heartBeatTime > 0 && System.currentTimeMillis() > (heartBeatTime / 2 + tmp.getDevice().getHeartBeatTime())){
                DeviceService.getInstance().updateDevice(tmp);
            }
        }
    }

    public void handleHeartBeat(@NonNull RequestDTO<Object> requestDTO){
        synchronized (mSynObj) {
            DeviceModel deviceModel = requestDTO.asDeviceModel();
            //只有离线状态的才需恢复在线状态
            DeviceModel tmp = mDeviceDataManager.findInIdMap(deviceModel.getDevice().getDeviceId());
            if (tmp == null) {
                return;
            }
            if(tmp.getState() == StateEnum.OFFLINE){
                tmp.setState(StateEnum.ONLINE);
            }
            tmp.getDevice().setHeartBeatTime(System.currentTimeMillis());
            DeviceService.getInstance().updateDevice(tmp);
        }
    }

    protected void addOperationLog(@NonNull CallModel callModel){
        OperationLog operationLog = new OperationLog();
        operationLog.setAppendPath(callModel.getAppendPath());
        PlaceModel calleePlace = findPlaceByDevice(mDeviceDataManager.findInIdMap(callModel.getCalleeDeviceId()));
        if(calleePlace != null){
            operationLog.setCalleePlaceName(calleePlace.getPlace().getPlaceNo());
        }
        operationLog.setCallee(callModel.getCallee());
        operationLog.setCalleeDeviceId(callModel.getCalleeDeviceId());
        operationLog.setCalleeType(callModel.getCalleeType());
        operationLog.setState(callModel.getState());
        operationLog.setUpdateTime(System.currentTimeMillis());
        PlaceModel callerPlace = findPlaceByDevice(mDeviceDataManager.findInIdMap(callModel.getCallerDeviceId()));
        if(callerPlace != null){
            operationLog.setCallerPlaceName(callerPlace.getPlace().getPlaceNo());
        }
        operationLog.setCaller(callModel.getCaller());
        operationLog.setCallerDeviceId(callModel.getCallerDeviceId());
        operationLog.setCallerType(callModel.getCallerType());
        operationLog.setCause(callModel.getCause());
        operationLog.setCallRef(callModel.getCallRef());
        operationLog.setResult(callModel.getResult());
        operationLog.setEmergency(BooleanConstant.booleanToInt(callModel.isEmergencyCall()));
        operationLog.setType(callModel.getCallType());
        operationLog.setStartTime(callModel.getStartTime());
        operationLog.setStopTime(callModel.getStopTime());
        operationLog.setConnectTime(callModel.getConnectTime());
        OperationLogService.getInstance().updateOperationLog(operationLog);
    }

    protected CallModel createCallModel(String remoteNo
            , @NonNull CallTypeEnum callTypeEnum
            , @NonNull StateEnum stateEnum
            , String cause
            , boolean isEmergency
            , boolean selfIsCaller){
        synchronized (mSynObj) {
            DeviceModel deviceModel =  mDeviceDataManager.findOneInPhoneNoMap(remoteNo);
            if(deviceModel == null){
                return null;
            }
            CallModel callModel;
            callModel = findCallModel(deviceModel,callTypeEnum);
            if(callModel == null){
                callModel = new CallModel();
                callModel.setCallee(selfIsCaller ? remoteNo : mSelf.getBindPhoneNo());
                callModel.setCalleeDeviceId(selfIsCaller ? deviceModel.getDevice().getDeviceId() : mSelf.getDevice().getDeviceId());
                callModel.setCalleeType(selfIsCaller ? deviceModel.getDevice().getDeviceType() : mSelf.getDevice().getDeviceType());
                callModel.setCaller(selfIsCaller ? mSelf.getBindPhoneNo() : remoteNo);
                callModel.setCallerDeviceId(selfIsCaller ? mSelf.getDevice().getDeviceId() : deviceModel.getDevice().getDeviceId());
                callModel.setCallerType(selfIsCaller ? mSelf.getDevice().getDeviceType() : deviceModel.getDevice().getDeviceType());
                callModel.setCallType(callTypeEnum.getValue());
                callModel.setEmergencyCall(isEmergency);
                callModel.setCause(cause);
                callModel.setState(stateEnum.getValue());
                callModel.setStartTime(System.currentTimeMillis());
                callModel.setCallRef(getCallRef(deviceModel));
                addCallModel(deviceModel,callModel);
            }else{
                callModel.setCause(cause);
            }
            return callModel;
        }


    }
    /**
     * 开始呼叫
     * （分机：发送请求呼叫的message消息）
     * （主叫：发送消息但不执行呼叫操作，呼叫操作统一由acceptCall实现）
     * @param remoteNo 被叫号码
     * @param callTypeEnum 呼叫类型
     * @param cause 呼叫原因
     */
    public CallModel startCall(String remoteNo, CallTypeEnum callTypeEnum,String cause,boolean isEmergency){
        synchronized (mSynObj) {
            DeviceModel deviceModel = mDeviceDataManager.findOneInPhoneNoMap(remoteNo);
            if(deviceModel == null){
                return null;
            }
            CallModel callModel = sendCall(deviceModel,callTypeEnum,cause,isEmergency);
            callModel.setState(StateEnum.CALLING.getValue());
            updateDeviceStateByCall(callModel,StateEnum.CALLING);
            addOperationLog(callModel);
            return callModel;
        }
    }

    /**
     * 产生呼叫索引
     * 方便查询
     */
    @NonNull
    private String getCallRef(@NonNull DeviceModel remoteDevice){
        return remoteDevice.getBindPhoneNo() + "-" + System.currentTimeMillis();
    }
    public CallModel sendCall(@NonNull DeviceModel deviceModel, CallTypeEnum callTypeEnum, String cause, boolean isEmergency){
        RequestDTO<CallModel> requestDTO = new RequestDTO<>(mSelf, CommandEnum.REQ_SEND_CALL.getId());
        CallModel callModel = createCallModel(deviceModel.getBindPhoneNo(), callTypeEnum, StateEnum.CALLING, cause, isEmergency, true);
        requestDTO.setData(callModel);
        ChatHelper.sendMessage(deviceModel.getPhoneNumber(),requestDTO);
        return callModel;
    }

    /**
     * 获取远端设备的数据
     */
    protected DeviceModel getRemoteDevice(@NonNull CallModel callModel){
        synchronized (mSynObj) {
            return StringUtil.equalsIgnoreCase(mSelf.getBindPhoneNo(), callModel.getCallee()) ? mDeviceDataManager.findOneInPhoneNoMap(callModel.getCaller()) : mDeviceDataManager.findOneInPhoneNoMap(callModel.getCallee());
        }
    }

    protected DeviceModel getDeviceById(@NonNull String deviceId){
        synchronized (mSynObj){
            if(StringUtil.equalsIgnoreCase(deviceId,mSelf.getDevice().getDeviceId())){
                return mSelf;
            }
            return mDeviceDataManager.findInIdMap(deviceId);
        }
    }

    /**
     * 处理req-send-call的相关请求
     * 默认不做任何处理
     * (只有主机才需处理)
     */
    public DeviceModel handleSendCall(@NonNull RequestDTO<CallModel> requestDTO){
        if(requestDTO.getData() != null){
            CallModel callModel = requestDTO.getData();

            //只接收针对自己的呼叫
            if(!StringUtil.equalsIgnoreCase(mSelf.getDevice().getDeviceId(),callModel.getCalleeDeviceId())
            &&!StringUtil.equalsIgnoreCase(mSelf.getDevice().getDeviceId(),callModel.getCallerDeviceId())){
                return null;
            }
            DeviceModel remoteDevice = getRemoteDevice(callModel);
            if(remoteDevice == null){
                return null;
            }
            //如果相同类型的呼叫已经存在，则不重复处理
            CallModel tmpCallModel = findCallModel(remoteDevice,CallTypeEnum.findById(callModel.getCallType()));
            if(tmpCallModel != null){
                return null;
            }
            callModel.setState(StateEnum.CALLING.getValue());
            updateDeviceStateByCall(callModel,StateEnum.CALLING);
            return remoteDevice;
        }
        return null;
    }

    protected CallModel crateCallModelByCall(Call call,String message){
        try {
            //KLog.i("call info ==> " + call.getRemoteAddress().asString());
            DeviceModel remoteDevice = getDeviceById(Objects.requireNonNull(call.getRemoteAddress().getUsername()));
            if (remoteDevice == null) {
                return null;
            }
            synchronized (mSynObj) {
                CallModel callModel = findCallModel(remoteDevice,CallTypeEnum.NORMAL);
                if(callModel == null) {
                    callModel = new CallModel();
                    callModel.setCaller(call.getDir() == Call.Dir.Outgoing ? mSelf.getBindPhoneNo() : remoteDevice.getBindPhoneNo());
                    callModel.setCallerDeviceId(call.getDir() == Call.Dir.Outgoing ? mSelf.getDevice().getDeviceId() : remoteDevice.getDevice().getDeviceId());
                    callModel.setCallerType(call.getDir() == Call.Dir.Outgoing ? mSelf.getDevice().getDeviceType() : remoteDevice.getDevice().getDeviceType());
                    callModel.setCallee(call.getDir() != Call.Dir.Outgoing ? mSelf.getBindPhoneNo() : remoteDevice.getBindPhoneNo());
                    callModel.setCalleeDeviceId(call.getDir() != Call.Dir.Outgoing ? mSelf.getDevice().getDeviceId() : remoteDevice.getDevice().getDeviceId());
                    callModel.setCalleeType(call.getDir() != Call.Dir.Outgoing ? mSelf.getDevice().getDeviceType() : remoteDevice.getDevice().getDeviceType());
                    callModel.setCallType(CallTypeEnum.NORMAL.getValue());
                    callModel.setEmergencyCall(false);
                    callModel.setStartTime(call.getCallLog().getStartDate());
                    callModel.setCallRef(getCallRef(remoteDevice));
                    addCallModel(remoteDevice,callModel);
                }
                callModel.setAppendPath(call.getParams().getRecordFile());
                callModel.setResult(message);
                if (call.getState() == Call.State.Idle
                        || call.getState() == Call.State.Released) {
                    callModel.setState(StateEnum.CALL_END.getValue());
                    callModel.setStopTime(System.currentTimeMillis());
                }
                if (call.getState() == Call.State.Connected || call.getState() == Call.State.StreamsRunning) {
                    callModel.setState(StateEnum.TALKING.getValue());
                } else {
                    callModel.setState(StateEnum.CALLING.getValue());
                }
                return callModel;
            }
        }catch (Exception e){
            KLog.e(e);
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    public void updateDeviceStateByCall(@NonNull CallModel callModel, @NonNull StateEnum stateEnum){
        KLog.i(String.format("updateDeviceStateByCall callerDeviceId %s,calleeDeviceId %s,callRef %s,callType %d,state %d,nextState %d"
                ,callModel.getCallerDeviceId()
                ,callModel.getCalleeDeviceId()
                ,callModel.getCallRef()
                ,callModel.getCallType()
                ,callModel.getState()
                ,stateEnum.getValue()
        ));
        DeviceModel callerDevice = getDeviceById(callModel.getCallerDeviceId());
        if(callerDevice != null){
            CallTypeEnum callTypeEnum = CallTypeEnum.findById(callModel.getCallType());
            if(callTypeEnum.getMediaType() == CallMediaTypeEnum.STREAM){
                callerDevice.setState(stateEnum);
                callerDevice.setCallModel(callModel);
            }

        }
        DeviceModel calleeDevice = getDeviceById(callModel.getCalleeDeviceId());
        if(calleeDevice != null){
            if(!(StringUtil.equalsIgnoreCase(calleeDevice.getDevice().getDeviceId(),mSelf.getDevice().getDeviceId())
                    && Objects.equals(mSelf.getDevice().getDeviceType(),DeviceTypeEnum.NURSE_STATION_MASTER.getValue()))) {
                //如果被叫是自己，且自己是主机，则不用更新状态
                CallTypeEnum callTypeEnum = CallTypeEnum.findById(callModel.getCallType());
                if(callTypeEnum.getMediaType() == CallMediaTypeEnum.STREAM) {
                    calleeDevice.setState(stateEnum);
                    calleeDevice.setCallModel(callModel);
                }
            }
        }
        DeviceModel remoteDevice = StringUtil.equalsIgnoreCase(mSelf.getDevice().getDeviceId(),callModel.getCallerDeviceId()) ? calleeDevice : callerDevice;
        callModel.setRemoteDevice(remoteDevice);
        if(remoteDevice != null && remoteDevice.getDevice() != null) {
            callModel.setRemotePlace(mPlaceMap.get(remoteDevice.getDevice().getPlaceUid()));
            synchronized (mSynObj){
                addCallModel(remoteDevice,callModel);
            }
        }

        CallInManager.getInstance().updateCallState(callModel);

        EventBus.getDefault().post(callModel, EventBusConstant.HANDLE_STATE_CHANGED);
    }
    public void handleInComingCall(CallModel callModel){

        if(callModel != null){
            callModel.setStartTime(System.currentTimeMillis());
            callModel.setState(StateEnum.CALLING.getValue());
            updateDeviceStateByCall(callModel,StateEnum.CALLING);
            addOperationLog(callModel);
            //通知界面层有呼叫进入
            EventBus.getDefault().post(callModel, EventBusConstant.INCOMING_CALL_NOTICE);

        }
    }

    public void handleConnectedCall(CallModel callModel){
        if(callModel != null){
            callModel.setConnectTime(System.currentTimeMillis());
            callModel.setState(StateEnum.TALKING.getValue());
            updateDeviceStateByCall(callModel,StateEnum.TALKING);
            DeviceModel remoteDevice = getRemoteDevice(callModel);
            if(remoteDevice == null){
                return;
            }
            addOperationLog(callModel);
        }
    }

    public void handleReleaseCall(CallModel callModel){
        if(callModel != null){
            callModel.setStopTime(System.currentTimeMillis());
            callModel.setState(StateEnum.CALL_END.getValue());
            updateDeviceStateByCall(callModel,StateEnum.ONLINE);
            DeviceModel remoteDevice = getRemoteDevice(callModel);
            if(remoteDevice == null){
                return;
            }
            addOperationLog(callModel);
            synchronized (mSynObj){
                removeCallModel(remoteDevice,callModel);
            }
        }
    }

    /**
     * 从设备模块中找到对应类型的呼叫对象
     */
    protected CallModel findCallModel(@NonNull DeviceModel deviceModel, @NonNull CallTypeEnum callTypeEnum){
        synchronized (mSynObj){
            if(callTypeEnum.getMediaType() == CallMediaTypeEnum.STREAM){
                return deviceModel.getCallModel();
            }else{
                if(!StringUtil.isEmpty(deviceModel.getOtherCallList())){
                    for(CallModel callModel : deviceModel.getOtherCallList()){
                        if(Objects.equals(callModel.getCallType(),callTypeEnum.getValue())){
                            return callModel;
                        }
                    }
                }
                return null;
            }
        }
    }

    /**
     * 插入呼叫对象
     */
    protected void addCallModel(@NonNull DeviceModel deviceModel,@NonNull CallModel callModel){
        synchronized (mSynObj){
            CallTypeEnum callTypeEnum = CallTypeEnum.findById(callModel.getCallType());
            if(callTypeEnum.getMediaType() == CallMediaTypeEnum.STREAM){
                deviceModel.setCallModel(callModel);
            }else{
                addOtherCallModel(deviceModel,callModel);
            }
        }
    }

    /**
     * 移除呼叫对象
     */
    protected void removeCallModel(@NonNull DeviceModel deviceModel,@NonNull CallModel callModel){
        synchronized (mSynObj){
            CallTypeEnum callTypeEnum = CallTypeEnum.findById(callModel.getCallType());
            if(callTypeEnum.getMediaType() == CallMediaTypeEnum.STREAM){
                deviceModel.setCallModel(null);
                deviceModel.setState(StateEnum.ONLINE);
            }else{
                removeOtherCallModel(deviceModel,callModel);
            }
        }
    }
    /**
     * 插入其它类型的呼叫列表
     */
    protected void addOtherCallModel(@NonNull DeviceModel deviceModel,@NonNull CallModel callModel){
        synchronized (mSynObj) {
            if (!StringUtil.isEmpty(deviceModel.getOtherCallList())) {
                for (CallModel tmp : deviceModel.getOtherCallList()) {
                    //已经存在了，就不用重复添加了
                    if (StringUtil.equalsIgnoreCase(tmp.getCallRef(), callModel.getCallRef())) {
                        return;
                    }
                }
            }
            if (deviceModel.getOtherCallList() == null) {
                deviceModel.setOtherCallList(new ObservableArrayList<>());
            }
            deviceModel.getOtherCallList().add(callModel);
        }
    }

    /**
     * 从其它类型的呼叫列表中移除呼叫对象
     */
    protected void removeOtherCallModel(@NonNull DeviceModel deviceModel,@NonNull CallModel callModel){
        synchronized (mSynObj) {
            if (!StringUtil.isEmpty(deviceModel.getOtherCallList())) {
                for (CallModel tmp : deviceModel.getOtherCallList()) {
                    //已经存在了，就不用重复添加了
                    if (StringUtil.equalsIgnoreCase(tmp.getCallRef(), callModel.getCallRef())) {
                        deviceModel.getOtherCallList().remove(tmp);
                        return;
                    }
                }
            }
        }
    }

    /**
     * 处理req-update-call-info命令
     */
    public void handleReqUpdateCallInfo(@NonNull RequestDTO<CallModel> requestDTO){
        if(requestDTO.getData() != null){
            CallModel callModel = requestDTO.getData();
            //如果是主被叫方则不用处理
            CallTypeEnum callTypeEnum = CallTypeEnum.findById(callModel.getCallType());
            if(StringUtil.equalsIgnoreCase(callModel.getCallee(),mSelf.getBindPhoneNo())
            || StringUtil.equalsIgnoreCase(callModel.getCaller(),mSelf.getBindPhoneNo())){
                return;
            }

            //第三方的呼叫状态都只能归到其它呼叫里去
            if(callModel.getState() == StateEnum.CALLING.getValue()
                    || callModel.getState() == StateEnum.TALKING.getValue()
                    || callModel.getState() == StateEnum.CALL_TIMING_START.getValue() ){
                //有可能主被叫都不是自己
                //比如病房门口屏、病床一览表
                if(!StringUtil.equalsIgnoreCase(callModel.getCallerDeviceId(),mSelf.getDevice().getDeviceId())) {
                    DeviceModel callerDevice = mDeviceDataManager.findInIdMap(callModel.getCallerDeviceId());
                    addCallModel(callerDevice, callModel);
                    if(callTypeEnum.getMediaType() == CallMediaTypeEnum.STREAM){
                        //如果是流类型的呼叫，则需要设置设备状态
                        callerDevice.setState(StateEnum.findById(callModel.getState()));
                    }
                }
                if(!StringUtil.equalsIgnoreCase(callModel.getCalleeDeviceId(),mSelf.getDevice().getDeviceId())) {
                    DeviceModel calleeDevice = mDeviceDataManager.findInIdMap(callModel.getCalleeDeviceId());
                    addCallModel(calleeDevice, callModel);
                    if(callTypeEnum.getMediaType() == CallMediaTypeEnum.STREAM){
                        //如果是流类型的呼叫，则需要设置设备状态
                        calleeDevice.setState(StateEnum.findById(callModel.getState()));
                    }
                }
            }else{
                if(!StringUtil.equalsIgnoreCase(callModel.getCallerDeviceId(),mSelf.getDevice().getDeviceId())) {
                    DeviceModel callerDevice = mDeviceDataManager.findInIdMap(callModel.getCallerDeviceId());
                    removeCallModel(callerDevice, callModel);
                    if(callTypeEnum.getMediaType() == CallMediaTypeEnum.STREAM){
                        //如果是流类型的呼叫，则需要设置设备状态
                        callerDevice.setState(StateEnum.ONLINE);
                    }
                }
                if(!StringUtil.equalsIgnoreCase(callModel.getCalleeDeviceId(),mSelf.getDevice().getDeviceId())) {
                    DeviceModel calleeDevice = mDeviceDataManager.findInIdMap(callModel.getCalleeDeviceId());
                    removeCallModel(calleeDevice, callModel);
                    if(callTypeEnum.getMediaType() == CallMediaTypeEnum.STREAM){
                        //如果是流类型的呼叫，则需要设置设备状态
                        calleeDevice.setState(StateEnum.ONLINE);
                    }
                }
            }
        }
    }
    //CallSimpleListener的回调
    /**
     * 有呼叫进入
     */
    @Override
    public void onInComingCall(Call call,String message){
        //new Thread(()->{
            CallModel callModel = crateCallModelByCall(call,message);
            callModel.setState(StateEnum.CALLING.getValue());
            handleInComingCall(callModel);
        //}).start();

    }

    /**
     * 呼叫建立
     */
    @Override
    public void onConnectedCall(Call call,String message){
        //new Thread(()->{
            CallModel callModel = crateCallModelByCall(call,message);
            callModel.setState(StateEnum.TALKING.getValue());
            callModel.setConnectTime(System.currentTimeMillis());
            callModel.setWithVideo(call.getParams().isVideoEnabled());
            handleConnectedCall(callModel);
        //}).start();
    }

    /**
     * 呼叫释放
     */
    @Override
    public void onReleaseCall(Call call,String message){
        //new Thread(()->{
            CallModel callModel = crateCallModelByCall(call,message);
            callModel.setState(StateEnum.CALL_END.getValue());
            handleReleaseCall(callModel);
        //}).start();
    }

    /**
     * 修改号码
     * 涉及到主机、分机绑定关系的改变，需要特殊处理
     * @param orgPhoneNo 修改前的分机号
     * @param orgParentNo 修改前的主机号
     * @param phoneNo 修改后的分机号
     * @param parentNo 修改后的主机号
     */
    public void changePhoneNo(String orgPhoneNo,String orgParentNo,String phoneNo,String parentNo){
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_PHONE_NUMBER,phoneNo);
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_PARENT_NUMBER,parentNo);
        mSelf.getDevice().setPhoneNo(phoneNo);
        mSelf.getDevice().setParentNo(parentNo);
    }
    /**
     * 修改自身的信息
     */
    public void changeSelfInfo(@NonNull DeviceModel deviceModel){
        //如果没任何改变就不用修改了
        Device device = deviceModel.getDevice();
        if(!mSelf.getNetConfig().compare(deviceModel.getNetConfig())){
            //修改网络配置
            NetworkManager networkManager = new NetworkManager();
            networkManager.saveConfig(mContext, deviceModel.getNetConfig());
        }
        mSelf.setServiceAddress(deviceModel.getServiceAddress());
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_SERVICE_ADDRESS,deviceModel.getServiceAddress());
        mSelf.setServicePort(deviceModel.getServicePort());
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_SERVICE_PORT,deviceModel.getServicePort());
        mSelf.setDirectCallNo(deviceModel.getDirectCallNo());
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_DIRECT_CALL_NO,deviceModel.getDirectCallNo());
        if(mSelf.compare(deviceModel.getDevice())){
            return;
        }
        mSelf.getDevice().setWholeTitle(deviceModel.getDevice().getWholeTitle());
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_WHOLE_TITLE,deviceModel.getDevice().getWholeTitle());
        String orgPhoneNo = mSelf.getDevice().getPhoneNo();
        String orgParentNo = mSelf.getDevice().getParentNo();
        //修改自身信息
        mSelf.getDevice().setHardwareVersion(device.getHardwareVersion());
        mSelf.getDevice().setSoftwareVersion(device.getSoftwareVersion());

        mSelf.getDevice().setSystemVersion(device.getSystemVersion());
        mSelf.getDevice().setKernelVersion(device.getKernelVersion());
        mSelf.getDevice().setUbootVersion(device.getUbootVersion());
        mSelf.getDevice().setIpAddress(device.getIpAddress());
        mSelf.getDevice().setMacAddress(device.getMacAddress());
        changePhoneNo(orgPhoneNo,orgParentNo,device.getPhoneNo(),device.getParentNo());
        //发送广播通知其它设备，自身信息修改
        RequestDTO<DeviceModel> requestDTO = new RequestDTO<>(mSelf,CommandEnum.REQ_CHANGE_DEVICE_INFO.getId());
        requestDTO.setData(mSelf);
        mHandlerBase.sendMulticast(requestDTO);

        //如果服务器在线，则还要告诉服务器
        mRequestHelper.updateDeviceInfo(requestDTO);
    }


    /**
     * 绑定分区
     */
    public void handleBindGroup(@NonNull RequestDTO<RequestBindGroup> requestDTO){

    }

    /**
     * 绑定位置
     */
    public void handleBindPlace(@NonNull RequestDTO<RequestBindPlace> requestDTO){

    }

    /**
     * 绑定父位置
     */
    public void handleBindPlaceParent(@NonNull RequestDTO<RequestBindPlaceParent> requestDTO){

    }


    /**
     * 更新病员信息
     */
    public void updatePatientInfo(@NonNull RequestDTO<ResponseList<Patient>> requestDTO){
        if(requestDTO.getData() != null) {
            ResponseList<Patient> patientList = CastUtil.cast(requestDTO.getData());
            //暴力更新
            if (patientList.getUpdateType() == UpdateTypeEnum.UPDATE_FORCE.getValue()) {
                if(mPatientUpdateHelper.addData(patientList)){
                    //如果有更新过了，则重新加载缓存
                    loadPlaceToCache();
                }
            } else {
                if (!StringUtil.isEmpty(patientList.getList())) {
                    for (Patient patient : patientList.getList()) {
                        if (patientList.getUpdateType() == UpdateTypeEnum.UPDATE_NORMAL.getValue()) {
                            PatientService.getInstance().updatePatient(patient);
                            if(patient.getBedSn() == null){
                                removeBedPatient(patient);
                            }else{
                                updateBedPatient(patient);
                            }
                        } else {
                            removeBedPatient(patient);
                            PatientService.getInstance().removePatient(patient);

                        }
                    }
                }
            }
        }
    }

    /**
     * 获取病床详情
     */
    public PlaceModel getBedDetail(@NonNull RequestDTO<RequestBedDetail> requestDTO){
        if(requestDTO.getData() != null) {
            RequestBedDetail requestBedDetail = requestDTO.getData();
            return getPlaceModelFromCache(PlaceUtil.genPlaceUid(requestBedDetail.getDeviceId(),PlaceTypeEnum.BED.getName(), requestBedDetail.getBedSn()));
        }
        return null;
    }


    //////////////////将分区（详情）数据优先加入缓存，提升访问效率，方便界面显示操作////////////////////////////////////////////////////////////////

    /**
     * 找到对应的分区
     * @param groupNo 分区区号
     * @return 分区
     */
    public MulticastGroupModel findGroupModelInCache(@NonNull String groupNo){
        return null;
    }

    /**
     * 找到对应的分区
     * @param groupSn 分区序号
     * @return 分区
     */
    public MulticastGroupModel findGroupModelInCacheBySn(@NonNull Integer groupSn){
        return null;
    }

    public void deleteGroupInCache(@NonNull MulticastGroup multicastGroup){

    }

    /**
     * 将分区数据加入缓存
     */
    public void loadGroupToCache(){

    }

    //从缓存中获取分区列表
    public List<MulticastGroupModel> getGroupListFromCache(boolean isBaseGroup){
        return null;
    }

    public List<MulticastGroupModel> convertGroupToGroupModel(@NonNull List<MulticastGroup> groupList){
        List<MulticastGroupModel> groupModelList = new ArrayList<>();
        for (MulticastGroup multicastGroup : groupList) {
            MulticastGroupModel groupModel = GroupUtil.convertToModel(multicastGroup,mContext,getMasterNo());
            groupModelList.add(groupModel);
        }
        return groupModelList;
    }

    /**
     * 通过分区序号找到分区
     */
    public MulticastGroupModel findMulticastGroupModelInCacheBySn(@NonNull String masterDeviceId, @NonNull Integer groupSn){
        return null;
    }

    /**
     * 获取病房详情
     */
    public PlaceModel getRoomDetail(@NonNull RequestDTO<RequestRoomDetail> requestDTO){
        if(requestDTO.getData() != null) {
            RequestRoomDetail requestRoomDetail = requestDTO.getData();
            return getPlaceModelFromCache(PlaceUtil.genPlaceUid(requestRoomDetail.getDeviceId(),PlaceTypeEnum.ROOM.getName(), requestRoomDetail.getRoomSn()));
        }
        return null;
    }

    /**
     * 获取主机号码
     */
    abstract String getMasterNo();


    /**
     * 更新病员信息
     * 同时更新病床位置与病员信息的关系
     */
    abstract void updateBedPatient(@NonNull Patient patient);

    /**
     * 从病床位置中移除病员信息
     */
    protected void removeBedPatient(@NonNull Patient patient){
        synchronized (mSynObj){
            for(PlaceModel placeModel : mPlaceMap.values()){
                for(PatientModel tmp : placeModel.getPatientModelList()){
                    if(tmp != null
                            && tmp.getPatient() != null
                            && StringUtil.equalsIgnoreCase(tmp.getPatient().getSerialNumber(),patient.getSerialNumber())){
                        placeModel.getPatientModelList().remove(tmp);
                        return;
                    }
                }
            }
        }
    }



    //////////////////位置信息缓存操作////////////////////////////////////////////////////////////////

    @NonNull
    protected ObservableArrayList<DeviceModel> getDeviceListByPlace(String placeUid){
        synchronized (mSynObj){
            ObservableArrayList<DeviceModel> deviceModelList = new ObservableArrayList<>();
            List<DeviceModel> tmpList = mDeviceDataManager.findInPlaceMap(placeUid);
            if(!StringUtil.isEmpty(tmpList)) {
                deviceModelList.addAll(tmpList);
            }
            return deviceModelList;
        }
    }

    private void findDeviceList(ObservableArrayList<DeviceModel> deviceModelList,String parentNo,DeviceTypeEnum deviceTypeEnum,Integer placeSn,String placeUid){
        String bindPhoneNo = DeviceModel.getBindPhoneNo(deviceTypeEnum,placeSn,parentNo);
        List<DeviceModel> tmpList = mDeviceDataManager.findInPhoneNoMap(bindPhoneNo);
        if(!StringUtil.isEmpty(tmpList)){
            for(DeviceModel deviceModel : tmpList){
                if(!StringUtil.equalsIgnoreCase(placeUid,deviceModel.getDevice().getPlaceUid())){
                    if(!StringUtil.isEmpty(deviceModel.getDevice().getPlaceUid())){
                        mDeviceDataManager.removeFromPlaceMap(deviceModel);
                    }
                    deviceModel.getDevice().setPlaceUid(placeUid);
                    DeviceService.getInstance().updateDevice(deviceModel);
                    mDeviceDataManager.addToPlaceMap(deviceModel);
                }
            }
            deviceModelList.addAll(tmpList);
        }
    }


    /**
     * 将位置从父位置移除
     */
    protected PlaceModel removeFromParent(@NonNull PlaceModel placeModel){
        PlaceModel parentModel = mPlaceMap.get(placeModel.getPlace().getParentUid());
        if(parentModel != null){
            if(parentModel.getChildren() != null){
                parentModel.getChildren().remove(placeModel);
            }
        }
        return parentModel;
    }

    /**
     * 将位置数据插入父位置的子节点
     */
    protected void addPlaceChild(PlaceModel parentPlace,PlaceModel child){
        if(parentPlace != null){
            ObservableArrayList<PlaceModel> subList = parentPlace.getChildren();
            if(subList == null){
                subList = new ObservableArrayList<>();
                parentPlace.setChildren(subList);
            }
            subList.add(child);
        }
    }

    /**
     * 对列表进行重排序
     * 否则从map里取上来的是乱的
     */
    protected List<PlaceModel> sortPlaceModelList(List<PlaceModel> placeModelList){
        //排序
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            placeModelList.sort(Comparator.comparingInt(t -> (t.getPlace().getPlaceType() * 1000 + t.getPlace().getPlaceSn())));
        }
        return placeModelList;
    }

    /**
     * 对列表进行重排序
     * 否则从map里取上来的是乱的
     */
    protected List<MulticastGroupModel> sortMulticastGroupList(List<MulticastGroupModel> multicastGroupModelList){
        //排序
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            multicastGroupModelList.sort(Comparator.comparingInt(t -> (t.getMulticastGroup().getGroupSn())));
        }
        return multicastGroupModelList;
    }
    /**
     * 将位置上的数据加入位置节点
     */
    private void loadPlaceData(@NonNull PlaceModel placeModel){
        Place place = placeModel.getPlace();
        placeModel.setDeviceModelList(getDeviceListByPlace(place.getUid()));
        placeModel.getPatientModelList().clear();
        //如果是病床，则还要获取病员信息
        if(placeModel.getPlace().getPlaceType() == PlaceTypeEnum.BED.getValue()){
            PatientModel patientModel = PatientService.getInstance().getPatient(placeModel.getPlace().getMasterDeviceId(),placeModel.getPlace().getPlaceSn());
            if(patientModel != null) {
                placeModel.getPatientModelList().add(patientModel);
            }
        }
        //获取相机
        placeModel.setIpCamera(IPCameraService.getInstance().getIPCamera(place.getMasterDeviceId(),place.getUid()));
    }
    /**
     * 将位置信息加入缓存
     */
    public void loadPlaceToCache(){
        mPlaceMap.clear();
        DeviceModel masterDevice = mDeviceDataManager.findOneInPhoneNoMap(DeviceModel.getMasterPhoneNo(getMasterNo()));
        if(masterDevice == null){
            return;
        }
        List<Place> placeList = PlaceService.getInstance().getPlaceList(masterDevice.getDevice().getDeviceId());
        if(!StringUtil.isEmpty(placeList)){
            for(Place place : placeList){
                PlaceModel placeModel = new PlaceModel();
                placeModel.setPlace(place);
                loadPlaceData(placeModel);
                mPlaceMap.put(place.getUid(),placeModel);
            }
            //再遍历一遍，将子节点挂载入父节点
            for(PlaceModel placeModel : mPlaceMap.values()){
                if(!StringUtil.isEmpty(placeModel.getPlace().getParentUid())) {
                    PlaceModel parentModel = mPlaceMap.get(placeModel.getPlace().getParentUid());
                    addPlaceChild(parentModel,placeModel);
                }
            }
            //给自己再特殊处理一遍
            PlaceModel placeModel = findPlaceByDevice(mSelf);
            if(placeModel != null){
                mSelf.getDevice().setPlaceUid(placeModel.getPlace().getUid());
            }
            //通知界面层缓存更新了
            EventBus.getDefault().post(mSelf, EventBusConstant.HANDLE_CACHE_UPDATED);
        }
    }

    /**
     * 从缓存中获取所有的位置列表
     * @param placeType 位置类型，-1表示全部
     * @param masterDeviceId 此位置绑定的主机id
     */
    @NonNull
    private List<PlaceModel> getPlaceListFromCache(int placeType, String masterDeviceId){
        List<PlaceModel> placeModelList = new ArrayList<>();
        for(PlaceModel placeModel : mPlaceMap.values()){
            if((placeType == Constant.INVALID_LIMIT || Objects.equals(placeModel.getPlace().getPlaceType(),placeType) )
                    && (StringUtil.isEmpty(masterDeviceId) || StringUtil.equalsIgnoreCase(placeModel.getPlace().getMasterDeviceId(),masterDeviceId))){
                placeModelList.add(placeModel);
            }
        }
        return placeModelList;
    }
    /**
     * 从缓存中获取位置列表
     * @param placeType 位置类型，-1表示全部
     * @param masterDeviceId 此位置绑定的主机id
     * @param offset 偏移位置
     * @param limit 每次获取的数量，-1表示全部，此时offset不起作用
     */
    public List<PlaceModel> getPlaceListFromCache(int placeType,String masterDeviceId,int offset,int limit){
        if(placeType == Constant.INVALID_LIMIT){
            if(limit == Constant.INVALID_LIMIT){
                return sortPlaceModelList(getPlaceListFromCache(placeType,masterDeviceId));
            }else{
                List<PlaceModel> placeModelList = sortPlaceModelList(getPlaceListFromCache(placeType,masterDeviceId));
                int realSize = Math.min(limit, (placeModelList.size() - offset));
                return placeModelList.subList(offset,offset + realSize);
            }
        }else{
            List<PlaceModel> placeModelList = sortPlaceModelList(getPlaceListFromCache(placeType,masterDeviceId));
            if(limit == Constant.INVALID_LIMIT){
                return placeModelList;
            }else{
                int realSize = Math.min(limit, (placeModelList.size() - offset));
                return placeModelList.subList(offset,offset + realSize);
            }
        }
    }
    /**
     * 从缓存中获取某个父位置下的子位置列表
     * @param parentUid 父uid
     * @return 子设备列表
     */
    public List<PlaceModel> getPlaceListByParentFromCache(String parentUid){
        synchronized (mSynObj) {
            PlaceModel placeModel = mPlaceMap.get(parentUid);
            if (placeModel != null) {
                return sortPlaceModelList(placeModel.getChildren());
            }
            return null;
        }
    }

    /**
     * 获取某个位置的所有父位置
     */
    public List<PlaceModel> getParentPlaceList(@NonNull PlaceModel placeModel){
        List<PlaceModel> placeModelList = new ArrayList<>();
        if(!StringUtil.isEmpty(placeModel.getPlace().getParentUid())){
            PlaceModel parentPlace = mPlaceMap.get(placeModel.getPlace().getParentUid());
            if(parentPlace != null){
                placeModelList.add(parentPlace);
                List<PlaceModel> tmpList = getParentPlaceList(parentPlace);
                if(!StringUtil.isEmpty(tmpList)) {
                    placeModelList.addAll(tmpList);
                }
            }
        }
        return placeModelList;
    }

    /**
     * 从缓存中获取某个分组下的子位置列表
     * @param groupSn 分组序号
     * @return 设备列表
     */
    public List<PlaceModel> getPlaceListByGroupFromCache(int groupSn){
        synchronized (mSynObj) {
            List<PlaceModel> placeModelList = new ArrayList<>();
            for (PlaceModel placeModel : mPlaceMap.values()) {
                if (Objects.equals(groupSn, placeModel.getPlace().getGroupSn())) {
                    placeModelList.add(placeModel);
                }
            }
            return sortPlaceModelList(placeModelList);
        }
    }

    /**
     * 从缓存中获取位置详情
     * @param uid 位置id
     * @return 位置详情
     */
    public PlaceModel getPlaceModelFromCache(String uid){
        return mPlaceMap.get(uid);
    }

    /**
     * 更新位置信息
     */
    public void updatePlaceInfo(@NonNull RequestDTO<ResponseList<Place>> requestDTO){
        synchronized (mSynObj) {
            if (requestDTO.getData() != null) {
                ResponseList<Place> placeList = CastUtil.cast(requestDTO.getData());
                if (placeList.getUpdateType() == UpdateTypeEnum.UPDATE_FORCE.getValue()) {
//                    PlaceService.getInstance().deleteAll();
//                    PlaceService.getInstance().insertAll(placeList.getList());
//                    loadPlaceToCache();
                    //强制更新的数据要用更新管理器来慎重更新
                    if(mPlaceUpdateHelper.addData(placeList)){
                        //如果有更新过了，则重新加载缓存
                        loadPlaceToCache();
                    }
                } else {
                    if (!StringUtil.isEmpty(placeList.getList())) {
                        for (Place place : placeList.getList()) {
                            if (placeList.getUpdateType() == UpdateTypeEnum.UPDATE_NORMAL.getValue()) {
                                //找到位置的原始数据
                                PlaceModel placeModel = mPlaceMap.get(place.getUid());
                                if(placeModel != null){
                                    if(!placeModel.getPlace().isSamePlace(place)){
                                        //只有数据有不同才需要处理
                                        //父节点是否改变
                                        if(!StringUtil.equalsIgnoreCase(placeModel.getPlace().getParentUid(),place.getParentUid())){
                                            //若改变，则找出之前的父节点
                                            //从它的子节点删除
                                            removeFromParent(placeModel);
                                            if (!StringUtil.isEmpty(place.getParentUid())){
                                                PlaceModel curParentModel = mPlaceMap.get(place.getParentUid());
                                                addPlaceChild(curParentModel,placeModel);
                                            }
                                        }
                                        placeModel.setPlace(place);
                                    }
                                }else{
                                    PlaceService.getInstance().updatePlace(place);
                                    PlaceModel tmpModel = new PlaceModel();
                                    tmpModel.setPlace(place);
                                    //如果是全新插入的数据，则
                                    PlaceModel curParentModel = mPlaceMap.get(place.getParentUid());
                                    addPlaceChild(curParentModel,tmpModel);

                                    //加载位置上的数据
                                    loadPlaceData(tmpModel);
                                }

                            } else {
                                PlaceModel tmpModel = new PlaceModel();
                                tmpModel.setPlace(place);
                                removeFromParent(tmpModel);
                                PlaceService.getInstance().deletePlace(place);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新分组信息
     */
    public void updateGroupInfo(@NonNull RequestDTO<ResponseList<MulticastGroup>> requestDTO){
        synchronized (mSynObj) {
            if (requestDTO.getData() != null) {
                ResponseList<MulticastGroup> groupList = CastUtil.cast(requestDTO.getData());
                if (groupList.getUpdateType() == UpdateTypeEnum.UPDATE_FORCE.getValue()) {
                    MulticastGroupService.getInstance().deleteAll();
                    MulticastGroupService.getInstance().insertAll(groupList.getList());
                } else {
                    if (!StringUtil.isEmpty(groupList.getList())) {
                        for (MulticastGroup multicastGroup : groupList.getList()) {
                            if (groupList.getUpdateType() == UpdateTypeEnum.UPDATE_NORMAL.getValue()) {
                               MulticastGroupService.getInstance().updateMulticastGroup(multicastGroup);
                            } else {
                                MulticastGroupService.getInstance().deleteMulticastGroup(multicastGroup);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 正式请求处理
     */
    abstract AjaxResult handleRequest(@NonNull RequestData requestData);

    /**
     * req-update-bed-template请求的处理
     * 更新床头屏模板
     */
    public void updateBedTemplate(RequestDTO<BedScreenTemplate> requestDTO){

    }
    /**
     * req-update-room-template请求的处理
     * 更新门口屏模板
     */
    public void updateRoomTemplate(RequestDTO<RoomScreenTemplate> requestDTO){

    }
    /**
     * req-get-bed-template请求的处理
     * 获取床头屏模板
     */
    public BedScreenTemplate getBedTemplate(RequestDTO<RequestBedScreenTemplate> requestDTO){
        return null;
    }
    /**
     * req-get-room-template请求的处理
     * 获取病房门口屏模板
     */
    public RoomScreenTemplate getRoomTemplate(RequestDTO<RequestRoomScreenTemplate> requestDTO){
        return null;
    }

    /**
     * req-update-capability请求的处理
     * 更新设备信息
     */
    public void handleUpdateCapability(@NonNull RequestDTO<RequestUpdateCapability> requestDTO){
        if(requestDTO.getData() != null){
            RequestUpdateCapability requestUpdateCapability = requestDTO.getData();
            DeviceModel masterDevice = getDeviceByPhoneNo(DeviceModel.getMasterPhoneNo(getMasterNo()));
            if(masterDevice == null || !StringUtil.equalsIgnoreCase(masterDevice.getDevice().getDeviceId(),requestUpdateCapability.getMasterDeviceId())){
                //如果不是自己的主机，则不予配置
                return;
            }
            PlaceModel placeModel = mPlaceMap.get(requestUpdateCapability.getPlaceUid());
            if(placeModel != null){
                if(placeModel.getIpCamera() != null){
                    //如果之前就有
                    if(requestUpdateCapability.isHasIPC()){
                        //更新
                        IPCamera ipCamera = requestUpdateCapability.getIpCamera();
                        if(ipCamera != null){
                            ipCamera.setUid(placeModel.getIpCamera().getUid());
                            IPCameraService.getInstance().updateIPCamera(ipCamera);
                            placeModel.setIpCamera(ipCamera);
                        }
                    }else{
                        //删除
                        IPCameraService.getInstance().deleteIPCamera(placeModel.getIpCamera());
                        placeModel.setIpCamera(null);
                    }
                }else{
                    if(requestUpdateCapability.isHasIPC()){
                        IPCameraService.getInstance().updateIPCamera(requestUpdateCapability.getIpCamera());
                        placeModel.setIpCamera(IPCameraService.getInstance().getIPCamera(requestUpdateCapability.getMasterDeviceId(),requestUpdateCapability.getPlaceUid()));
                    }
                }

                if(requestUpdateCapability.isHasIPC()) {
                    //需要通知界面，此时如果正在通话的话，需要考虑刷新摄像头
                    EventBus.getDefault().post(requestUpdateCapability.getIpCamera(),EventBusConstant.HANDLE_IPC_UPDATED);
                }
            }
        }
    }

    /**
     * 处理托管请求
     */
    public void handleReqTrust(RequestDTO<RequestTrust> trustRequestDTO){

    }

    /**
     * 处理托管回复
     */
    public void handleRspTrust(RequestDTO<ResponseTrust> requestDTO){

    }

    /**
     * 请求进行隐私设置
     */
    public void handleReqPrivacySet(@NonNull RequestDTO<RequestPrivacy> requestDTO){
        if(requestDTO.getData() != null){
            RequestPrivacy requestPrivacy = requestDTO.getData();
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PRIVACY_ENABLE,requestPrivacy.isEnable());
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PRIVACY_DEVICE_TYPE,requestPrivacy.getDeviceTypes());
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PRIVACY_RULE_HIDE_START,requestPrivacy.getHideStart());
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PRIVACY_RULE_HIDE_LENGTH,requestPrivacy.getHideLength());
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PRIVACY_RULE_REPLACE_CHARACTER,requestPrivacy.getReplaceCharacter());
        }
    }

    /**
     * 获取隐私设置
     */
    public RequestPrivacy getPrivacySet(){
        RequestPrivacy requestPrivacy = new RequestPrivacy();
        requestPrivacy.setEnable(SettingsHelper.getInstance(mContext).getBoolean(PreferenceConstant.PRIVACY_ENABLE,mContext.getResources().getBoolean(R.bool.default_privacy_enable)));
        requestPrivacy.setDeviceTypes(SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PRIVACY_DEVICE_TYPE,mContext.getString(R.string.default_privacy_device_type)));
        requestPrivacy.setHideStart(SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PRIVACY_RULE_HIDE_START,mContext.getResources().getInteger(R.integer.default_privacy_hide_start)));
        requestPrivacy.setHideLength(SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PRIVACY_RULE_HIDE_LENGTH,mContext.getResources().getInteger(R.integer.default_privacy_hide_length)));
        requestPrivacy.setReplaceCharacter(SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PRIVACY_RULE_REPLACE_CHARACTER,mContext.getString(R.string.default_privacy_rule_replace_character)));
        return requestPrivacy;
    }


    /**
     * 是否启用隐私政策
     * 主机永远不需要启用
     */
    public boolean isPrivacyEnable(@NonNull RequestPrivacy requestPrivacy){
        //如果启用，则还需判断是否对自己这个型号启用
        if(!requestPrivacy.isEnable()){
            //如果不启用，则直接不启用
            return false;
        }
        List<DeviceTypeEnum> deviceTypeEnumList = requestPrivacy.getDeviceTypeList();
        if(!StringUtil.isEmpty(deviceTypeEnumList)){
            for(DeviceTypeEnum deviceTypeEnum : deviceTypeEnumList){
                if(deviceTypeEnum == getSelfDeviceType()){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 根据隐私政策获取病员姓名
     */
    public String getPatientName(PatientModel patientModel){
        if(patientModel == null || patientModel.getPatient() == null){
            return "";
        }
        RequestPrivacy requestPrivacy = getPrivacySet();
        if(!isPrivacyEnable(requestPrivacy)){
            //没启用隐私政策，直接返回全面
            return patientModel.getPatient().getName();
        }

        //如果有隐私政策，则做名字替换
        StringBuilder str = new StringBuilder(patientModel.getPatient().getName());
        return str.replace(requestPrivacy.getHideStart()
                ,requestPrivacy.getHideStart() + requestPrivacy.getHideLength()
                ,requestPrivacy.getReplaceCharacter()).toString();
    }

    /**
     * 根据年龄处理策略获取病员年龄
     */
    public String getPatientAge(PatientModel patientModel){
        if(patientModel == null || patientModel.getPatient() == null){
            return "";
        }
        if(patientModel.getPatient().getAge() != null){
            return patientModel.getPatient().getAge() + mContext.getString(R.string.age_name);
        }
        if(!StringUtil.isEmpty(patientModel.getPatient().getIdCard())){
            try {
                Date dob = IdCardUtil.getBirthDayFromIdCard(patientModel.getPatient().getIdCard());
                if(dob == null){
                    return "";
                }
                //是否有身份证，有则通过身份证计算
                boolean useNominalAge = SettingsHelper.getInstance(mContext).getBoolean(PreferenceConstant.AGE_RULE_USE_NOMINAL, mContext.getResources().getBoolean(R.bool.default_age_rule_use_nominal));
                Calendar cal = Calendar.getInstance();
                Calendar dobCal = Calendar.getInstance();
                dobCal.setTime(dob);
                if (useNominalAge) {
                    //如果计算虚岁，则为出生年份+1
                    return cal.get(Calendar.YEAR) - dobCal.get(Calendar.YEAR) + 1 + mContext.getString(R.string.age_name);
                }else{
                    //如果是周岁，则还需要根据年龄策略判断
                    int yearMonthAge = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.AGE_RULE_SHOW_YEAR_MONTH_AGE,mContext.getResources().getInteger(R.integer.default_age_rule_show_year_month_age));
                    int monthDayAge = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.AGE_RULE_SHOW_MONTH_DAY_AGE,mContext.getResources().getInteger(R.integer.default_age_rule_show_month_day_age));
                    //先计算周岁
                    int age = IdCardUtil.getAgeForIdCard(patientModel.getPatient().getIdCard());
                    //再计算不满周岁的月份
                    int month = 12 + cal.get(Calendar.MONTH) - dobCal.get(Calendar.MONTH);
                    //当前天如果比月大，则还需再减一个月的时间
                    month -= (cal.get(Calendar.DAY_OF_MONTH) > dobCal.get(Calendar.DAY_OF_MONTH) ? 1 : 0);
                    if(age >= yearMonthAge){
                        //如果大于需要显示月份的年龄，则直接显示周岁即可
                        return age + mContext.getString(R.string.age_name);
                    }else if(age * 12 + month >= monthDayAge){
                        //如果是显示月份的年龄，则显示周岁加月份
                        return age + mContext.getString(R.string.age_name) + month + mContext.getString(R.string.month_name);
                    }else {
                        int day = cal.get(Calendar.DAY_OF_MONTH) > dobCal.get(Calendar.DAY_OF_MONTH) ? cal.get(Calendar.DAY_OF_MONTH) - dobCal.get(Calendar.DAY_OF_MONTH) : 30 + dobCal.get(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DAY_OF_MONTH);
                        //否则显示月份加天数
                        return age * 12 + month + mContext.getString(R.string.month_name) + day + mContext.getString(R.string.day_name);
                    }
                }
            }catch (Exception e){
                KLog.e(e);
                return "";
            }
        }
        return "";
    }

    /**
     * 获取音量设置
     */
    public RequestVolumeSet getVolumeSet(){
        RequestVolumeSet requestVolumeSet = new RequestVolumeSet();
        requestVolumeSet.setMasterSpeakerDayVolume(SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.MASTER_SPEAKER_DAY_VOLUME,mContext.getResources().getInteger(R.integer.default_master_speaker_day_volume)));
        requestVolumeSet.setMasterRingDayVolume(SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.MASTER_RING_DAY_VOLUME,mContext.getResources().getInteger(R.integer.default_master_ring_day_volume)));
        requestVolumeSet.setMasterKeyDayVolume(SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.MASTER_KEY_DAY_VOLUME,mContext.getResources().getInteger(R.integer.default_master_key_day_volume)));
        requestVolumeSet.setSlaveSpeakerDayVolume(SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.SLAVE_SPEAKER_DAY_VOLUME,mContext.getResources().getInteger(R.integer.default_slave_speaker_day_volume)));

        requestVolumeSet.setNightVolumeSetEnable(SettingsHelper.getInstance(mContext).getBoolean(PreferenceConstant.NIGHT_VOLUME_SET_ENABLE,mContext.getResources().getBoolean(R.bool.default_night_volume_set_enable)));

        requestVolumeSet.setMasterSpeakerNightVolume(SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.MASTER_SPEAKER_NIGHT_VOLUME,mContext.getResources().getInteger(R.integer.default_master_speaker_night_volume)));
        requestVolumeSet.setMasterRingNightVolume(SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.MASTER_RING_NIGHT_VOLUME,mContext.getResources().getInteger(R.integer.default_master_ring_night_volume)));
        requestVolumeSet.setMasterKeyNightVolume(SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.MASTER_KEY_NIGHT_VOLUME,mContext.getResources().getInteger(R.integer.default_master_key_night_volume)));
        requestVolumeSet.setSlaveSpeakerNightVolume(SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.SLAVE_SPEAKER_NIGHT_VOLUME,mContext.getResources().getInteger(R.integer.default_slave_speaker_night_volume)));

        return requestVolumeSet;
    }

    /**
     * 设置音量
     */
    public void handleReqVolumeSet(@NonNull RequestDTO<RequestVolumeSet> requestDTO){
        if(requestDTO.getData() != null){
            RequestVolumeSet requestVolumeSet = requestDTO.getData();
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.MASTER_SPEAKER_DAY_VOLUME,requestVolumeSet.getMasterSpeakerDayVolume());
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.MASTER_RING_DAY_VOLUME,requestVolumeSet.getMasterRingDayVolume());
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.MASTER_KEY_DAY_VOLUME,requestVolumeSet.getMasterKeyDayVolume());
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.SLAVE_SPEAKER_DAY_VOLUME,requestVolumeSet.getSlaveSpeakerDayVolume());

            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.NIGHT_VOLUME_SET_ENABLE,requestVolumeSet.isNightVolumeSetEnable());

            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.MASTER_SPEAKER_NIGHT_VOLUME,requestVolumeSet.getMasterSpeakerNightVolume());
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.MASTER_RING_NIGHT_VOLUME,requestVolumeSet.getMasterRingNightVolume());
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.MASTER_KEY_NIGHT_VOLUME,requestVolumeSet.getMasterKeyNightVolume());
            SettingsHelper.getInstance(mContext).putData(PreferenceConstant.SLAVE_SPEAKER_NIGHT_VOLUME,requestVolumeSet.getSlaveSpeakerNightVolume());
            setVolume(requestVolumeSet);
        }
    }

    /**
     * 是否使用夜间音量设置
     */
    public boolean isUseNightVolumeSet(@NonNull RequestVolumeSet requestVolumeSet){
        if(!requestVolumeSet.isNightVolumeSetEnable()){
            return false;
        }
        return TimeSlotManager.getInstance().isNight(System.currentTimeMillis());
    }
    /**
     * 获取通话音量
     */
    abstract int getSpeakerVolume(RequestVolumeSet requestVolumeSet);

    /**
     * 设置音量
     */
    public void setVolume(RequestVolumeSet requestVolumeSet){
        AudioManager am = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        //设置通话音量
        int speakerVolume = getSpeakerVolume(requestVolumeSet);
        am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, VolumeUtil.appToSys(speakerVolume),AudioManager.FLAG_PLAY_SOUND);
        //设置振铃音量
        int ringVolume = isUseNightVolumeSet(requestVolumeSet) ? requestVolumeSet.getMasterRingNightVolume() : requestVolumeSet.getMasterRingDayVolume();
        am.setStreamVolume(AudioManager.STREAM_MUSIC,VolumeUtil.appToSys(ringVolume),AudioManager.FLAG_PLAY_SOUND);
        //设置按键音量
        int keyVolume = isUseNightVolumeSet(requestVolumeSet) ? requestVolumeSet.getMasterKeyNightVolume() : requestVolumeSet.getMasterKeyDayVolume();
        am.setStreamVolume(AudioManager.STREAM_SYSTEM,VolumeUtil.appToSys(keyVolume),AudioManager.FLAG_PLAY_SOUND);
    }

    /**
     * 释放呼叫
     */
    public void releaseCall(String bindPhoneNo,CallTypeEnum callTypeEnum){

    }

    /**
     * 清理呼叫
     */
    public void clearCall(CallModel callModel){
        DeviceModel deviceModel = getRemoteDevice(callModel);
        if(deviceModel != null){
            removeCallModel(deviceModel,callModel);
            CallTypeEnum callTypeEnum = CallTypeEnum.findById(callModel.getCallType());
            if(callTypeEnum.getMediaType() == CallMediaTypeEnum.STREAM) {
                //设置状态为在线
                callModel.setState(StateEnum.CALL_END.getValue());
                updateDeviceStateByCall(callModel,StateEnum.ONLINE);
            }
        }
    }

    /**
     * 判断这是不是对自己有效的广播
     * @param deviceModel 广播消息发送方
     * @return true 有效，false 无效
     */
    public boolean validateMulticast(@NonNull DeviceModel deviceModel){
        //首先第一点就是不能接收自己发送的广播
        return !StringUtil.equalsIgnoreCase(deviceModel.getDevice().getDeviceId(), mSelf.getDevice().getDeviceId());
    }
}
