package com.hcs.android.business.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.business.WorkHandler;
import com.hcs.android.business.constant.CallTypeEnum;
import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.EventBusConstant;
import com.hcs.android.business.constant.InitStateEnum;
import com.hcs.android.business.constant.PlaceTypeEnum;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.constant.StateEnum;
import com.hcs.android.business.controller.WebController;
import com.hcs.android.business.entity.BedScreenTemplate;
import com.hcs.android.business.entity.CallModel;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.MulticastGroup;
import com.hcs.android.business.entity.MulticastGroupModel;
import com.hcs.android.business.entity.OperationLogModel;
import com.hcs.android.business.entity.Patient;
import com.hcs.android.business.entity.PatientModel;
import com.hcs.android.business.entity.Place;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.entity.RequestAudioMulticast;
import com.hcs.android.business.entity.RequestBedDetail;
import com.hcs.android.business.entity.RequestBedScreenTemplate;
import com.hcs.android.business.entity.RequestBindGroup;
import com.hcs.android.business.entity.RequestBindPlace;
import com.hcs.android.business.entity.RequestBindPlaceParent;
import com.hcs.android.business.entity.RequestConfig;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestData;
import com.hcs.android.business.entity.RequestPrivacy;
import com.hcs.android.business.entity.RequestRoomDetail;
import com.hcs.android.business.entity.RequestRoomScreenTemplate;
import com.hcs.android.business.entity.RequestTrust;
import com.hcs.android.business.entity.RequestUpdateCapability;
import com.hcs.android.business.entity.RequestVolumeSet;
import com.hcs.android.business.entity.ResponseConfig;
import com.hcs.android.business.entity.ResponseDeviceStatus;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.entity.ResponseTrust;
import com.hcs.android.business.entity.RoomScreenTemplate;
import com.hcs.android.business.request.model.RequestHelper;
import com.hcs.android.business.request.model.RetrofitHelper;
import com.hcs.android.business.service.DictService;
import com.hcs.android.call.api.ChatHelper;
import com.hcs.android.call.api.FriendHelper;
import com.hcs.android.call.api.LinphonePreferences;
import com.hcs.android.call.api.PhoneManager;
import com.hcs.android.common.multicast.MulticastHelper;
import com.hcs.android.common.network.NetConfig;
import com.hcs.android.common.network.NetConfigConstants;
import com.hcs.android.common.network.NetworkManager;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.PermissionCheckUtil;
import com.hcs.android.common.util.RobustTimer;
import com.hcs.android.common.util.RobustTimerTask;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.TTSUtil;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.maintain.DeviceBaseManager;
import com.hcs.android.maintain.MaintainManager;
import com.hcs.android.maintain.SNTPManager;
import com.hcs.android.server.entity.ObservableData;
import com.hcs.android.server.web.AjaxResult;
import com.hcs.android.server.web.WebServer;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * 总的工作入口
 */
public class WorkManager implements WorkHandler.HandlerListener {
    private final Context mContext;

    private InitStateEnum mInitStateEnum = InitStateEnum.NONE;
    /**
     * 需要等待完成的信号数量
     */
    private final static int MAX_INITIALIZE_COUNT = 2;
    private final Semaphore mInitSem = new Semaphore(0);

    /**
     * 是否第一次登录
     */
    private boolean mFirstLogin = true;

    /**
     * 同步专用对象
     */
    private final Object mSynObj = new Object();

    private MulticastHelper mMulticastHelper = null;


    private boolean mRunning = false;
    private final Semaphore mWorkSem = new Semaphore(0);
    private final WorkHandler mWorkHandler;
    private final HandlerBase mHandlerBase;
    private RobustTimer mWorkTimer;
    private DeviceManager mDeviceManager = null;

    private IInitListener mInitListener;

    private final WebController mWebController;

    private static final class MInstanceHolder {
        @SuppressLint("StaticFieldLeak")
        static final WorkManager mInstance = new WorkManager();
    }

    public static WorkManager getInstance(){
        return MInstanceHolder.mInstance;
    }

