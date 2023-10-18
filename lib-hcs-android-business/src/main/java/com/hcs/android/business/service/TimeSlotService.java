package com.hcs.android.business.service;

import androidx.annotation.NonNull;

import com.hcs.android.business.dao.DataBaseHelper;
import com.hcs.android.business.dao.StepMasterDao;
import com.hcs.android.business.dao.TimeSlotDao;
import com.hcs.android.business.entity.StepMaster;
import com.hcs.android.business.entity.TimeSlot;
import com.hcs.android.common.util.StringUtil;

import java.util.List;

/**
 * 时间段
 */
public class TimeSlotService {
    private final TimeSlotDao mTimeSlotDao;
    private static TimeSlotService mInstance = null;
    public static TimeSlotService getInstance(){
        if(mInstance == null){
            synchronized (TimeSlotService.class){
                if(mInstance == null) {
                    mInstance = new TimeSlotService();
                }
            }
        }
        return mInstance;
    }

    public TimeSlotService(){
        mTimeSlotDao = DataBaseHelper.getInstance().timeSlotDao();
    }


    public TimeSlot getTimeSlotById(int uid){
        return mTimeSlotDao.findOneById(uid);
    }

    public List<TimeSlot> getTimeSlotList(int offset, int limit){
        return mTimeSlotDao.getAll(limit,offset);
    }

    public List<TimeSlot> getTimeSlotList(){
        return mTimeSlotDao.getAll();
    }

    public Integer getTimeSlotCount(){
        return mTimeSlotDao.countAll();
    }

    public List<TimeSlot> getTimeSlotList(int type,int offset, int limit){
        return mTimeSlotDao.getListByType(type,limit,offset);
    }

    public List<TimeSlot> getTimeSlotList(int type){
        return mTimeSlotDao.getListByType(type);
    }

    public Integer getTimeSlotCount(int type){
        return mTimeSlotDao.countByType(type);
    }

    /**
     * 更新或添加
     */
    public void updateTimeSlot(@NonNull TimeSlot timeSlot){
        timeSlot.setUpdateTime(System.currentTimeMillis());
        if(timeSlot.getUid() != null){
            mTimeSlotDao.update(timeSlot);
        }else{
            mTimeSlotDao.insert(timeSlot);
        }
    }

    public void deleteTimeSlot(@NonNull TimeSlot timeSlot){
        mTimeSlotDao.delete(timeSlot);
    }


    public void deleteAll(){
        mTimeSlotDao.deleteAll();
    }

    public void insertAll(List<TimeSlot> timeSlotList){
        if(StringUtil.isEmpty(timeSlotList)){
            return;
        }
        for(TimeSlot timeSlot : timeSlotList){
            timeSlot.setUid(null);
        }
        TimeSlot[] timeSlots = timeSlotList.toArray(new TimeSlot[0]);
        mTimeSlotDao.insertAll(timeSlots);
    }

    public void deleteByType(int type){
        mTimeSlotDao.deleteByType(type);
    }
}
