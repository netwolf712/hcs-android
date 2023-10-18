package com.hcs.android.business.manager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.business.constant.BooleanConstant;
import com.hcs.android.business.constant.CallMediaTypeEnum;
import com.hcs.android.business.constant.CallTypeEnum;
import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.ConfirmTypeEnum;
import com.hcs.android.business.constant.Constant;
import com.hcs.android.business.constant.DeviceTypeEnum;
import com.hcs.android.business.constant.PlaceTypeEnum;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.constant.StateEnum;
import com.hcs.android.business.constant.StepMasterTypeEnum;
import com.hcs.android.business.constant.TrustStateEnum;
import com.hcs.android.business.constant.UpdateTypeEnum;
import com.hcs.android.business.entity.BedScreenTemplate;
import com.hcs.android.business.entity.CallModel;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.Dict;
import com.hcs.android.business.entity.ListCompare;
import com.hcs.android.business.entity.MulticastGroup;
import com.hcs.android.business.entity.MulticastGroupModel;
import com.hcs.android.business.entity.OperationLog;
import com.hcs.android.business.entity.OperationLogModel;
import com.hcs.android.business.entity.Patient;
import com.hcs.android.business.entity.PatientModel;
import com.hcs.android.business.entity.Place;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.entity.RequestAudioMulticast;
import com.hcs.android.business.entity.RequestBedScreenTemplate;
import com.hcs.android.business.entity.RequestBindGroup;
import com.hcs.android.business.entity.RequestBindPlace;
import com.hcs.android.business.entity.RequestBindPlaceParent;
import com.hcs.android.business.entity.RequestConfig;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.RequestData;
import com.hcs.android.business.entity.RequestPrivacy;
import com.hcs.android.business.entity.RequestRoomScreenTemplate;
import com.hcs.android.business.entity.RequestTrust;
import com.hcs.android.business.entity.RequestUpdateDict;
import com.hcs.android.business.entity.RequestUpdateStepMaster;
import com.hcs.android.business.entity.RequestUpdateTimeSlot;
import com.hcs.android.business.entity.RequestVolumeSet;
import com.hcs.android.business.entity.ResponseConfig;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.entity.ResponseTrust;
import com.hcs.android.business.entity.RoomScreenTemplate;
import com.hcs.android.business.entity.StepMaster;
import com.hcs.android.business.entity.TimeSlot;
import com.hcs.android.business.service.DeviceService;
import com.hcs.android.business.service.DictService;
import com.hcs.android.business.service.MulticastGroupService;
import com.hcs.android.business.service.OperationLogService;
import com.hcs.android.business.service.PatientService;
import com.hcs.android.business.service.PlaceService;
import com.hcs.android.business.util.GroupUtil;
import com.hcs.android.business.util.PlaceUtil;
import com.hcs.android.call.api.ChatHelper;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.CastUtil;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.ResourceUtil;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.server.web.AjaxResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 护士站主机特有功能
 */
public class NurseStationManager extends DeviceManager{
    /**
     * 分区映射表
     * groupSn,MulticastGroupModel
     */
    private final Map<Integer,MulticastGroupModel> mMulticastGroupMap = new HashMap<>();


    public NurseStationManager(Context context){
        super(context);
        //初始化病区
        initSections();
        //初始化病房
        initRooms();
        //初始化病床
        initBeds();
        //初始化工作站（护士台、医生办公室）
        initStations();
        //初始化分区
        initGroups();
        //开启自动录音功能
        mCallManager.setAutoRecord(true);
        //开启托管功能
        startTrustTimer();
        //初始化全局广播配置
        AudioMulticastManager.getInstance().initGlobalAudioMulticast(mSelf.getDevice().getPhoneNo());
        //开启自动广播功能
        AutoMulticastManager.getInstance().loadAutoMulticastModels(mSelf.getDevice().getPhoneNo());
        AutoMulticastManager.getInstance().startAutoMulticastTimer();
    }