    private WorkManager(){
        mContext = BusinessApplication.getAppContext();
        //注册事件管道
        EventBus.getDefault().register(this);
        mWorkHandler = new WorkHandler(Looper.getMainLooper());
        mWorkHandler.setHandlerListener(this);
        mHandlerBase = new HandlerBase(mWorkHandler);

        //创建web服务
        WebServer.getInstance().startServer();

        //初始化运维管理器
        MaintainManager.getInstance().init();

        //添加设备信息改变监听器
        DeviceBaseManager.getInstance().setDeviceInfoChangeListener(deviceBaseInfo -> {
            DeviceModel deviceModel = getSelfInfo();
            if(deviceModel != null){
                deviceModel.setNetConfig(deviceBaseInfo.getNetConfig());
                reloadConfig();
            }
        });

        //开启时间同步功能
        SNTPManager.getInstance().startSNTPClient();

        //TTS预热，否则可能导致一开始无声
        TTSUtil.getInstance(mContext);

        mWebController = new WebController();
    }
    public DeviceManager getDeviceManager(){
        return mDeviceManager;
    }

    public RequestHelper getRequestHelper(){
        return mDeviceManager.getRequestHelper();
    }
    private void init(Context context){
        //开启初始化是否完成的监听器
        initializeWaiter();

        mDeviceManager = DeviceFactory.getInstance().getDeviceManager(mContext,DeviceFactory.getInstance().getDeviceType(mContext));
        mDeviceManager.setHandlerBase(mHandlerBase);

        //创建呼叫服务
        PhoneManager.getInstance().init(mContext);
        LinphonePreferences.instance().setContext(mContext);
        //启动呼叫服务
        PhoneManager.getInstance().start(()->{
            //设置消息监听器
            ChatHelper.setChatListener((chatRoom, chatMessage) -> new Thread(()-> mWebController.dispatchMessage(chatMessage.getTextContent())).start());
            //设置主机的默认username,displayName
            LinphonePreferences.instance().setDefaultUsername(getSelfInfo().getPhoneNumber());
            LinphonePreferences.instance().setDefaultDisplayName(getSelfInfo().getBindPhoneNo());

            //全部设置成自动应答机制
            //所有呼叫都是先发送message消息给被叫方
            //被叫如果要接通，则发送INVITE消息过来
            LinphonePreferences.instance().enableAutoAnswer(true);
            LinphonePreferences.instance().setAutoAnswerTime(0);

            mDeviceManager.startCallListener();

            //启动成功后,更新好友列表
            updateLinphoneFriend();

            mInitSem.release(1);
        });

        //PhoneManager.getInstance().setActivityContext(activity);
        //注册web请求回调
        WebServer.getInstance().setWebController(new WebController());

        //创建与服务器间的链接
        RetrofitHelper.getInstance().setHandlerBase(mHandlerBase);
        String serviceURL = mDeviceManager.getSelf().getServiceURL();
        if(!StringUtil.isEmpty(serviceURL)){
            RetrofitHelper.getInstance().start(serviceURL);
        }

        //启动信令广播
        startMulticastHelper();

        //加载字典缓存数据
        DictService.getInstance().loadDictModelToCache();

        //加载位置缓存数据
        mDeviceManager.loadPlaceToCache();
        //加载分区缓存数据
        mDeviceManager.loadGroupToCache();
        mInitSem.release(1);
    }

