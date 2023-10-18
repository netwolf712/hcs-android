package com.hcs.android.business.manager;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.R;
import com.hcs.android.business.constant.BooleanConstant;
import com.hcs.android.business.constant.PreferenceConstant;
import com.hcs.android.business.constant.TimeSlotTypeEnum;
import com.hcs.android.business.entity.TimeSlot;
import com.hcs.android.business.service.TimeSlotService;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.TimeUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 时间段管理器
 */
public class TimeSlotManager {

    private final Context mContext;

    private final Map<TimeSlotTypeEnum,List<TimeSlot>> mTimeSlotMap;

    private TimeSlotManager(){
        mTimeSlotMap = new HashMap<>();
        mContext = BusinessApplication.getAppContext();
        loadAllData();
    }

    private static final class MInstanceHolder {
        @SuppressLint("StaticFieldLeak")
        static final TimeSlotManager mInstance = new TimeSlotManager();
    }

    public static TimeSlotManager getInstance(){
        return MInstanceHolder.mInstance;
    }

    /**
     * 加载所有数据
     */
    public void loadAllData(){
        synchronized (mTimeSlotMap){
            for(TimeSlotTypeEnum timeSlotTypeEnum : TimeSlotTypeEnum.values()){
                mTimeSlotMap.put(timeSlotTypeEnum,loadTimeSlots(timeSlotTypeEnum));
            }
        }
    }
    private List<TimeSlot> loadTimeSlots(@NonNull TimeSlotTypeEnum typeEnum){
        return TimeSlotService.getInstance().getTimeSlotList(typeEnum.getValue());
    }


    public void reloadData(TimeSlotTypeEnum typeEnum){
        if(typeEnum == null){
            loadAllData();
        }else {
            mTimeSlotMap.put(typeEnum,loadTimeSlots(typeEnum));
        }
    }

    public List<TimeSlot> getTimeSlotList(@NonNull TimeSlotTypeEnum typeEnum){
        return mTimeSlotMap.get(typeEnum);
    }
    /**
     * 更新某一个种类的时间段
     * 只能一起更新
     * 因为需要将之前的全部删除再次写入（没错，就是为算法偷懒）
     * @param timeSlotList 时间段列表
     * @param typeEnum 类型，为null表示全部
     */
    public void updateTimeSlots(List<TimeSlot> timeSlotList, TimeSlotTypeEnum typeEnum,Long updateTime){
        if(typeEnum == null){
            TimeSlotService.getInstance().deleteAll();
        }else {
            TimeSlotService.getInstance().deleteByType(typeEnum.getValue());
        }
        TimeSlotService.getInstance().insertAll(timeSlotList);
        reloadData(typeEnum);
        //更新时间段刷新时间
        long currentTime = updateTime == null ? System.currentTimeMillis() : updateTime;
        SettingsHelper.getInstance(mContext).putData(PreferenceConstant.TIME_SLOT_UPDATE_TIME,currentTime);
    }

    public void updateTimeSlot(TimeSlot timeSlot){
        TimeSlotService.getInstance().updateTimeSlot(timeSlot);
        synchronized (mTimeSlotMap){
            List<TimeSlot> timeSlotList = mTimeSlotMap.get(TimeSlotTypeEnum.findById(timeSlot.getType()));
            if(!StringUtil.isEmpty(timeSlotList)){
                for(TimeSlot tmp : timeSlotList){
                    if(Objects.equals(tmp.getUid(),timeSlot.getUid())){
                        timeSlotList.remove(tmp);
                        break;
                    }
                }
            }
            if(timeSlotList == null){
                timeSlotList = new ArrayList<>();
                mTimeSlotMap.put(TimeSlotTypeEnum.findById(timeSlot.getType()),timeSlotList);
            }
            timeSlotList.add(timeSlot);
        }
    }

    public void removeTimeSlot(TimeSlotTypeEnum timeSlotTypeEnum,int uid){
        synchronized (mTimeSlotMap){
            List<TimeSlot> timeSlotList = mTimeSlotMap.get(timeSlotTypeEnum);
            if(!StringUtil.isEmpty(timeSlotList)){
                for(TimeSlot tmp : timeSlotList){
                    if(Objects.equals(tmp.getUid(),uid)){
                        timeSlotList.remove(tmp);
                        break;
                    }
                }
            }
            TimeSlotService.getInstance().getTimeSlotById(uid);
        }
    }

    /**
     * 获取所有的时间段
     */
    public List<TimeSlot> getAllTimeSlotList(){
        List<TimeSlot> timeSlotList = new ArrayList<>();
        for(TimeSlotTypeEnum timeSlotTypeEnum : TimeSlotTypeEnum.values()){
            timeSlotList.addAll(getTimeSlotList(timeSlotTypeEnum));
        }
        return timeSlotList;
    }