    /**
     * 初始化分区
     */
    private void initPlaces(@NonNull PlaceTypeEnum placeTypeEnum){
        int placeRealCount = PlaceService.getInstance().getPlaceCount(mSelf.getDevice().getDeviceId(),placeTypeEnum.getValue());
        int placeSetCount = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.MAX_PRE + placeTypeEnum.getName() + PreferenceConstant.MAX_SUFFIX
                ,mContext.getResources().getInteger(ResourceUtil.getIntegerId(mContext,PreferenceConstant.DEFAULT_MAX_PRE + placeTypeEnum.getName() + PreferenceConstant.MAX_SUFFIX)));
        if(placeSetCount > placeRealCount){
            for(int i = placeRealCount; i < placeSetCount; i++){
                Place place = new Place();
                place.setUid(PlaceUtil.genPlaceUid(mSelf.getDevice().getDeviceId(), placeTypeEnum.getName(), (i + 1)));
                place.setPlaceSn(i + 1);
                place.setPlaceNo(String.valueOf(i + 1));
                place.setMasterDeviceId(mSelf.getDevice().getDeviceId());
                place.setPlaceType(placeTypeEnum.getValue());
                PlaceService.getInstance().updatePlace(place);

            }
        }
    }

    /**
     * 初始化病区
     */
    private void initSections(){
        initPlaces(PlaceTypeEnum.SECTION);
    }

    /**
     * 初始化病房
     */
    private void initRooms(){
        initPlaces(PlaceTypeEnum.ROOM);
    }

    /**
     * 初始化病床
     */
    private void initBeds(){
        initPlaces(PlaceTypeEnum.BED);
    }

    /**
     * 初始化工作站（护士台、医生办公室）
     */
    private void initStations(){
        initPlaces(PlaceTypeEnum.STATION);
    }
    //初始化分区
    private void initGroups(){
        int groupRealCount = MulticastGroupService.getInstance().getMulticastGroupCount();
        int groupSetCount = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.MAX_GROUP_COUNT,mContext.getResources().getInteger(R.integer.default_max_group_count));
        if(groupSetCount > groupRealCount){
            for(int i = groupRealCount; i < groupSetCount; i++){
                MulticastGroup multicastGroup = new MulticastGroup();
                multicastGroup.setGroupSn(i + 1);
                multicastGroup.setGroupNo(String.valueOf(i + 1));
                multicastGroup.setDeviceId(mSelf.getDevice().getDeviceId());
                MulticastGroupService.getInstance().updateMulticastGroup(multicastGroup);
            }
        }

    }


    /**
     * 判断分机的配置是否最新的
     * @param slaveModel 分机数据
     * @param requestConfig 分机发过来的配置请求
     * @return true 最新的，false版本不一致，需要更新
     */
    private boolean isSameConfig(@NonNull DeviceModel slaveModel,@NonNull RequestConfig requestConfig){
        if(slaveModel.getDevice().getLastDataCommandIndex() == null
        || slaveModel.getDevice().getLastDataCommandIndex() == -1){
            return false;
        }

        if(!Objects.equals(slaveModel.getDevice().getLastDataCommandIndex(),requestConfig.getLastDataCommandIndex())){
            return false;
        }

        if(slaveModel.getLastReqGetConfigIndex() == null
        || slaveModel.getLastReqGetConfigIndex() == -1){
            return false;
        }

        return Objects.equals(slaveModel.getLastReqGetConfigIndex(), requestConfig.getLastDataCommandIndex());
    }
    /**
     * req-get-config请求的回复
     */
    public void handleReqGetConfig(@NonNull RequestDTO<RequestConfig> requestDTO){
        //requestDTO = JsonUtils.toObject(JsonUtils.toJsonString(requestDTO),new Class[]{RequestDTO.class,RequestConfig.class});
        RequestConfig requestConfig = CastUtil.cast(requestDTO.getData());
        DeviceModel slaveModel = getDeviceById(requestDTO.getDeviceId());
        changeDeviceState(slaveModel.getDevice().getDeviceId(), StateEnum.ONLINE);
        if(isSameConfig(slaveModel,requestConfig)){
            KLog.i("nothing changed,no need to send rsp get config");
            return;
        }
        //先检查是否自己的分机
        if(!StringUtil.equalsIgnoreCase(slaveModel.getDevice().getParentNo(),mSelf.getDevice().getPhoneNo())){
            KLog.i("not my slave ==> " + JsonUtils.toJsonString(slaveModel.getDevice()));
            //如果不是自己的分机则不做任何处理
            return;
        }
        //判断是否需要更新配置数据
        boolean isNeedUpdateConfig = !Objects.equals(requestConfig.getLastDataCommandIndex(),slaveModel.getDevice().getLastDataCommandIndex());
        RequestDTO<ResponseConfig> rspConfig = new RequestDTO<>(mSelf,slaveModel,CommandEnum.RSP_GET_CONFIG);
        ResponseConfig responseConfig = new ResponseConfig();
        rspConfig.setData(responseConfig);
        responseConfig.setHospitalTitle(mSelf.getDevice().getWholeTitle());
        if(slaveModel.getDevice().getDeviceType() == DeviceTypeEnum.BED_SCREEN.getValue()) {
            //如果是床头屏，还要判断床序号号设置是否正确
            try{
                //设置设备状态为在线
                //再判断此分机的床号是否正确
                int bedNo = Integer.parseInt(slaveModel.getDevice().getPhoneNo());
                int maxBedCount = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.MAX_BED_COUNT, mContext.getResources().getInteger(R.integer.default_max_bed_count));
                if (bedNo < 0 || bedNo >= maxBedCount) {
                    responseConfig.setConfirmId(ConfirmTypeEnum.WRONG_BED.getValue());
                    ChatHelper.sendMessage(slaveModel.getPhoneNumber(), JsonUtils.toJsonString(rspConfig));
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
                responseConfig.setConfirmId(ConfirmTypeEnum.WRONG_BED.getValue());
                ChatHelper.sendMessage(slaveModel.getPhoneNumber(),JsonUtils.toJsonString(rspConfig));
                return;
            }
        }else if(slaveModel.getDevice().getDeviceType() == DeviceTypeEnum.ROOM_SCREEN.getValue()){
            //如果是门口屏，也需做设置正确性的预判
            try{
                //设置设备状态为在线
                //再判断此分机的床号是否正确
                int bedNo = Integer.parseInt(slaveModel.getDevice().getPhoneNo());
                int maxBedCount = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.MAX_ROOM_COUNT, mContext.getResources().getInteger(R.integer.default_max_bed_count));
                if (bedNo < 0 || bedNo >= maxBedCount) {
                    responseConfig.setConfirmId(ConfirmTypeEnum.WRONG_ROOM.getValue());
                    ChatHelper.sendMessage(slaveModel.getPhoneNumber(), JsonUtils.toJsonString(rspConfig));
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
                responseConfig.setConfirmId(ConfirmTypeEnum.WRONG_ROOM.getValue());
                ChatHelper.sendMessage(slaveModel.getPhoneNumber(),JsonUtils.toJsonString(rspConfig));
                return;
            }
        }
        //先发送确认通知Get
        responseConfig.setConfirmId(ConfirmTypeEnum.OK.getValue());
        ChatHelper.sendMessage(slaveModel.getPhoneNumber(),JsonUtils.toJsonString(rspConfig));

        slaveModel.setLastReqGetConfigIndex(requestConfig.getLastDataCommandIndex());

        if(isNeedUpdateConfig) {
            //如果需要更新数据，则发送最新的配置给分机
            //更新模板信息
            checkUpdateTemplate(slaveModel, requestConfig);
            //更新字典信息
            checkUpdateDict(slaveModel, requestConfig);
            //更新继任主机信息
            checkStepMaster(slaveModel, requestConfig);
            //更新时间段信息
            checkTimeSlot(slaveModel, requestConfig);
            //更新位置信息
            checkUpdatePlaceInfo(slaveModel);
            //发送隐私政策
            checkSendPrivacySet(slaveModel);
            //发送音量设置
            checkSendVolumeSet(slaveModel);
        }
    }

    /**
     * 检查并更新分机模板
     * @param slaveModel 分机基本信息
     * @param requestConfig 请求信息
     */
    public void checkUpdateTemplate(@NonNull DeviceModel slaveModel,@NonNull RequestConfig requestConfig){
        DeviceTypeEnum slaveTypeEnum = DeviceTypeEnum.findById(slaveModel.getDevice().getDeviceType());
        DeviceConfig deviceConfig = DeviceConfigFactory.getInstance().getDeviceConfig(mContext,slaveTypeEnum);
        String lastConfigId = deviceConfig.getCurrentTemplateConfigId();
        String lastTemplateId = deviceConfig.getCurrentTemplateId();
        if(!StringUtil.equalsIgnoreCase(lastConfigId,requestConfig.getCurrentTemplateConfigId())
                || !StringUtil.equalsIgnoreCase(lastTemplateId,requestConfig.getCurrentTemplateId())
        ){
            updateTemplate(slaveModel,lastTemplateId);
        }
        slaveModel.getDevice().setTemplateConfigId(lastConfigId);
        slaveModel.getDevice().setTemplateId(lastTemplateId);
        DeviceService.getInstance().updateDevice(slaveModel);
    }

    /**
     * 检查并更新字典信息
     * @param slaveModel 分机基本信息
     * @param requestConfig 请求信息
     */
    public void checkUpdateDict(@NonNull DeviceModel slaveModel,@NonNull RequestConfig requestConfig){
        Long lastDictUpdateTime = SettingsHelper.getInstance(mContext).getLong(PreferenceConstant.DICT_UPDATE_TIME,mContext.getResources().getInteger(R.integer.default_dict_update_time));
        if(!Objects.equals(lastDictUpdateTime,requestConfig.getDictUpdateTime())){
            List<Dict> dictList = DictService.getInstance().getDictList();
            if(!StringUtil.isEmpty(dictList)){
                SendHelper<Dict> dictSendHelper = new SendHelper<>();
                List<ResponseList<Dict>> responseLists = dictSendHelper.packageData(dictList,UpdateTypeEnum.UPDATE_FORCE.getValue());
                if(!StringUtil.isEmpty(responseLists)){
                    for(ResponseList<Dict> responseList : responseLists){
                        RequestDTO<RequestUpdateDict> requestDTO = new RequestDTO<>(mSelf,slaveModel, CommandEnum.REQ_UPDATE_DICT);
                        RequestUpdateDict requestUpdateDict = new RequestUpdateDict();
                        requestUpdateDict.setLastUpdateTime(lastDictUpdateTime);
                        requestUpdateDict.setResponseList(responseList);
                        requestDTO.setData(requestUpdateDict);
                        ChatHelper.sendMessage(slaveModel.getPhoneNumber(),requestDTO);
                    }
                }
            }
        }
    }

    /**
     * 检查并更新继任主机信息
     * @param slaveModel 分机基本信息
     * @param requestConfig 请求信息
     */
    public void checkStepMaster(@NonNull DeviceModel slaveModel,@NonNull RequestConfig requestConfig){
        Long lastStepMasterUpdateTime = StepMasterManager.getInstance().getLastUpdateTime();
        if(!Objects.equals(lastStepMasterUpdateTime,requestConfig.getStepMasterUpdateTime())){
            List<StepMaster> stepMasterList = StepMasterManager.getInstance().getAllStepMasterList();
            if(!StringUtil.isEmpty(stepMasterList)){
                SendHelper<StepMaster> stepMasterSendHelper = new SendHelper<>();
                List<ResponseList<StepMaster>> responseLists = stepMasterSendHelper.packageData(stepMasterList,UpdateTypeEnum.UPDATE_FORCE.getValue());
                if(!StringUtil.isEmpty(responseLists)){
                    for(ResponseList<StepMaster> responseList : responseLists){
                        RequestDTO<RequestUpdateStepMaster> requestDTO = new RequestDTO<>(mSelf,slaveModel, CommandEnum.REQ_UPDATE_STEP_MASTER);
                        RequestUpdateStepMaster requestUpdateStepMaster = new RequestUpdateStepMaster();
                        requestUpdateStepMaster.setLastUpdateTime(lastStepMasterUpdateTime);
                        requestUpdateStepMaster.setResponseList(responseList);
                        requestDTO.setData(requestUpdateStepMaster);
                        ChatHelper.sendMessage(slaveModel.getPhoneNumber(),requestDTO);
                    }
                }
            }
        }
    }


    /**
     * 检查并更新时间段信息
     * @param slaveModel 分机基本信息
     * @param requestConfig 请求信息
     */
    public void checkTimeSlot(@NonNull DeviceModel slaveModel,@NonNull RequestConfig requestConfig){
        Long lastTimeSlotUpdateTime = TimeSlotManager.getInstance().getLastUpdateTime();
        if(!Objects.equals(lastTimeSlotUpdateTime,requestConfig.getTimeSlotUpdateTime())){
            List<TimeSlot> timeSlotList = TimeSlotManager.getInstance().getAllTimeSlotList();
            if(!StringUtil.isEmpty(timeSlotList)){
                SendHelper<TimeSlot> timeSlotSendHelper = new SendHelper<>();
                List<ResponseList<TimeSlot>> responseLists = timeSlotSendHelper.packageData(timeSlotList,UpdateTypeEnum.UPDATE_FORCE.getValue());
                if(!StringUtil.isEmpty(responseLists)){
                    for(ResponseList<TimeSlot> responseList : responseLists){
                        RequestDTO<RequestUpdateTimeSlot> requestDTO = new RequestDTO<>(mSelf,slaveModel, CommandEnum.REQ_UPDATE_TIME_SLOT);
                        RequestUpdateTimeSlot requestUpdateTimeSlot = new RequestUpdateTimeSlot();
                        requestUpdateTimeSlot.setLastUpdateTime(lastTimeSlotUpdateTime);
                        requestUpdateTimeSlot.setResponseList(responseList);
                        requestDTO.setData(requestUpdateTimeSlot);
                        ChatHelper.sendMessage(slaveModel.getPhoneNumber(),requestDTO);
                    }
                }
            }
        }
    }

    /**
     * 更新分机模板
     */
    public void updateTemplate(@NonNull DeviceModel slaveModel, String templateId){
        DeviceTypeEnum slaveTypeEnum = DeviceTypeEnum.findById(slaveModel.getDevice().getDeviceType());
        switch (slaveTypeEnum){
            case BED_SCREEN: {
                BedScreenConfig deviceConfig = (BedScreenConfig) DeviceConfigFactory.getInstance().getDeviceConfig(mContext, DeviceTypeEnum.BED_SCREEN);
                BedScreenTemplate bedScreenTemplate = deviceConfig.getTemplate(templateId);
                RequestDTO<BedScreenTemplate> requestDTO = new RequestDTO<>(mSelf, slaveModel, CommandEnum.REQ_UPDATE_BED_TEMPLATE);
                requestDTO.setData(bedScreenTemplate);
                ChatHelper.sendMessage(slaveModel.getPhoneNumber(), requestDTO);
                break;
            }
            case ROOM_SCREEN: {
                RoomScreenConfig deviceConfig = (RoomScreenConfig) DeviceConfigFactory.getInstance().getDeviceConfig(mContext, DeviceTypeEnum.ROOM_SCREEN);
                RoomScreenTemplate roomScreenTemplate = deviceConfig.getTemplate(templateId);
                RequestDTO<RoomScreenTemplate> requestDTO = new RequestDTO<>(mSelf, slaveModel, CommandEnum.REQ_UPDATE_ROOM_TEMPLATE);
                requestDTO.setData(roomScreenTemplate);
                ChatHelper.sendMessage(slaveModel.getPhoneNumber(), requestDTO);
                break;
            }
            default:
                break;
        }
    }

    /**
     * 检查并更新分机病床信息
     * @param slaveModel 分机基本信息
     */
    public void checkUpdatePlaceInfo(@NonNull DeviceModel slaveModel){
        //直接强制全部更新
        synchronized (mSynObj){
            //找到此设备所在位置及所有子位置
            PlaceModel placeModel = findPlaceByDevice(slaveModel);
            if(placeModel != null){
                List<PlaceModel> placeModelList = getPlaceAndChildren(placeModel);
                sortPlaceModelList(placeModelList);
                //发送位置数据
                List<Place> placeList = getPlaceList(placeModelList);
                SendHelper<Place> placeSendHelper = new SendHelper<>();
                placeSendHelper.sendDataFlow(slaveModel,mSelf,placeList,CommandEnum.REQ_UPDATE_PLACE_INFO,UpdateTypeEnum.UPDATE_FORCE.getValue());
                //发送病员数据
                List<Patient> patientList = getPatientList(placeModelList);
                SendHelper<Patient> patientSendHelper = new SendHelper<>();
                patientSendHelper.sendDataFlow(slaveModel,mSelf,patientList,CommandEnum.REQ_UPDATE_PATIENT_INFO,UpdateTypeEnum.UPDATE_FORCE.getValue());
            }
        }
    }

    /**
     * 发送隐私政策给分机
     * （当前不做更新判断，只要连上就发送）
     */
    private void checkSendPrivacySet(@NonNull DeviceModel slaveModel){
        RequestDTO<RequestPrivacy> privacyRequestDTO = new RequestDTO<>(mSelf,CommandEnum.REQ_UPDATE_PRIVACY.getId());
        privacyRequestDTO.setData(getPrivacySet());
        ChatHelper.sendMessage(slaveModel.getPhoneNumber(),privacyRequestDTO);
    }

    /**
     * 发送音量设置给分机
     * （当前不做更新判断，只要连上就发送）
     */
    private void checkSendVolumeSet(@NonNull DeviceModel slaveModel){
        RequestDTO<RequestVolumeSet> volumeSetRequestDTO = new RequestDTO<>(mSelf,CommandEnum.REQ_UPDATE_VOLUME.getId());
        volumeSetRequestDTO.setData(getVolumeSet());
        ChatHelper.sendMessage(slaveModel.getPhoneNumber(),volumeSetRequestDTO);
    }

    /**
     * 从位置列表中获取纯粹的位置数据
     */
    @NonNull
    private List<Place> getPlaceList(@NonNull List<PlaceModel> placeModelList){
        List<Place> placeList = new ArrayList<>();
        for(PlaceModel placeModel : placeModelList){
            placeList.add(placeModel.getPlace());
        }
        return placeList;
    }

    /**
     * 从位置列表中获取纯粹的病员数据
     */
    @NonNull
    private List<Patient> getPatientList(@NonNull List<PlaceModel> placeModelList){
        List<Patient> patientList = new ArrayList<>();
        for(PlaceModel placeModel : placeModelList){
            if(!StringUtil.isEmpty(placeModel.getPatientModelList())){
                for(PatientModel patientModel : placeModel.getPatientModelList()){
                    patientList.add(patientModel.getPatient());
                }
            }
        }
        return patientList;
    }
    /**
     * 将位置及其子位置转换为列表
     */
    @NonNull
    private List<PlaceModel> getPlaceAndChildren(@NonNull PlaceModel placeModel){
        List<PlaceModel> placeList = new ArrayList<>();
        placeList.add(placeModel);
        if(!StringUtil.isEmpty(placeModel.getChildren())){
            for(PlaceModel tmp : placeModel.getChildren()){
                placeList.addAll(getPlaceAndChildren(tmp));
            }
        }
        return placeList;
    }


    private boolean comparePatients(List<PatientModel> masterList,List<PatientModel> slaveList){
        ListCompare<PatientModel> listCompare = new ListCompare<>();
        return listCompare.compareLists(masterList,slaveList,PatientModel::needUpdate);
    }

    //////////////病员相关管理////////////////////////////////
    /**
     * 分页的形式查询病员信息
     * @param page 页码（从0开始）
     * @param size 每页大小
     */
    public List<PatientModel> getPatientList(int page,int size){
        return PatientService.getInstance().getPatientList(page * size,size);
    }

    /**
     * 获取病员总数
     */
    public int getPatientCount(){
        return PatientService.getInstance().getPatientCount();
    }

    /**
     * 根据病床号向各个设备发送病员信息
     */
    private void sendPatientMessageToSlave(@NonNull Patient patient, UpdateTypeEnum updateTypeEnum){
        if(patient.getBedSn() != null) {
            String placeUid = PlaceUtil.genPlaceUid(patient.getMasterDeviceId(), PlaceTypeEnum.BED.getName(), patient.getBedSn());
            synchronized (mPlaceMap) {
                PlaceModel placeModel = mPlaceMap.get(placeUid);
                if (placeModel != null) {
                    //获取它的父位置节点
                    List<PlaceModel> placeModelList = getParentPlaceList(placeModel);
                    //将自己也加入位置节点中
                    placeModelList.add(placeModel);
                    //遍历所有的设备并发送病员更新数据
                    for (PlaceModel tmpModel : placeModelList) {
                        if (!StringUtil.isEmpty(tmpModel.getDeviceModelList())) {
                            for (DeviceModel deviceModel : tmpModel.getDeviceModelList()) {
                                RequestDTO<ResponseList<Patient>> requestDTO = new RequestDTO<>(mSelf, deviceModel, CommandEnum.REQ_UPDATE_PATIENT_INFO);
                                ResponseList<Patient> patientList = new ResponseList<>(patient);
                                patientList.setUpdateType(updateTypeEnum.getValue());
                                requestDTO.setData(patientList);
                                ChatHelper.sendMessage(deviceModel.getPhoneNumber(), requestDTO);
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * 修改病员信息
     * （包括新增）
     */
    public void changePatient(@NonNull PatientModel patientModel){
        //获取之前的病员信息
        PatientModel oldPatient = PatientService.getInstance().getPatient(patientModel.getPatient().getSerialNumber());
        if(oldPatient != null){
            //如果有旧的病员信息，则需要床号是否变了
            //若发生改变，则需要通知之前的设备删除此病员信息
            if(Objects.equals(oldPatient.getPatient().getBedSn(),patientModel.getPatient().getBedSn())){
                sendPatientMessageToSlave(oldPatient.getPatient(),UpdateTypeEnum.UPDATE_DELETE);
            }
        }
        sendPatientMessageToSlave(patientModel.getPatient(),UpdateTypeEnum.UPDATE_NORMAL);
        //再根据病床信息找到需要发送消息通知的设备列表
        PatientService.getInstance().updatePatient(patientModel.getPatient());
    }
    /**
     * 从病床位置中移除病员信息
     */
    @Override
    protected void removeBedPatient(@NonNull Patient patient){
        super.removeBedPatient(patient);
        sendPatientMessageToSlave(patient,UpdateTypeEnum.UPDATE_DELETE);
    }

    /**
     * 更新病员信息
     * 同时更新病床位置与病员信息的关系
     */
    @Override
    protected void updateBedPatient(@NonNull Patient patient){
        synchronized (mSynObj){
            //只遍历床位列表
            List<PlaceModel> placeModelList = getPlaceListFromCache(PlaceTypeEnum.BED.getValue(),"",0, Constant.INVALID_LIMIT);
            for(PlaceModel placeModel : placeModelList){
                if(!StringUtil.isEmpty(placeModel.getPatientModelList())) {
                    for (PatientModel tmp : placeModel.getPatientModelList()) {
                        //先找到之前是否已经有关联到床位
                        if (tmp != null &&
                                tmp.getPatient() != null &&
                                StringUtil.equalsIgnoreCase(tmp.getPatient().getSerialNumber(), patient.getSerialNumber())) {
                            //如果关联到了，则检查床位数据是否发生变化
                            if (Objects.equals(tmp.getPatient().getBedSn(), patient.getBedSn())) {
                                //若没发生变化，则只要更新数据即可
                                tmp.reloadPatient(patient);
                            } else {
                                //若发生变化，则先从当前节点移除
                                placeModel.getPatientModelList().remove(tmp);
                                //通知之前所有关联的设备删除此病员信息
                                sendPatientMessageToSlave(tmp.getPatient(),UpdateTypeEnum.UPDATE_DELETE);
                            }
                            //通知所有设备更新床位信息
                            sendPatientMessageToSlave(patient,UpdateTypeEnum.UPDATE_NORMAL);
                            return;
                        }
                    }
                }
            }
            //通过床位编号找到床位
            for(PlaceModel placeModel : placeModelList){
                if(Objects.equals(patient.getBedSn(),placeModel.getPlace().getPlaceSn())){
                    PatientModel patientModel = new PatientModel(patient);
                    placeModel.getPatientModelList().add(patientModel);
                    //通知所有设备更新床位信息
                    sendPatientMessageToSlave(patient,UpdateTypeEnum.UPDATE_NORMAL);
                    return;
                }
            }
        }
    }

    /**
     * 删除病员信息
     */
    public void removePatient(@NonNull PatientModel patientModel){
        //通知各子设备执行删除操作
        sendPatientMessageToSlave(patientModel.getPatient(),UpdateTypeEnum.UPDATE_DELETE);
        PatientService.getInstance().removePatient(patientModel.getPatient());
    }

    /**
     * 处理req-send-call的相关请求
     * 主机收到此请求后需要执行呼叫操作
     */
    @Override
    public DeviceModel handleSendCall(@NonNull RequestDTO<CallModel> requestDTO){
        DeviceModel remoteModel = super.handleSendCall(requestDTO);
        return remoteModel;
    }

    /**
     * 给位置下绑定的分机发送位置信息改变请求
     */
    private void sendPlaceInfoToDevice(@NonNull PlaceModel placeModel){
        if(!StringUtil.isEmpty(placeModel.getDeviceModelList())){
            for(DeviceModel deviceModel : placeModel.getDeviceModelList()){
                RequestDTO<ResponseList<Place>> tmpDto = new RequestDTO<>(mSelf,deviceModel,CommandEnum.REQ_UPDATE_PLACE_INFO);
                ResponseList<Place> tmpList = new ResponseList<>(placeModel.getPlace());
                tmpDto.setData(tmpList);
                ChatHelper.sendMessage(deviceModel.getPhoneNumber(),tmpDto);
            }
        }
    }
    /**
     * 给父位置下绑定的分机发送子位置信息改变请求
     */
    private void sendPlaceInfoToParentDevice(@NonNull PlaceModel parentPlaceModel,@NonNull PlaceModel placeModel,UpdateTypeEnum updateTypeEnum){
        if(!StringUtil.isEmpty(parentPlaceModel.getDeviceModelList())){
            for(DeviceModel deviceModel : parentPlaceModel.getDeviceModelList()){
                RequestDTO<ResponseList<Place>> tmpDto = new RequestDTO<>(mSelf,deviceModel,CommandEnum.REQ_UPDATE_PLACE_INFO);
                ResponseList<Place> tmpList = new ResponseList<>(placeModel.getPlace());
                tmpList.setUpdateType(updateTypeEnum.getValue());
                tmpDto.setData(tmpList);
                ChatHelper.sendMessage(deviceModel.getPhoneNumber(),tmpDto);
            }
        }
    }
    /**
     * 绑定分区
     */
    @Override
    public void handleBindGroup(@NonNull RequestDTO<RequestBindGroup> requestDTO){
        synchronized (mSynObj) {
            if (requestDTO.getData() != null) {
                RequestBindGroup requestBindGroup = requestDTO.getData();
                if (!StringUtil.equalsIgnoreCase(mSelf.getDevice().getPhoneNo(), requestBindGroup.getParentNo())) {
                    return;
                }
                //从缓存中找到之前绑定此分组的位置列表
                List<PlaceModel> placeModelList = getPlaceListByGroupFromCache(requestBindGroup.getGroupSn());
                //将不在绑定名单内的位置移除
                if(!StringUtil.isEmpty(placeModelList)){
                    for(PlaceModel placeModel : placeModelList){
                        boolean find = false;
                        if(!StringUtil.isEmpty(requestBindGroup.getPlaceUidList())){
                            for(String placeUid : requestBindGroup.getPlaceUidList()){
                                if(StringUtil.equalsIgnoreCase(placeModel.getPlace().getUid(),placeUid)){
                                    find = true;
                                    break;
                                }
                            }
                        }
                        if(!find){
                            placeModel.getPlace().setGroupNo(null);
                            placeModel.getPlace().setGroupSn(null);
                            PlaceService.getInstance().updatePlace(placeModel.getPlace());
                            //通知其下的设备，位置信息发生了变更
                            sendPlaceInfoToDevice(placeModel);
                        }
                    }
                }
                if(!StringUtil.isEmpty(requestBindGroup.getPlaceUidList())){


                    for(String placeUid : requestBindGroup.getPlaceUidList()){
                        PlaceModel placeModel = mPlaceMap.get(placeUid);
                        if(placeModel != null){
                            //如果之前的绑定的分区不一致，则重新绑定
                            if(!Objects.equals(placeModel.getPlace().getGroupSn(),requestBindGroup.getGroupSn())){
                                placeModel.getPlace().setGroupSn(requestBindGroup.getGroupSn());
                                synchronized (mMulticastGroupMap) {
                                    MulticastGroupModel multicastGroupModel = mMulticastGroupMap.get(requestBindGroup.getGroupSn());
                                    if (multicastGroupModel != null) {
                                        placeModel.getPlace().setGroupNo(multicastGroupModel.getMulticastGroup().getGroupNo());
                                    }
                                }
                                //更新数据库
                                PlaceService.getInstance().updatePlace(placeModel.getPlace());
                                //通知其下的设备，位置信息发生了变更
                                sendPlaceInfoToDevice(placeModel);
                            }
                        }
                    }
                }
                //发送广播通知自己的分机改变
                RequestDTO<RequestBindGroup> tmpDto = new RequestDTO<>(mSelf,CommandEnum.REQ_BIND_GROUP.getId());
                tmpDto.setData(requestDTO.getData());
                mHandlerBase.sendMulticast(tmpDto);
            }

        }
    }

    /**
     * 绑定位置
     */
    @Override
    public void handleBindPlace(@NonNull RequestDTO<RequestBindPlace> requestDTO){
        synchronized (mSynObj) {
            if (requestDTO.getData() != null) {
                RequestBindPlace requestBindPlace = requestDTO.getData();
                if (!StringUtil.equalsIgnoreCase(mSelf.getDevice().getPhoneNo(), requestBindPlace.getParentNo())) {
                    return;
                }
                if(!StringUtil.isEmpty(requestBindPlace.getPlaceUidList())){
                    for(String placeUid : requestBindPlace.getPlaceUidList()){
                        PlaceModel placeModel = mPlaceMap.get(placeUid);
                        if(placeModel != null){
                            //父位置有没改变，若没改变则不用处理
                            if(!StringUtil.equals(placeModel.getPlace().getParentUid(),requestBindPlace.getParentUid())){
                                //若不相同，则需要从之前的父节点移除
                                removeFromParent(placeModel);
                                //更改父节点
                                placeModel.getPlace().setParentUid(requestBindPlace.getParentUid());
                                //找到当前父节点并插入列表
                                PlaceModel parentModel = mPlaceMap.get(requestBindPlace.getParentUid());
                                if(parentModel != null) {
                                    addPlaceChild(parentModel, placeModel);
                                    sendPlaceInfoToParentDevice(parentModel, placeModel, UpdateTypeEnum.UPDATE_NORMAL);
                                }
                                //更新数据库
                                PlaceService.getInstance().updatePlace(placeModel.getPlace());
                                sendPlaceInfoToDevice(placeModel);
                            }
                        }
                    }
                }

            }
        }
    }

    /**
     * 绑定父位置
     */
    @Override
    public void handleBindPlaceParent(@NonNull RequestDTO<RequestBindPlaceParent> requestDTO){
        synchronized (mSynObj) {
            if (requestDTO.getData() != null) {
                RequestBindPlaceParent requestBindPlaceParent = requestDTO.getData();
                PlaceModel placeModel = mPlaceMap.get(requestBindPlaceParent.getPlaceUid());
                if(placeModel == null){
                    return;
                }
                //查找此位置之前是否有父位置
                if(!StringUtil.isEmpty(placeModel.getPlace().getParentUid())){
                    PlaceModel parentModel = mPlaceMap.get(placeModel.getPlace().getParentUid());
                    if(parentModel != null){
                        //如果父位置没变，则不做任何处理
                        if(Objects.equals(parentModel.getPlace().getPlaceType(),requestBindPlaceParent.getParentPlaceType())
                        && StringUtil.equalsIgnoreCase(parentModel.getPlace().getPlaceNo(),requestBindPlaceParent.getParentPlaceNo())){
                            return;
                        }
                        //如果不一样，则先更新父节点
                        removeFromParent(placeModel);
                    }
                }
                //找到新的父节点
                PlaceModel parentModel = null;
                for(PlaceModel tmp : mPlaceMap.values()){
                    if(Objects.equals(tmp.getPlace().getPlaceType(),requestBindPlaceParent.getParentPlaceType())
                            && StringUtil.equalsIgnoreCase(tmp.getPlace().getPlaceNo(),requestBindPlaceParent.getParentPlaceNo())){
                        parentModel = tmp;
                        break;
                    }
                }
                if(parentModel != null){
                    //挂载到父节点下
                    addPlaceChild(parentModel,placeModel);
                    sendPlaceInfoToParentDevice(parentModel,placeModel,UpdateTypeEnum.UPDATE_NORMAL);
                    placeModel.getPlace().setParentUid(parentModel.getPlace().getUid());
                    //更新数据库
                    PlaceService.getInstance().updatePlace(placeModel.getPlace());
                    sendPlaceInfoToDevice(placeModel);
                }
            }
        }
    }


    /**
     * 主机接受呼叫，其实也是主机主叫
     */
    public void acceptCall(String bindPhoneNo){
        DeviceModel deviceModel = getDeviceByPhoneNo(bindPhoneNo);
        if(deviceModel != null){
            mCallManager.startCall(deviceModel.getPhoneNumber(),deviceModel.getBindPhoneNo());
        }
    }

    /**
     * 释放呼叫
     */
    @Override
    public void releaseCall(String bindPhoneNo,CallTypeEnum callTypeEnum){
        DeviceModel deviceModel = getDeviceByPhoneNo(bindPhoneNo);
        if(deviceModel != null){
            if(callTypeEnum.getMediaType() == CallMediaTypeEnum.STREAM
                    && mCallManager.isCalling(deviceModel.getPhoneNumber())) {
                //如果在呼叫的，则停止呼叫
                mCallManager.stopCall(deviceModel.getPhoneNumber());
            }else {

                //发送呼叫清理操作
                synchronized (mSynObj) {

                    CallModel callModel = findCallModel(deviceModel, callTypeEnum);
                    if (callModel != null) {
                        callModel.setState(StateEnum.CALL_END.getValue());
                        updateDeviceStateByCall(callModel, StateEnum.CALL_END);
                        RequestDTO<CallModel> requestDTO = new RequestDTO<>(mSelf, CommandEnum.REQ_CLEAR_CALL.getId());
                        requestDTO.setData(callModel);
                        ChatHelper.sendMessage(deviceModel.getPhoneNumber(), requestDTO);
                        //最后再删除一下
                        //因为在updateDeviceStateByCall里又被添加了一遍
                        removeCallModel(deviceModel, callModel);
                    }
                    if (callTypeEnum.getMediaType() == CallMediaTypeEnum.STREAM) {
                        deviceModel.setState(StateEnum.ONLINE);
                    }
                }
            }

        }
    }

    /**
     * 获取主机号码
     * 对主机来说主机号码就是phoneNo
     */
    @Override
    public String getMasterNo(){
        return mSelf.getDevice().getPhoneNo();
    }


    /**
     * 开始媒体广播
     */
    public void startTalkMulticast(@NonNull MulticastGroupModel multicastGroupModel){
        if(multicastGroupModel.isTalking()
                || multicastGroupModel.getMulticastGroup().getGroupSn()== null
                || multicastGroupModel.getMulticastGroup().getGroupNo() == null){
            //已经有媒体指针了，则不允许重复开启播放功能
            return;
        }
        multicastGroupModel.setMulticastAddress(SettingsHelper.getInstance(BusinessApplication.getAppContext()).getString(PreferenceConstant.PREF_KEY_AUDIO_MULTICAST_ADDRESS,BusinessApplication.getAppContext().getString(R.string.default_audio_multicast_address)));
        multicastGroupModel.setMulticastPort(GroupUtil.getAudioMulticastPort(Integer.parseInt(getSelf().getDevice().getPhoneNo()), multicastGroupModel.getMulticastGroup().getGroupSn()));
        //开启广播
        AudioMulticastManager.getInstance().startTalkMulticast(multicastGroupModel);
        //发送语音广播通知
        RequestDTO<RequestAudioMulticast> requestDTO = new RequestDTO<>(getSelf(), CommandEnum.REQ_MULTICAST_START.getId());
        RequestAudioMulticast requestAudioMulticast = new RequestAudioMulticast();
        requestAudioMulticast.setMulticastGroup(multicastGroupModel.getMulticastGroup().getGroupSn());
        requestAudioMulticast.setListenIP(multicastGroupModel.getMulticastAddress());
        requestAudioMulticast.setListenPort(multicastGroupModel.getMulticastPort());
        requestAudioMulticast.setTalking(multicastGroupModel.isTalking());
        requestAudioMulticast.setPlaying(multicastGroupModel.isPlaying());

        requestAudioMulticast.setPlayReadyCode(SettingsHelper.getInstance(BusinessApplication.getAppContext()).getBoolean(PreferenceConstant.PREF_KEY_PLAY_AUDIO_MULTICAST_READ_CODE
                ,BusinessApplication.getAppContext().getResources().getBoolean(R.bool.default_play_audio_multicast_ready_code)));
        requestDTO.setData(requestAudioMulticast);
        mHandlerBase.sendMulticast(requestDTO);

    }

    /**
     * 停止广播
     */
    public void stopTalkMulticast(@NonNull MulticastGroupModel multicastGroupModel){
        //停止广播
        AudioMulticastManager.getInstance().stopTalkMulticast(multicastGroupModel);

        //发送语音广播通知
        RequestDTO<RequestAudioMulticast> requestDTO = new RequestDTO<>(getSelf(), CommandEnum.REQ_MULTICAST_STOP.getId());
        RequestAudioMulticast requestAudioMulticast = new RequestAudioMulticast();
        requestAudioMulticast.setMulticastGroup(multicastGroupModel.getMulticastGroup().getGroupSn());
        requestAudioMulticast.setPlaying(multicastGroupModel.isPlaying());
        requestAudioMulticast.setTalking(multicastGroupModel.isTalking());
        requestDTO.setData(requestAudioMulticast);
        mHandlerBase.sendMulticast(requestDTO);
    }



    /**
     * 开始语音广播
     */
    public void startAudioMulticast(@NonNull MulticastGroupModel multicastGroupModel){
        if(multicastGroupModel.getStreamPtr() != -1
                || multicastGroupModel.getMulticastGroup().getGroupSn()== null
                || multicastGroupModel.getMulticastGroup().getGroupNo() == null){
            //已经有媒体指针了，则不允许重复开启播放功能
            return;
        }
        multicastGroupModel.setMulticastAddress(SettingsHelper.getInstance(BusinessApplication.getAppContext()).getString(PreferenceConstant.PREF_KEY_AUDIO_MULTICAST_ADDRESS,BusinessApplication.getAppContext().getString(R.string.default_audio_multicast_address)));
        multicastGroupModel.setMulticastPort(GroupUtil.getAudioMulticastPort(Integer.parseInt(getSelf().getDevice().getPhoneNo()), multicastGroupModel.getMulticastGroup().getGroupSn()));


        //开启广播
        AudioMulticastManager.getInstance().startAudioMulticast(multicastGroupModel);

        //发送语音广播通知
        RequestDTO<RequestAudioMulticast> requestDTO = new RequestDTO<>(getSelf(), CommandEnum.REQ_MULTICAST_START.getId());
        RequestAudioMulticast requestAudioMulticast = new RequestAudioMulticast();
        requestAudioMulticast.setMulticastGroup(multicastGroupModel.getMulticastGroup().getGroupSn());
        requestAudioMulticast.setListenIP(multicastGroupModel.getMulticastAddress());
        requestAudioMulticast.setListenPort(multicastGroupModel.getMulticastPort());
        requestAudioMulticast.setTalking(multicastGroupModel.isTalking());
        requestAudioMulticast.setPlaying(multicastGroupModel.isPlaying());
        requestAudioMulticast.setPlayReadyCode(SettingsHelper.getInstance(BusinessApplication.getAppContext()).getBoolean(PreferenceConstant.PREF_KEY_PLAY_AUDIO_MULTICAST_READ_CODE
                ,BusinessApplication.getAppContext().getResources().getBoolean(R.bool.default_play_audio_multicast_ready_code)));
        mHandlerBase.sendMulticast(requestDTO);
    }

    /**
     * 停止广播
     */
    public void stopAudioMulticast(MulticastGroupModel multicastGroupModel){
        //停止广播
        AudioMulticastManager.getInstance().stopAudioMulticast(multicastGroupModel);
        //发送语音广播通知
        RequestDTO<RequestAudioMulticast> requestDTO = new RequestDTO<>(getSelf(), CommandEnum.REQ_MULTICAST_STOP.getId());
        RequestAudioMulticast requestAudioMulticast = new RequestAudioMulticast();
        requestAudioMulticast.setMulticastGroup(multicastGroupModel.getMulticastGroup().getGroupSn());
        requestAudioMulticast.setPlaying(multicastGroupModel.isPlaying());
        requestAudioMulticast.setTalking(multicastGroupModel.isTalking());
        mHandlerBase.sendMulticast(requestDTO);
    }
    //////////////////将分区（详情）数据优先加入缓存，提升访问效率，方便界面显示操作////////////////////////////////////////////////////////////////

    /**
     * 找到对应的分区
     * @param groupNo 分区区号
     * @return 分区
     */
    @Override
    public MulticastGroupModel findGroupModelInCache(@NonNull String groupNo){
        synchronized (mSynObj){
            for(MulticastGroupModel groupModel : mMulticastGroupMap.values()){
                if(StringUtil.equals(groupModel.getMulticastGroup().getGroupNo(),groupNo)){
                    return groupModel;
                }
            }
        }
        return null;
    }

    /**
     * 找到对应的分区
     * @param groupSn 分区序号
     * @return 分区
     */
    @Override
    public MulticastGroupModel findGroupModelInCacheBySn(@NonNull Integer groupSn){
        synchronized (mSynObj){
            return mMulticastGroupMap.get(groupSn);
        }
    }

    @Override
    public void deleteGroupInCache(@NonNull MulticastGroup multicastGroup){
        synchronized (mSynObj){
            mMulticastGroupMap.remove(multicastGroup.getGroupSn());
        }
    }

    /**
     * 获取与自己相关的区列表
     */
    public List<MulticastGroupModel> loadSelfGroupList(){
        List<MulticastGroup> groupList = MulticastGroupService.getInstance().getBaseMulticastGroupList();
        if(!StringUtil.isEmpty(groupList)) {
            return convertGroupToGroupModel(groupList);
        }
        return null;
    }

    /**
     * 将分区数据加入缓存
     */
    @Override
    public void loadGroupToCache(){
        synchronized (mSynObj){
            List<MulticastGroupModel> multicastGroupModelList = loadSelfGroupList();
            mMulticastGroupMap.clear();
            if(!StringUtil.isEmpty(multicastGroupModelList)){
                for(MulticastGroupModel multicastGroupModel : multicastGroupModelList){
                    mMulticastGroupMap.put(multicastGroupModel.getMulticastGroup().getGroupSn(),multicastGroupModel);
                }
            }
        }
    }

    //从缓存中获取分区列表
    @Override
    public List<MulticastGroupModel> getGroupListFromCache(boolean isBaseGroup){
        Collection<MulticastGroupModel> tmpList = isBaseGroup ? mMulticastGroupMap.values() : AutoMulticastManager.getInstance().getAutoMulticastModelFromCash();
        return  sortMulticastGroupList(new ArrayList<>(tmpList));
    }

    /**
     * 通过分区序号找到分区
     */
    @Override
    public MulticastGroupModel findMulticastGroupModelInCacheBySn(@NonNull String masterDeviceId, @NonNull Integer groupSn){
        return mMulticastGroupMap.get(groupSn);
    }
    /**
     * 处理请求总入口
     */
    @Override
    public AjaxResult handleRequest(@NonNull RequestData requestData){
        return (AjaxResult) mWebHandlerManager.handleCommand(requestData.getCommandEnum().getId(),requestData.getRawData());
    }

    /**
     * 将位置从父位置移除
     */
    protected PlaceModel removeFromParent(@NonNull PlaceModel placeModel){
        PlaceModel parentModel = super.removeFromParent(placeModel);
        if(parentModel != null){
            //发送通知给父节点下的设备
            sendPlaceInfoToParentDevice(parentModel,placeModel,UpdateTypeEnum.UPDATE_DELETE);
        }
        return parentModel;
    }

    /**
     * 更新病员信息
     */
    @Override
    public void updatePatientInfo(@NonNull RequestDTO<ResponseList<Patient>> requestDTO){
        if(isCommandSelf(requestDTO.asDeviceModel())) {
            super.updatePatientInfo(requestDTO);
            //如果是强制更新，则把所有信息直接广播给分机
            if (requestDTO.getData() != null) {
                ResponseList<Patient> patientList = CastUtil.cast(requestDTO.getData());
                if (patientList.getUpdateType() == UpdateTypeEnum.UPDATE_FORCE.getValue()) {
                    RequestDTO<ResponseList<Patient>> tmp = new RequestDTO<>(mSelf, CommandEnum.REQ_UPDATE_PATIENT_INFO.getId());
                    tmp.setData(patientList);
                    mHandlerBase.sendMulticast(tmp);
                }
            }
        }
    }

    @Override
    public void updatePlaceInfo(@NonNull RequestDTO<ResponseList<Place>> requestDTO){
        if(isCommandSelf(requestDTO.asDeviceModel())) {
            synchronized (mSynObj) {
                if (requestDTO.getData() != null) {
                    ResponseList<Place> placeList = CastUtil.cast(requestDTO.getData());
                    if (placeList.getUpdateType() == UpdateTypeEnum.UPDATE_FORCE.getValue()) {
                        if (mPlaceUpdateHelper.addData(placeList)) {
                            //如果有更新过了，则重新加载缓存
                            loadPlaceToCache();
                        }
                    } else {
                        if (!StringUtil.isEmpty(placeList.getList())) {
                            for (Place place : placeList.getList()) {
                                if (placeList.getUpdateType() == UpdateTypeEnum.UPDATE_NORMAL.getValue()) {
                                    //找到位置的原始数据
                                    PlaceModel placeModel = mPlaceMap.get(place.getUid());
                                    if (placeModel != null) {
                                        if (!placeModel.getPlace().isSamePlace(place)) {
                                            //只有数据有不同才需要处理
                                            //父节点是否改变
                                            if (!StringUtil.equalsIgnoreCase(placeModel.getPlace().getParentUid(), place.getParentUid())) {
                                                //若改变，则找出之前的父节点
                                                //从它的子节点删除
                                                removeFromParent(placeModel);
                                                if (!StringUtil.isEmpty(place.getParentUid())) {
                                                    PlaceModel curParentModel = mPlaceMap.get(place.getParentUid());
                                                    placeModel.setPlace(place);
                                                    if (curParentModel != null) {
                                                        addPlaceChild(curParentModel, placeModel);
                                                        sendPlaceInfoToParentDevice(curParentModel, placeModel, UpdateTypeEnum.UPDATE_NORMAL);
                                                    }
                                                }
                                            } else {
                                                placeModel.setPlace(place);
                                                sendPlaceInfoToDevice(placeModel);
                                            }
                                            PlaceService.getInstance().updatePlace(placeModel.getPlace());
                                        }
                                    } else {
                                        PlaceService.getInstance().updatePlace(place);
                                        PlaceModel tmpModel = new PlaceModel();
                                        tmpModel.setPlace(place);
                                        //如果是全新插入的数据，则
                                        PlaceModel curParentModel = mPlaceMap.get(place.getParentUid());
                                        if (curParentModel != null) {
                                            addPlaceChild(curParentModel, tmpModel);
                                            sendPlaceInfoToParentDevice(curParentModel, tmpModel, UpdateTypeEnum.UPDATE_NORMAL);
                                        }
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
    }

    /**
     * 是否是命令自己的
     * @param deviceModel 通过requestDTO.asDeviceModel()转换的deviceModel
     * @return true 是，false 否
     */
    private boolean isCommandSelf(@NonNull DeviceModel deviceModel){
        return (Objects.equals(deviceModel.getDevice().getDeviceType(),DeviceTypeEnum.SERVICE.getValue())
                || Objects.equals(deviceModel.getDevice().getDeviceType(),DeviceTypeEnum.WEB.getValue())
                || Objects.equals(deviceModel.getDevice().getDeviceType(),DeviceTypeEnum.NURSE_STATION_MASTER.getValue()) && StringUtil.equalsIgnoreCase(deviceModel.getDevice().getDeviceId(),mSelf.getDevice().getDeviceId())
        );
    }
    /**
     * req-update-bed-template请求的处理
     * 更新床头屏模板
     */
    @Override
    public void updateBedTemplate(@NonNull RequestDTO<BedScreenTemplate> requestDTO){
        if(isCommandSelf(requestDTO.asDeviceModel())) {
            //只有服务器、web或者主机自身才能改变模板
            if (requestDTO.getData() != null) {
                BedScreenTemplate bedScreenTemplate = requestDTO.getData();
                BedScreenConfig bedScreenConfig = new BedScreenConfig(mContext);
                bedScreenTemplate.setConfigId(String.valueOf(System.currentTimeMillis()));
                bedScreenConfig.setTemplate(bedScreenTemplate, bedScreenConfig.getTemplateId());
                //如果配置的是当前模板，则通知所有分机
                if (StringUtil.equalsIgnoreCase(bedScreenTemplate.getTemplateId(), bedScreenConfig.getCurrentTemplateId())) {
                    RequestDTO<BedScreenTemplate> tmp = new RequestDTO<>(mSelf, CommandEnum.REQ_UPDATE_BED_TEMPLATE.getId());
                    tmp.setData(bedScreenTemplate);
                    mHandlerBase.sendMulticast(tmp);
                }
            }
        }
    }

    /**
     * req-get-bed-template请求的处理
     * 获取床头屏模板
     */
    @Override
    public BedScreenTemplate getBedTemplate(@NonNull RequestDTO<RequestBedScreenTemplate> requestDTO){
        if(requestDTO.getData() != null) {
            RequestBedScreenTemplate requestBedScreenTemplate = requestDTO.getData();
            BedScreenConfig bedScreenConfig = new BedScreenConfig(mContext);
            String templateId = StringUtil.isEmpty(requestBedScreenTemplate.getTemplateId()) ? bedScreenConfig.getCurrentTemplateId() : requestBedScreenTemplate.getTemplateId();
            return bedScreenConfig.getTemplate(templateId);
        }
        return null;
    }

    /**
     * req-get-room-template请求的处理
     * 获取病房门口屏模板
     */
    @Override
    public RoomScreenTemplate getRoomTemplate(@NonNull RequestDTO<RequestRoomScreenTemplate> requestDTO){
        if(requestDTO.getData() != null) {
            RequestRoomScreenTemplate requestRoomScreenTemplate = requestDTO.getData();
            RoomScreenConfig roomScreenConfig = new RoomScreenConfig(mContext);
            String templateId = StringUtil.isEmpty(requestRoomScreenTemplate.getTemplateId()) ? roomScreenConfig.getCurrentTemplateId() : requestRoomScreenTemplate.getTemplateId();
            return roomScreenConfig.getTemplate(templateId);
        }
        return null;
    }

    public ObservableArrayList<OperationLogModel> getOperationLogList(Integer deviceType, String deviceName, String deviceId, String dateStart, String dateEnd, int limit, int offset){
        ObservableArrayList<OperationLogModel> observableArrayList = new ObservableArrayList<>();
        new Thread(()->{
            List<OperationLog> operationLogList = OperationLogService.getInstance().getOperationLogList(deviceType,deviceName,deviceId,dateStart,dateEnd,limit,offset);
            if(!StringUtil.isEmpty(operationLogList)){
                List<OperationLogModel> modelList = new ArrayList<>();
                for(OperationLog operationLog : operationLogList){
                    OperationLogModel operationLogModel = new OperationLogModel();
                    operationLogModel.setOperationLog(operationLog);
                    if(StringUtil.equalsIgnoreCase(mSelf.getDevice().getDeviceId(),operationLog.getCallerDeviceId())){
                        //自己是主叫，则远端为被叫
                        operationLogModel.setRemoteDevice(mDeviceDataManager.findInIdMap(operationLog.getCalleeDeviceId()));
                    }else{
                        //否则远端为主机
                        operationLogModel.setRemoteDevice(mDeviceDataManager.findInIdMap(operationLog.getCallerDeviceId()));
                    }
                    modelList.add(operationLogModel);
                }
                observableArrayList.addAll(modelList);
            }
        }).start();
        return observableArrayList;
    }
    /**
     * 处理托管请求
     */
    @Override
    public void handleReqTrust(@NonNull RequestDTO<RequestTrust> trustRequestDTO){
        RequestTrust requestTrust = trustRequestDTO.getData();
        if(StringUtil.equalsIgnoreCase(requestTrust.getTrustNo(),mSelf.getDevice().getPhoneNo())){
            //如果托管主机是自己，则是其它主机发来的请求
            if(requestTrust.getState() == BooleanConstant.INTEGER_FALSE){
                //如果是释放，则删除之前缓存的托管设备列表
                StepMasterManager.getInstance().removeBeTrustNo(requestTrust.getBeTrustNo());
                //删除位置列表
                PlaceService.getInstance().deletePlaceByMasterDeviceId(trustRequestDTO.getDeviceId());
                //重新加载位置缓存
                loadPlaceToCache();
            }else{
                //将此设备加入托管设备列表
                StepMasterManager.getInstance().addBeTrustNo(requestTrust.getBeTrustNo());
            }
            //如果是向自己发送的托管请求，则发送回复，接受请求
            RequestDTO<ResponseTrust> requestDTO = new RequestDTO<>(mSelf,CommandEnum.RSP_TRUST.getId());
            ResponseTrust responseTrust = new ResponseTrust();
            responseTrust.setOk(true);
            responseTrust.setTrustNo(requestTrust.getTrustNo());
            responseTrust.setBeTrustNo(requestTrust.getBeTrustNo());
            responseTrust.setState(requestTrust.getState());
            requestDTO.setData(responseTrust);
            DeviceModel remoteDevice = getDeviceById(trustRequestDTO.getDeviceId());
            ChatHelper.sendMessage(remoteDevice.getPhoneNumber(),requestDTO);
        }else if (StringUtil.equalsIgnoreCase(requestTrust.getBeTrustNo(),mSelf.getDevice().getPhoneNo())){
            //如果被托管主机是自己，则是用户配置的
            if(requestTrust.isAutoTrust()){
                //如果是自动托管，且之前不是，则启动自动托管定时器
                if(!TrustManager.getInstance().isAutoTrust()) {
                    TrustManager.getInstance().startAutoTrustTimer(obj->{
                        handleAutoTrust();
                    },TrustManager.getInstance().getAutoTrustCheckTime());
                    TrustManager.getInstance().setAutoTrust(requestTrust.isAutoTrust());
                }
            }else{
                if(TrustManager.getInstance().isAutoTrust()) {
                   //如果之前是自动托管，则关闭自动托管功能
                   TrustManager.getInstance().stopAutoTrustTimer();
                    TrustManager.getInstance().setAutoTrust(requestTrust.isAutoTrust());
                }
                //发送托管请求
                sendTrustRequest(requestTrust.getTrustNo(),requestTrust.getState() == BooleanConstant.INTEGER_TRUE);
            }
        }
    }

    /**
     * 发送托管请求
     * @param trustNo 请求托管的主机号
     * @param isStart true 开始托管，false 结束托管
     */
    public void sendTrustRequest(String trustNo,boolean isStart){
        DeviceModel remoteDevice = getDeviceByPhoneNo(trustNo);
        if(remoteDevice == null){
            return;
        }
        RequestDTO<RequestTrust> requestDTO = new RequestDTO<>(mSelf,CommandEnum.REQ_TRUST.getId());
        RequestTrust requestTrust = new RequestTrust();
        requestTrust.setBeTrustNo(mSelf.getDevice().getPhoneNo());
        requestTrust.setBeTrustNo(trustNo);
        requestTrust.setState(isStart ? BooleanConstant.INTEGER_TRUE : BooleanConstant.INTEGER_FALSE);
        requestDTO.setData(requestTrust);
        ChatHelper.sendMessage(remoteDevice.getPhoneNumber(),requestDTO);
        TrustStateEnum stateEnum = TrustManager.getInstance().getTrustState();
        if(stateEnum == TrustStateEnum.NONE){
            //将托管主机写入配置
            TrustManager.getInstance().setTrustNo(trustNo);
            TrustManager.getInstance().setTrustState(isStart ? TrustStateEnum.REQUEST_START_TRUST.getValue() : TrustStateEnum.REQUEST_STOP_TRUST.getValue());
            TrustManager.getInstance().startTimer(obj->{
                sendTrustRequest(trustNo,isStart);
            },TrustManager.getInstance().getTrustCheckTime());
        }
    }
    /**
     * 处理托管回复
     */
    public void handleRspTrust(@NonNull RequestDTO<ResponseTrust> requestDTO){
        ResponseTrust responseTrust = requestDTO.getData();
        if(responseTrust.isOk()) {
            if(StringUtil.equalsIgnoreCase(responseTrust.getBeTrustNo(),mSelf.getDevice().getPhoneNo())) {
                //关闭托管请求定时器
                TrustManager.getInstance().stopTimer();
                if (responseTrust.getState() == BooleanConstant.INTEGER_TRUE) {
                    //接受托管请求
                    if (StringUtil.equalsIgnoreCase(mSelf.getDevice().getPhoneNo(), responseTrust.getBeTrustNo())) {
                        //更新位置信息
                        sendDataToTrustDevice(responseTrust.getTrustNo());
                    }
                    TrustManager.getInstance().setTrustState(TrustStateEnum.TRUSTING.getValue());
                } else {
                    //接受取消托管请求
                    TrustManager.getInstance().setTrustNo("");
                    TrustManager.getInstance().setTrustState(TrustStateEnum.NONE.getValue());
                    //转换为非托管状态时，就可以托管其它主机了
                    startTrustTimer();
                }
            }
        }
    }

    /**
     * 将数据传给托管主机
     * @param trustNo 托管主机号码
     */
    public void sendDataToTrustDevice(@NonNull String trustNo){
        synchronized (mSynObj){
            DeviceModel remoteDevice = getDeviceByPhoneNo(trustNo);
            if(remoteDevice == null){
                return;
            }
            List<PlaceModel> placeModelList = new ArrayList<>();
            for(PlaceModel placeModel : mPlaceMap.values()){
                if(StringUtil.equalsIgnoreCase(placeModel.getPlace().getMasterDeviceId(),mSelf.getDevice().getDeviceId())){
                    placeModelList.add(placeModel);
                }
            }
            sortPlaceModelList(placeModelList);
            //发送位置数据
            List<Place> placeList = getPlaceList(placeModelList);
            SendHelper<Place> placeSendHelper = new SendHelper<>();
            placeSendHelper.sendDataFlow(remoteDevice,mSelf,placeList,CommandEnum.REQ_UPDATE_PLACE_INFO,UpdateTypeEnum.UPDATE_NORMAL.getValue());
            //发送病员数据
            List<Patient> patientList = getPatientList(placeModelList);
            SendHelper<Patient> patientSendHelper = new SendHelper<>();
            patientSendHelper.sendDataFlow(remoteDevice,mSelf,patientList,CommandEnum.REQ_UPDATE_PATIENT_INFO,UpdateTypeEnum.UPDATE_NORMAL.getValue());
            //发送摄像头数据
            //不发送了，托管情况下不去查看病房摄像头
            //减去逻辑上的麻烦
        }
    }

    /**
     * 开启托管定时器
     * 定时通知被托管的主机下的分机，当前呼叫到此主机
     */
    private void startTrustTimer(){
        TrustStateEnum stateEnum = TrustManager.getInstance().getTrustState();
        if(stateEnum == TrustStateEnum.NONE){
            //此主机当前需在非托管状态
            TrustManager.getInstance().startTimer(obj->{
                List<StepMaster> stepMasterList = StepMasterManager.getInstance().getStepMasterList(StepMasterTypeEnum.BE_TRUST);
                if(!StringUtil.isEmpty(stepMasterList)) {
                    for (StepMaster stepMaster : stepMasterList) {
                        RequestDTO<RequestTrust> requestDTO = new RequestDTO<>(mSelf, CommandEnum.REQ_TRUST.getId());
                        RequestTrust requestTrust = new RequestTrust();
                        requestTrust.setTrustNo(mSelf.getDevice().getPhoneNo());
                        requestTrust.setBeTrustNo(stepMaster.getMasterNo());
                        requestTrust.setState(BooleanConstant.INTEGER_TRUE);
                        requestDTO.setData(requestTrust);
                        mHandlerBase.sendMulticast(requestDTO);
                    }
                }
            },TrustManager.getInstance().getTrustCheckTime());
        }else if(stateEnum == TrustStateEnum.REQUEST_START_TRUST || stateEnum == TrustStateEnum.REQUEST_STOP_TRUST){
            String trustNo = TrustManager.getInstance().getTrustNo();
            if(!StringUtil.isEmpty(trustNo)) {
                //如果是请求状态中，则继续发送请求
                TrustManager.getInstance().startTimer(obj -> {
                    sendTrustRequest(trustNo,stateEnum == TrustStateEnum.REQUEST_START_TRUST);
                },TrustManager.getInstance().getTrustCheckTime());
            }
        }

        if(TrustManager.getInstance().isAutoTrust()){
            //是否自动托管，是则开启自动托管定时器
            TrustManager.getInstance().startAutoTrustTimer(obj->{
                handleAutoTrust();
            },TrustManager.getInstance().getAutoTrustCheckTime());
        }
    }

    /**
     * 处理自动托管的逻辑
     */
    private void handleAutoTrust(){
        TrustStateEnum stateEnum = TrustManager.getInstance().getTrustState();
        String trustNo = "";
        List<StepMaster> stepMasterList = StepMasterManager.getInstance().getStepMasterList(StepMasterTypeEnum.TRUST);
        if(StringUtil.isEmpty(stepMasterList)){
            return;
        }
        //只取第一个托管的主机即可
        trustNo = stepMasterList.get(0).getMasterNo();
        if(stateEnum == TrustStateEnum.TRUSTING) {
            //如果是托管中，则检查是否在自动托管的时间段外
            if(!TimeSlotManager.getInstance().isInAutoTrustTime(System.currentTimeMillis())){
                sendTrustRequest(trustNo,false);
            }
        }else if(stateEnum == TrustStateEnum.NONE) {
            //如果没在托管，则检查是否在自动托管的时间段内
            if(TimeSlotManager.getInstance().isInAutoTrustTime(System.currentTimeMillis())){
                sendTrustRequest(trustNo,true);
            }
        }
    }

    /**
     * 请求进行隐私设置
     * 主机要把此请求分发给其它分机
     */
    @Override
    public void handleReqPrivacySet(@NonNull RequestDTO<RequestPrivacy> requestDTO){
        super.handleReqPrivacySet(requestDTO);
        if(isCommandSelf(requestDTO.asDeviceModel())) {
            RequestDTO<RequestPrivacy> privacyRequestDTO = new RequestDTO<>(mSelf, CommandEnum.REQ_UPDATE_PRIVACY.getId());
            privacyRequestDTO.setData(requestDTO.getData());
            mHandlerBase.sendMulticast(privacyRequestDTO);
        }
    }

    /**
     * 是否启用隐私政策
     * 主机永远不需要启用
     */
    @Override
    public boolean isPrivacyEnable(@NonNull RequestPrivacy requestPrivacy){
        return false;
    }

    /**
     * 获取自己是什么型号的设备
     */
    @Override
    public DeviceTypeEnum getSelfDeviceType(){
        return DeviceTypeEnum.NURSE_STATION_MASTER;
    }

    /**
     * 获取通话音量
     */
    @Override
    public int getSpeakerVolume(@NonNull RequestVolumeSet requestVolumeSet){
        if(isUseNightVolumeSet(requestVolumeSet)){
            return requestVolumeSet.getMasterSpeakerNightVolume();
        }else{
            return requestVolumeSet.getMasterSpeakerDayVolume();
        }
    }

    /**
     * 设置音量
     * 主机要把此请求分发给其它分机
     */
    public void handleReqVolumeSet(@NonNull RequestDTO<RequestVolumeSet> requestDTO) {
        super.handleReqVolumeSet(requestDTO);
        if(isCommandSelf(requestDTO.asDeviceModel())) {
            RequestDTO<RequestVolumeSet> volumeSetRequestDTO = new RequestDTO<>(mSelf, CommandEnum.REQ_UPDATE_VOLUME.getId());
            volumeSetRequestDTO.setData(requestDTO.getData());
            mHandlerBase.sendMulticast(volumeSetRequestDTO);
        }
    }

    /**
     * 判断这是不是对自己有效的广播
     * 主机只接收自己管理的、或被托管的分机的广播
     * @param deviceModel 广播消息发送方
     * @return true 有效，false 无效
     */
    @Override
    public boolean validateMulticast(@NonNull DeviceModel deviceModel){
        boolean isValidate = super.validateMulticast(deviceModel);
        //主机判断是否有效，有效则继续处理
        if(isValidate){
            //主机只接收自己管理的、或被托管的分机的广播
            if (Objects.equals(deviceModel.getDevice().getDeviceType(),DeviceTypeEnum.NURSE_STATION_MASTER.getValue())){
                return false;
            }
            if(StringUtil.equalsIgnoreCase(deviceModel.getDevice().getParentNo(),mSelf.getDevice().getPhoneNo())){
                return true;
            }
            List<StepMaster> beTrustMasterList = StepMasterManager.getInstance().getStepMasterList(StepMasterTypeEnum.BE_TRUST);
            if(!StringUtil.isEmpty(beTrustMasterList)){
                for(StepMaster stepMaster : beTrustMasterList){
                    if(StringUtil.equalsIgnoreCase(deviceModel.getDevice().getParentNo(),stepMaster.getMasterNo())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 处理req-update-call-info命令
     * 需要处理分机同时呼叫多个主机的问题
     */
    public void handleReqUpdateCallInfo(@NonNull RequestDTO<CallModel> requestDTO){
        super.handleReqUpdateCallInfo(requestDTO);

        //如果呼叫类型为数据流，则需要判断其是否已经和其它主机接通了
        if(requestDTO.getData() != null) {
            CallModel callModel = requestDTO.getData();
            //如果是语音处理类的主被叫方则不用处理
            CallTypeEnum callTypeEnum = CallTypeEnum.findById(callModel.getCallType());
            DeviceModel remoteDevice = getRemoteDevice(callModel);
            if(!Objects.equals(callModel.getState(),StateEnum.CALLING.getValue())){
                //且呼叫状态已经不是呼叫中了，要从呼入队列中去除
                CallInManager.getInstance().removeCallInModelByCaller(remoteDevice.getDevice().getDeviceId(),callTypeEnum);
            }
        }
    }

    /**
     * 更新设备信息
     * 判断是自己的分机则发送自己的信息给分机
     */
    public void updateDeviceInfo(@NonNull RequestDTO<DeviceModel> requestDTO) {
        super.updateDeviceInfo(requestDTO);
        DeviceModel deviceModel = requestDTO.getData();
        if(StringUtil.equalsIgnoreCase(deviceModel.getDevice().getParentNo(),mSelf.getDevice().getPhoneNo())){
            RequestDTO<DeviceModel> tmp = new RequestDTO<>(mSelf,CommandEnum.REQ_CHANGE_DEVICE_INFO.getId());
            tmp.setData(mSelf);
            ChatHelper.sendMessage(deviceModel.getPhoneNumber(),tmp);
        }
    }

    /**
     * 更新分组信息
     */
    public void updateGroupInfo(@NonNull RequestDTO<ResponseList<MulticastGroup>> requestDTO) {
        super.updateGroupInfo(requestDTO);
        synchronized (mSynObj) {
            if (requestDTO.getData() != null) {
                ResponseList<MulticastGroup> groupList = CastUtil.cast(requestDTO.getData());
                if (groupList.getUpdateType() == UpdateTypeEnum.UPDATE_FORCE.getValue()) {
                    //关闭自动广播
                    AutoMulticastManager.getInstance().stopAutoMulticastTimer();
                    //如果是暴力更新，则只能先停止之前的全部广播
                    AudioMulticastManager.getInstance().stopAllMulticast();
                    mMulticastGroupMap.clear();
                    loadGroupToCache();
                }else{
                    if (!StringUtil.isEmpty(groupList.getList())) {
                        synchronized (mMulticastGroupMap) {
                            for (MulticastGroup multicastGroup : groupList.getList()) {
                                MulticastGroupModel multicastGroupModel = mMulticastGroupMap.get(multicastGroup.getGroupSn());
                                if (groupList.getUpdateType() == UpdateTypeEnum.UPDATE_NORMAL.getValue()) {
                                    if (multicastGroupModel != null) {
                                        multicastGroupModel.setMulticastGroup(multicastGroup);
                                    } else {
                                        multicastGroupModel = GroupUtil.convertToModel(multicastGroup, mContext, getMasterNo());
                                        mMulticastGroupMap.put(multicastGroup.getGroupSn(),multicastGroupModel);
                                    }
                                } else {
                                    //停止广播
                                    if(multicastGroupModel != null){
                                        mMulticastGroupMap.remove(multicastGroup.getGroupSn());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