    /**
     * 重新加载数据
     */
    public void reloadData(){
        LinphonePreferences.instance().setDefaultUsername(getSelfInfo().getPhoneNumber());
        LinphonePreferences.instance().setDefaultDisplayName(getSelfInfo().getBindPhoneNo());
        mDeviceManager.reloadData();
        //加载字典缓存数据
        DictService.getInstance().loadDictModelToCache();
        //加载位置缓存数据
        mDeviceManager.loadPlaceToCache();
        //加载分区缓存数据
        mDeviceManager.loadGroupToCache();
    }
    /**
     * 启动信令广播
     */
    private void startMulticastHelper(){
        //启动信令广播助手
        mMulticastHelper = new MulticastHelper(SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_MULTICAST_ADDRESS
                ,mContext.getString(R.string.default_message_multicast_address))
                , SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PREF_KEY_MULTICAST_PORT
                ,mContext.getResources().getInteger(R.integer.default_message_multicast_port))
                , (data, len) -> onReadMessageMulticast(data));
        mMulticastHelper.start();
    }

    public InitStateEnum getInitStateEnum(){
        return mInitStateEnum;
    }
    /**
     * 初始化等待器
     * 在完全初始化好后发送完成提醒
     */
    private void initializeWaiter(){
        mInitStateEnum = InitStateEnum.INITIALIZING;
        new Thread(()->{
            try {
                mInitSem.acquire(MAX_INITIALIZE_COUNT);
                mInitStateEnum = InitStateEnum.INITIALIZED;
                //开启工作轮询
                startWorkThread();

                //发送初始化成功的广播消息
                RequestDTO<DeviceModel> requestDTO = new RequestDTO<>(mDeviceManager.getSelfInfo(), CommandEnum.NOTICE_DEVICE_STARTED.getId());
                requestDTO.setData(getSelfInfo());
                sendMulticastData(JsonUtils.toJsonString(requestDTO));

                //注册媒体广播监听器
                AudioMulticastManager.getInstance().setReady();

                //通知初始化监听者
                if(mInitListener != null){
                    mInitListener.onInitOK();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                mInitStateEnum = InitStateEnum.FAILED;
            }
        }).start();
    }

    /**
     * 发送广播请求
     */
    public void sendMulticastData(String requestDTO){
        if(mMulticastHelper == null){
            return;
        }
        //String str = JsonUtils.toJsonString(requestDTO);
        mMulticastHelper.sendData(requestDTO.getBytes(StandardCharsets.UTF_8));
    }


    private void onReadMessageMulticast(byte[] buff){
        try {
            String tmpStr = new String(buff, StandardCharsets.UTF_8);
            RequestDTO<Object> requestDTO = JsonUtils.toObject(tmpStr,new Class[]{RequestDTO.class,Object.class});
            if(requestDTO != null) {
                DeviceModel deviceModel = requestDTO.asDeviceModel();
                if(deviceModel.getDevice().getDeviceType() != DeviceTypeEnum.WEB.getValue()) {
                    //通过web发送的请求不用做预处理
                    handleRequestFirst(requestDTO);
                }
                if(mDeviceManager.validateMulticast(requestDTO.asDeviceModel())) {
                    RequestData requestData = new RequestData(CommandEnum.findById(requestDTO.getCommandId()), requestDTO.getDataCommandIndex(), tmpStr);
                    handleRequest(requestData);
                }
            }else{
                KLog.i("do analyze serialData  failed");
            }
        }catch (Exception e){
            e.printStackTrace();
            KLog.e(e);
        }
    }

    /**
     * 启动工作线程
     */
    private void startWorkThread(){
        synchronized (mSynObj){
            new Thread(() -> {
               Looper.prepare();
               while (mRunning) {
                   try {
                       mWorkSem.acquire();
                       //定时检查连接状态
                       mDeviceManager.checkConnection();
                       //定时检查白天、黑夜的切换状态
                       mDeviceManager.checkDayOrNight();
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               }
               Looper.loop();
            }).start();
            mWorkTimer = new RobustTimer(true);
            RobustTimerTask timerTask = new RobustTimerTask() {
                @Override
                public void run() {
                    //释放工作信号
                    mWorkSem.release();
                }
            };
            int workLoopSpan = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.WORK_LOOP_SPAN,mContext.getResources().getInteger(R.integer.default_work_loop_span));
            mWorkTimer.schedule(timerTask, 0, workLoopSpan);
        }
    }

    /**
     * 工作管理器启动时的预检查
     * @return 是否可以进行初始化操作
     */
    public boolean canStart(){
        synchronized (mSynObj) {
            //本机号码检查
            String phoneNo = SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_PHONE_NUMBER, "");
            if (StringUtil.isEmpty(phoneNo)) {
                //如果自己的号码为空，则不允许初始化
                KLog.w("phone number can not be empty");
                mInitStateEnum = InitStateEnum.WAIT_PHONE_NO;
                return false;
            }
            //如果是分机则主机号码也不能为空
            DeviceTypeEnum deviceTypeEnum = DeviceFactory.getInstance().getDeviceType(mContext);
            if (deviceTypeEnum != DeviceTypeEnum.NURSE_STATION_MASTER) {
                String parentNo = SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_PARENT_NUMBER, "");
                if (StringUtil.isEmpty(parentNo)) {
                    //如果自己的号码为空，则不允许初始化
                    KLog.w("master number can not be empty");
                    mInitStateEnum = InitStateEnum.WAIT_PARENT_NO;
                    return false;
                }
            }

            //IP地址检查
            NetworkManager networkManager = new NetworkManager();
            NetConfig netConfig = networkManager.getNetConfig(mContext);
            if(netConfig.getLinkMode().equalsIgnoreCase(NetConfigConstants.LINK_MODE_STATIC)){
                //主要判断静态地址的情况下，IP地址是否设置正确
                if(StringUtil.isEmpty(netConfig.getIpAddress()) || StringUtil.isEmpty(netConfig.getMask()) || StringUtil.isEmpty(netConfig.getGateway())){
                    mInitStateEnum = InitStateEnum.WAIT_NETWORK_CONFIG;
                    return false;
                }
            }
            if (mRunning) {
                return false;
            }
        }
        return true;
    }

    /**
     * 重新加载配置
     */
    private void reloadConfig(){
        //如果不是已经初始化完成，则重新初始化
        if(mInitStateEnum != InitStateEnum.INITIALIZED){
            if(canStart()) {
                start();
            }
        }else{
            //如果已经初始化完成了，则重新加载
            if(canStart()){
                reloadData();
            }
        }
    }

    /**
     * 写入设备的基本信息
     */
    public void writeDeviceBaseConfig(@NonNull DeviceModel deviceModel){
        //主、分机号码
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_PHONE_NUMBER,deviceModel.getDevice().getPhoneNo());
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_PARENT_NUMBER,deviceModel.getDevice().getParentNo());
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_DIRECT_CALL_NO,deviceModel.getDirectCallNo());
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_WHOLE_TITLE,deviceModel.getDevice().getWholeTitle());
        //修改网络配置
        NetworkManager networkManager = new NetworkManager();
        networkManager.saveConfig(mContext, deviceModel.getNetConfig());
        //服务器地址
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.PREF_KEY_SERVICE_ADDRESS,deviceModel.getServiceAddress());
        //重新加载配置
        reloadConfig();

    }
    /**
     * 启动工作管理器
     */
    public void start(){
        if(!canStart()){
            if(mInitListener != null){
                mInitListener.onInitFailed();
            }
            return ;
        }
        synchronized (mSynObj) {
            if (mRunning) {
                return;
            }
            mRunning = true;
            new Thread(()->{
                Looper.prepare();
                init(BusinessApplication.getAppContext());
                Looper.loop();
            }).start();
        }
    }

    public boolean isRunning(){
        synchronized (mSynObj) {
            return mRunning;
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
     * 更新linphone的好友列表
     */
    private void updateLinphoneFriend(){
        synchronized (mSynObj) {
            if (!PhoneManager.getInstance().isPhoneReady()) {
                return;
            }
            List<DeviceModel> deviceModelList = mDeviceManager.getDeviceList();
            if (!StringUtil.isEmpty(deviceModelList)) {
                for (DeviceModel deviceModel : deviceModelList) {
                    updateLinphoneFriend(deviceModel);
                }
            }
        }
    }

    /**
     * 更新设备信息
     */
    public void updateDeviceInfo(RequestDTO<DeviceModel> requestDTO){
        mDeviceManager.updateDeviceInfo(requestDTO);
        //更新linphone上的好友信息
        updateLinphoneFriend(requestDTO.asDeviceModel());
        //如果是自己则需要重新加载数据
        DeviceModel deviceModel = requestDTO.getData();
        if(deviceModel != null && StringUtil.equalsIgnoreCase(deviceModel.getDevice().getDeviceId(),getSelfInfo().getDevice().getDeviceId())){
            reloadData();
        }
    }

    public List<DeviceModel> getDeviceList(){
        return mDeviceManager.getDeviceList();
    }

    public String getSelfPassword(){
        return mDeviceManager.getSelfPassword();
    }
    public String getSelfUsername(){
        return mDeviceManager.getSelfUsername();
    }
    public DeviceModel getSelfInfo(){
        if(mDeviceManager != null) {
            return mDeviceManager.getSelf();
        }else{
            return DeviceManager.getDeviceInfo(mContext);
        }
    }

    /**
     * req-get-config请求的处理
     */
    public void handleReqGetConfig(RequestDTO<RequestConfig> requestDTO){
        // 这个是主机处理的
        // 其它类型的设备不用处理
        if(mDeviceManager instanceof NurseStationManager){
            ((NurseStationManager)mDeviceManager).handleReqGetConfig(requestDTO);
        }
    }

    /**
     * rsp-get-config请求的处理
     */
    public void handleRspGetConfig(RequestDTO<ResponseConfig> requestDTO){
        // 这个是分机处理的
        // 主机不用处理
        if(mDeviceManager instanceof SlaveManager){
            ((SlaveManager)mDeviceManager).handleRspGetConfig(requestDTO);
        }
    }

    /**
     * req-update-bed-template请求的处理
     * 更新床头屏模板
     */
    public void updateBedTemplate(RequestDTO<BedScreenTemplate> requestDTO){
        mDeviceManager.updateBedTemplate(requestDTO);
    }

    public BedScreenTemplate getBedTemplate(RequestDTO<RequestBedScreenTemplate> requestDTO){
        return mDeviceManager.getBedTemplate(requestDTO);
    }
    /**
     * req-update-room-template请求的处理
     * 更新门口屏模板
     */
    public void updateRoomTemplate(RequestDTO<RoomScreenTemplate> requestDTO){
        // 这个是门口屏处理的
        // 其它类型的设备不用处理
        mDeviceManager.updateRoomTemplate(requestDTO);
    }

    public RoomScreenTemplate getRoomTemplate(RequestDTO<RequestRoomScreenTemplate> requestDTO){
        return mDeviceManager.getRoomTemplate(requestDTO);
    }

    /**
     * req-update-patient-info请求的处理
     * 更新病员信息
     */
    public void updatePatientInfo(@NonNull RequestDTO<ResponseList<Patient>> requestDTO){
        mDeviceManager.updatePatientInfo(requestDTO);
    }

    /**
     * req-update-place-info请求的处理
     * 更新位置信息
     */
    public void updatePlaceInfo(@NonNull RequestDTO<ResponseList<Place>> requestDTO){
        mDeviceManager.updatePlaceInfo(requestDTO);
    }

    /**
     * req-update-groupp-info请求的处理
     * 更新位置信息
     */
    public void updateGroupInfo(@NonNull RequestDTO<ResponseList<MulticastGroup>> requestDTO){
        mDeviceManager.updateGroupInfo(requestDTO);
    }

    /**
     * 统一请求预处理
     */
    public void handleRequestFirst(@NonNull RequestDTO<Object> requestDTO){
        KLog.i("do command " + requestDTO.getCommandId());
        mDeviceManager.handleRequestFirst(requestDTO);
    }

    /**
     * 正式请求处理
     */
    public AjaxResult handleRequest(@NonNull RequestData requestData){
        return mDeviceManager.handleRequest(requestData);
    }

    /**
     * req-heart-beat
     * 的请求处理
     */
    public void handleHeartBeat(@NonNull RequestDTO<Object> requestDTO){
        mDeviceManager.handleHeartBeat(requestDTO);
    }

    /**
     * 处理req-send-call的相关请求
     * 主机收到此请求后需要执行呼叫操作
     */
    public void handleSendCall(@NonNull RequestDTO<CallModel> requestDTO){
        mDeviceManager.handleSendCall(requestDTO);
    }

    /**
     * 处理req-clear-call的相关请求
     * 主机收到此请求后需要执行清理呼叫操作
     */
    public void handleClearCall(@NonNull RequestDTO<CallModel> requestDTO){
        if(requestDTO.getData() != null){
            CallModel callModel = requestDTO.getData();
            mDeviceManager.clearCall(callModel);
        }
    }
    /**
     * 处理req-update-call-info命令
     */
    public void handleReqUpdateCallInfo(@NonNull RequestDTO<CallModel> requestDTO){
        mDeviceManager.handleReqUpdateCallInfo(requestDTO);
    }


    /**
     * 处理req-bind-group命令
     */
    public void handleBindGroup(@NonNull RequestDTO<RequestBindGroup> requestDTO){
        mDeviceManager.handleBindGroup(requestDTO);
    }
    /**
     * 处理req-bind-place命令
     */
    public void handleBindPlace(@NonNull RequestDTO<RequestBindPlace> requestDTO){
        mDeviceManager.handleBindPlace(requestDTO);
    }
    /**
     * 处理req-bind-place-parent命令
     */
    public void handleBindPlaceParent(@NonNull RequestDTO<RequestBindPlaceParent> requestDTO){
        mDeviceManager.handleBindPlaceParent(requestDTO);
    }
    public PlaceModel handleGetBedDetail(@NonNull RequestDTO<RequestBedDetail> requestDTO){
        return mDeviceManager.getBedDetail(requestDTO);
    }

    public PlaceModel handleGetRoomDetail(@NonNull RequestDTO<RequestRoomDetail> requestDTO){
        return mDeviceManager.getRoomDetail(requestDTO);
    }

    /**
     * 分页的形式查询病床信息
     * @param masterDeviceId 主机id
     * @param page 页码（从0开始）
     * @param size 每页大小
     */
    public ResponseList<PlaceModel> getBedList(String masterDeviceId,int page, int size){
        return new ResponseList<>(mDeviceManager.getPlaceListFromCache(PlaceTypeEnum.BED.getValue(),masterDeviceId, page * size,size));
    }

    /**
     * 分页的形式查询病房信息
     * @param masterDeviceId 主机id
     * @param page 页码（从0开始）
     * @param size 每页大小
     */
    public ResponseList<PlaceModel> getRoomList(String masterDeviceId,int page, int size){
        return new ResponseList<>(mDeviceManager.getPlaceListFromCache(PlaceTypeEnum.ROOM.getValue(),masterDeviceId, page * size,size));
    }

    public void sendInnerRequest(String requestDTO){
        EventBus.getDefault().post(requestDTO,EventBusConstant.HANDLE_INNER_REQUEST);
    }

    /**
     * 采用异步可订阅的形式发送内部请求
     * @param requestDTO 请求内容
     * @return 可订阅对象
     */
    public ObservableData<AjaxResult> sendInnerRequestObservable(String requestDTO){
        ObservableData<AjaxResult> observableAjaxResult = new ObservableData<>();
        new Thread(()->{
            AjaxResult ajaxResult = mWebController.dispatchMessage(requestDTO);
            observableAjaxResult.setT(ajaxResult);
        }).start();
        return observableAjaxResult;
    }


    /**
     * 开始呼叫
     * （分机：发送请求呼叫的message消息）
     * （主叫：发送消息并执行呼叫操作）
     * @param remoteNo 被叫号码
     * @param callTypeEnum 呼叫类型
     * @param cause 呼叫原因
     */
    public CallModel startCall(String remoteNo, CallTypeEnum callTypeEnum, String cause, boolean isEmergency){
        if(mDeviceManager instanceof NurseStationManager) {
            return mDeviceManager.startCall(remoteNo, callTypeEnum, cause, isEmergency);
        }else{
            return startCallMaster(callTypeEnum,cause,isEmergency);
        }
    }

    /**
     * 开始呼叫主机
     * （分机：发送请求呼叫的message消息）
     * （主叫：发送消息并执行呼叫操作）
     * @param callTypeEnum 呼叫类型
     * @param cause 呼叫原因
     */
    public CallModel startCallMaster(CallTypeEnum callTypeEnum, String cause, boolean isEmergency){
        if(mDeviceManager instanceof SlaveManager) {
            return ((SlaveManager)mDeviceManager).startCallMaster(callTypeEnum, cause, isEmergency);
        }
        return null;
    }

    /**
     * 主机接受呼叫，其实也是主机主叫
     */
    public void acceptCall(String bindPhoneNo){
        if(mDeviceManager instanceof NurseStationManager){
            ((NurseStationManager)mDeviceManager).acceptCall(bindPhoneNo);
        }
    }

    /**
     * 释放呼叫
     */
    public void releaseCall(String bindPhoneNo,CallTypeEnum callTypeEnum){
        mDeviceManager.releaseCall(bindPhoneNo,callTypeEnum);
    }


    /**
     * 只是作为信息显示的状态变更
     */
    public void updateStateInfo(CallTypeEnum callTypeEnum, StateEnum stateEnum, String cause){
        if(mDeviceManager instanceof SlaveManager){
            ((SlaveManager)mDeviceManager).updateStateInfo(callTypeEnum,stateEnum, cause);
        }
    }

    @Subscriber(tag = EventBusConstant.HANDLE_INNER_REQUEST, mode = ThreadMode.ASYNC)
    public void handleInnerRequest(@NonNull String requestDTO) {
        mWebController.dispatchMessage(requestDTO);
    }

    /**
     * 提前检测所有所需权限
     */
    public void preCheckAllPermission(Activity activity){
        PermissionCheckUtil.checkAndRequestRecordVideoPermissions(activity,null);
        PermissionCheckUtil.checkChangeNetworkPermission(activity,null);
    }


    /**
     * 获取某台设备的所有分区数据
     * @param isBaseGroup 是否基础配置
     */
    public List<MulticastGroupModel> getMulticastGroupListFromCache(boolean isBaseGroup){
        return mDeviceManager.getGroupListFromCache(isBaseGroup);
    }

    /**
     * 从缓存中获取某个父位置下的子位置列表
     * @param parentUid 父uid
     * @return 子设备列表
     */
    public List<PlaceModel> getPlaceListByParentFromCache(String parentUid){
        return mDeviceManager.getPlaceListByParentFromCache(parentUid);
    }

    /**
     * 从缓存中获取某个分组下的子位置列表
     * @param groupSn 分组序号
     * @return 设备列表
     */
    public List<PlaceModel> getPlaceListByGroupFromCache(int groupSn){
        return mDeviceManager.getPlaceListByGroupFromCache(groupSn);
    }

    /**
     * 从缓存中获取位置列表
     * @param placeType 位置类型，-1表示全部
     * @param masterDeviceId 主机id
     * @param offset 偏移位置
     * @param limit 每次获取的数量，-1表示全部，此时offset不起作用
     */
    public List<PlaceModel> getPlaceListFromCache(int placeType,String masterDeviceId,int offset,int limit){
        return mDeviceManager.getPlaceListFromCache(placeType,masterDeviceId, offset, limit);
    }

    /**
     * 从缓存中获取位置详情
     * @param uid 位置id
     * @return 位置详情
     */
    public PlaceModel getPlaceModelFromCache(String uid){
        return mDeviceManager.getPlaceModelFromCache(uid);
    }

    /**
     * 获取分区详情
     * @param deviceId 主机序列号
     * @param groupSn 分区序号（不是分区号）
     */
    public MulticastGroupModel getMulticastGroupModelFromCache(String deviceId,int groupSn){
        return mDeviceManager.findMulticastGroupModelInCacheBySn(deviceId, groupSn);
    }

    /**
     * 发送媒体广播
     */
    public void startAudioMulticast(MulticastGroupModel multicastGroupModel){
        if(mDeviceManager instanceof NurseStationManager){
            ((NurseStationManager)mDeviceManager).startAudioMulticast(multicastGroupModel);
        }
    }

    /**
     * 停止媒体广播
     */
    public void stopAudioMulticast(MulticastGroupModel multicastGroupModel){
        if(mDeviceManager instanceof NurseStationManager){
            ((NurseStationManager)mDeviceManager).stopAudioMulticast(multicastGroupModel);
        }
    }


    /**
     * 发送语音广播
     */
    public void startTalkMulticast(MulticastGroupModel multicastGroupModel){
        if(mDeviceManager instanceof NurseStationManager){
            ((NurseStationManager)mDeviceManager).startTalkMulticast(multicastGroupModel);
        }
    }

    /**
     * 停止媒体广播
     */
    public void stopTalkMulticast(MulticastGroupModel multicastGroupModel){
        if(mDeviceManager instanceof NurseStationManager){
            ((NurseStationManager)mDeviceManager).stopTalkMulticast(multicastGroupModel);
        }
    }

    /**
     * 处理语音广播请求
     */
    public void handleStartAudioMulticast(RequestDTO<RequestAudioMulticast> requestDTO){
        if(mDeviceManager instanceof SlaveManager){
            ((SlaveManager)mDeviceManager).handleStartAudioMulticast(requestDTO);
        }
    }
    /**
     * 处理语音广播请求
     */
    public void handleStopAudioMulticast(RequestDTO<RequestAudioMulticast> requestDTO){
        if(mDeviceManager instanceof SlaveManager){
            ((SlaveManager)mDeviceManager).handleStopAudioMulticast(requestDTO);
        }
    }

    /**
     * req-update-capability请求的处理
     * 更新设备信息
     */
    public void handleUpdateCapability(@NonNull RequestDTO<RequestUpdateCapability> requestDTO){
        mDeviceManager.handleUpdateCapability(requestDTO);
    }

    public ObservableArrayList<OperationLogModel> getOperationLogList(Integer deviceType, String deviceName, String deviceId, String dateStart, String dateEnd, int limit, int offset){
        if(mDeviceManager instanceof NurseStationManager){
            return ((NurseStationManager)mDeviceManager).getOperationLogList(deviceType, deviceName, deviceId, dateStart, dateEnd, limit, offset);
        }
        return null;
    }

    /**
     * 处理托管请求
     */
    public void handleReqTrust(RequestDTO<RequestTrust> trustRequestDTO){
        mDeviceManager.handleReqTrust(trustRequestDTO);
    }

    /**
     * 处理托管回复
     */
    public void handleRspTrust(RequestDTO<ResponseTrust> requestDTO){
        mDeviceManager.handleRspTrust(requestDTO);
    }

    /**
     * 获取设备的当前状态
     */
    public ResponseDeviceStatus getSelfState(){
        ResponseDeviceStatus responseDeviceStatus = new ResponseDeviceStatus();
        responseDeviceStatus.setTrustState(TrustManager.getInstance().getTrustState().getValue());
        responseDeviceStatus.setWorkState(getSelfInfo().getState() == null ? StateEnum.ONLINE.getValue() : getSelfInfo().getState().getValue());
        return responseDeviceStatus;
    }

    /**
     * 根据设备配置找到对应的位置
     */
    public PlaceModel findPlaceModelByDevice(DeviceModel deviceModel){
        return mDeviceManager.findPlaceByDevice(deviceModel);
    }

    /**
     * 根据号码获取设备
     */
    public DeviceModel getDeviceByPhoneNo(String phoneNo){
        return mDeviceManager.getDeviceByPhoneNo(phoneNo);
    }

    /**
     * 根据设备id获取设备
     */
    public DeviceModel getDeviceById(String deviceId){
        return mDeviceManager.getDeviceById(deviceId);
    }

    /**
     * 请求进行隐私设置
     */
    public void handleReqPrivacySet(@NonNull RequestDTO<RequestPrivacy> requestDTO){
        mDeviceManager.handleReqPrivacySet(requestDTO);
    }

    /**
     * 获取隐私设置
     */
    public RequestPrivacy getPrivacySet(){
        return mDeviceManager.getPrivacySet();
    }

    /**
     * 请求进行音量设置
     */
    public void handleReqVolumeSet(@NonNull RequestDTO<RequestVolumeSet> requestDTO){
        mDeviceManager.handleReqVolumeSet(requestDTO);
    }

    /**
     * 获取音量设置
     */
    public RequestVolumeSet getVolumeSet(){
        return mDeviceManager.getVolumeSet();
    }

    /**
     * 根据隐私政策获取病员姓名
     */
    public String getPatientName(PatientModel patientModel){
        return mDeviceManager.getPatientName(patientModel);
    }

    /**
     * 根据年龄策略获取病员年龄
     */
    public String getPatientAge(PatientModel patientModel){
        return mDeviceManager.getPatientAge(patientModel);
    }
    /**
     * @see WorkHandler.HandlerListener
     */
    @Override
    public void onLoginOK(){
        synchronized (mSynObj) {
            if (mFirstLogin) {
                //如果第一次登录，则获取设备列表
                mDeviceManager.getDeviceListFromService();
                mFirstLogin = false;
            }
        }
    }

    /**
     * @see WorkHandler.HandlerListener
     */
    @Override
    public void onReadDeviceListOK(){
        updateLinphoneFriend();
    }

    /**
     * 发送广播数据
     */
    @Override
    public void onSendMulticast(Object obj){
        if(obj instanceof String){
            sendMulticastData((String) obj);
        }else {
            sendMulticastData(JsonUtils.toJsonString(obj));
        }
    }

    /**
     * 更新linphone好友
     */
    @Override
    public void onUpdateLinphoneFriend(Object obj){
        if(obj instanceof DeviceModel) {
            updateLinphoneFriend((DeviceModel) obj);
        }
    }

    public void setInitListener(IInitListener initListener){
        mInitListener = initListener;
    }
    /**
     * 初始化监听器
     */
    public interface IInitListener{
        /**
         * 初始化完成
         */
        void onInitOK();

        /**
         * 初始化失败
         */
        void onInitFailed();
    }
}