    /**
     * 获取时间段的最后一次配置的时间
     */
    public long getLastUpdateTime(){
        return SettingsHelper.getInstance(mContext).getLong(PreferenceConstant.TIME_SLOT_UPDATE_TIME,mContext.getResources().getInteger(R.integer.default_step_master_update_time));
    }
    /**
     * 获取时间在这一天中过去的毫秒数
     * @param millisecond unix时间戳
     * @return 时间在一天中的毫秒数
     */
    private long getTimeOfDay(long millisecond){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(millisecond));
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return (hour * TimeUtil.HOUR + minute * TimeUtil.MINUTE + second) * TimeUtil.SECOND_MS;
    }
    /**
     * 获取这一天是周几
     * @param millisecond unix时间戳
     * @return 周几，星期天为第一天
     */
    private int getDayOfWeek(long millisecond){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(millisecond));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        boolean isFirstSunday = (calendar.getFirstDayOfWeek() == Calendar.SUNDAY);
        if(isFirstSunday){
            return (dayOfWeek - 1);
        }else{
            if(dayOfWeek == 7){
                return 0;
            }else{
                return dayOfWeek;
            }
        }
    }

    public boolean isInTime(long currentTime, @NonNull TimeSlot timeSlot){
        int dayOfWeek = getDayOfWeek(currentTime);
        long timeOfDay = getTimeOfDay(currentTime);
        if(timeSlot.getState() == BooleanConstant.INTEGER_TRUE
                && timeSlot.getPeriod().contains(String.valueOf(dayOfWeek))) {
            return timeSlot.getState() == BooleanConstant.INTEGER_TRUE
                    && timeSlot.getStartTime() >= timeOfDay && timeOfDay <= timeSlot.getEndTime();
        }
        return false;
    }
    /**
     * 是否在时间段内
     * @param callTime unix时间戳
     * @param timeSlotList 时间段列表
     * @return true 在时间段内，false 没在时间段内
     */
    private boolean isInTime(Long callTime,List<TimeSlot> timeSlotList){
        if (StringUtil.isEmpty(timeSlotList)) {
            return false;
        }
        int dayOfWeek = getDayOfWeek(callTime);
        long timeOfDay = getTimeOfDay(callTime);
        for (TimeSlot timeSlot : timeSlotList){
            if(timeSlot.getState() == BooleanConstant.INTEGER_TRUE
                    && timeSlot.getPeriod().contains(String.valueOf(dayOfWeek))) {
                if (timeSlot.getState() == BooleanConstant.INTEGER_TRUE
                        && timeSlot.getStartTime() >= timeOfDay && timeOfDay <= timeSlot.getEndTime()) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 是否在禁止上传的时间段内
     * @param callTime 呼叫的时间，unix时间戳
     * @return true 禁止上传，false 可以上传
     */
    public boolean isInForbidCallUploadTime(Long callTime){
        synchronized (mTimeSlotMap) {
            return isInTime(callTime,mTimeSlotMap.get(TimeSlotTypeEnum.SUPERIOR_FORBID_UPLOAD_CALL));
        }
    }

    /**
     * 是否在自动托管的时间内
     * @param callTime 呼叫时间
     * @return true 需要托管，false 不用托管
     */
    public boolean isInAutoTrustTime(Long callTime){
        synchronized (mTimeSlotMap) {
            return isInTime(callTime,mTimeSlotMap.get(TimeSlotTypeEnum.AUTO_TRUST));
        }
    }

    /**
     * 判断是否晚上
     * 对一些需要区分白天黑夜的操作有效
     * @param callTime 呼叫时间
     * @return true 晚上，false 白天
     */
    public boolean isNight(Long callTime){
        synchronized (mTimeSlotMap){
            return isInTime(callTime,mTimeSlotMap.get(TimeSlotTypeEnum.NIGHT));
        }
    }

    /**
     * 是否在自动广播的时间内
     */
    public boolean isInAutoAudioMulticastTime(Long callTime){
        synchronized (mTimeSlotMap){
            return isInTime(callTime,mTimeSlotMap.get(TimeSlotTypeEnum.AUTO_AUDIO_MULTICAST));
        }
    }

    /**
     * 获取指定的时间段设置
     */
    public TimeSlot getTimeSlot(TimeSlotTypeEnum slotTypeEnum,Integer timeSlotId){
        synchronized (mTimeSlotMap){
            List<TimeSlot> timeSlotList = mTimeSlotMap.get(slotTypeEnum);
            if(!StringUtil.isEmpty(timeSlotList)){
                for(TimeSlot timeSlot : timeSlotList){
                    if(Objects.equals(timeSlot.getUid(),timeSlotId)){
                        return timeSlot;
                    }
                }
            }
            return null;
        }
    }
}
