package com.hcs.android.business.service;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.hcs.android.business.dao.DataBaseHelper;
import com.hcs.android.business.dao.HandoverLogDao;
import com.hcs.android.business.entity.HandoverLog;
import com.hcs.android.common.util.StringUtil;

import java.util.List;

/**
 * 交班服务
 */
public class HandoverLogService {
    private final HandoverLogDao mHandoverLogDao;
    private static HandoverLogService mInstance = null;
    public static HandoverLogService getInstance(){
        if(mInstance == null){
            synchronized (HandoverLogService.class){
                if(mInstance == null) {
                    mInstance = new HandoverLogService();
                }
            }
        }
        return mInstance;
    }

    public HandoverLogService(){
        mHandoverLogDao = DataBaseHelper.getInstance().handoverLogDao();
    }



    public List<HandoverLog> getHandoverLogList(int limit,int offset){
        return mHandoverLogDao.getAll(limit,offset);
    }

    //多条件查询
    public Integer count(Integer state){
        return mHandoverLogDao.count(state);
    }
    public List<HandoverLog> getHandoverLogList(Integer state, int limit, int offset){
        return mHandoverLogDao.getHandoverLogList(state,limit,offset);
    }

    public int getHandoverLogCount(){
        return mHandoverLogDao.countAll();
    }

    public HandoverLog getHandoverLog(Integer uid){
        if(uid == null){
            return null;
        }
        return mHandoverLogDao.findOne(uid);
    }

    /**
     * 更新或添加
     */
    public void updateHandoverLog(@NonNull HandoverLog handoverLog){
        new Thread(()->{
            HandoverLog oldHandoverLog = getHandoverLog(handoverLog.getUid());
            handoverLog.setUpdateTime(System.currentTimeMillis());
            if(oldHandoverLog != null){
                handoverLog.setUid(oldHandoverLog.getUid());
                mHandoverLogDao.update(handoverLog);
            }else{
                mHandoverLogDao.insert(handoverLog);
            }
        }).start();
    }

    public void deleteHandoverLog(@NonNull HandoverLog HandoverLog){
        mHandoverLogDao.delete(HandoverLog);
    }


    public void deleteAll(){
        mHandoverLogDao.deleteAll();
    }

    public void insertAll(List<HandoverLog> HandoverLogList){
        if(StringUtil.isEmpty(HandoverLogList)){
            return;
        }
        HandoverLog[] HandoverLogs = HandoverLogList.toArray(new HandoverLog[0]);
        mHandoverLogDao.insertAll(HandoverLogs);
    }
}
