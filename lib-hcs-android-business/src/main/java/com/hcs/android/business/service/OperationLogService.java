package com.hcs.android.business.service;

import androidx.annotation.NonNull;

import com.hcs.android.business.dao.OperationLogDao;
import com.hcs.android.business.dao.DataBaseHelper;
import com.hcs.android.business.entity.OperationLog;
import com.hcs.android.common.util.StringUtil;

import java.util.List;

/**
 * 操作日志服务
 */
public class OperationLogService {
    private final OperationLogDao mOperationLogDao;
    private static OperationLogService mInstance = null;
    public static OperationLogService getInstance(){
        if(mInstance == null){
            synchronized (OperationLogService.class){
                if(mInstance == null) {
                    mInstance = new OperationLogService();
                }
            }
        }
        return mInstance;
    }

    public OperationLogService(){
        mOperationLogDao = DataBaseHelper.getInstance().operationLogDao();
    }



    public List<OperationLog> getOperationLogList(int limit,int offset){
        return mOperationLogDao.getAll(limit,offset);
    }

    //多条件查询
    public Integer count(Integer deviceType,String deviceName,String deviceId,String dateStart,String dateEnd){
        return mOperationLogDao.count(deviceType,deviceName,deviceId,dateStart,dateEnd);
    }
    public List<OperationLog> getOperationLogList(Integer deviceType, String deviceName, String deviceId, String dateStart, String dateEnd, int limit, int offset){
        return mOperationLogDao.getOperationLogList(deviceType,deviceName,deviceId,dateStart,dateEnd,limit,offset);
    }

    public int getOperationLogCount(){
        return mOperationLogDao.countAll();
    }

    public OperationLog getOperationLog(Integer uid){
        if(uid == null){
            return null;
        }
        return mOperationLogDao.findOne(uid);
    }

    public OperationLog getOperationLog(@NonNull String callRef){
        return mOperationLogDao.findOne(callRef);
    }

    /**
     * 更新或添加
     */
    public void updateOperationLog(@NonNull OperationLog operationLog){
        new Thread(()->{
            OperationLog oldOperationLog = getOperationLog(operationLog.getCallRef());
            operationLog.setUpdateTime(System.currentTimeMillis());
            if(oldOperationLog != null){
                operationLog.setUid(oldOperationLog.getUid());
                mOperationLogDao.update(operationLog);
            }else{
                mOperationLogDao.insert(operationLog);
            }
        }).start();
    }

    public void deleteOperationLog(@NonNull OperationLog operationLog){
        mOperationLogDao.delete(operationLog);
    }


    public void deleteAll(){
        mOperationLogDao.deleteAll();
    }

    public void insertAll(List<OperationLog> operationLogList){
        if(StringUtil.isEmpty(operationLogList)){
            return;
        }
        OperationLog[] OperationLogs = operationLogList.toArray(new OperationLog[0]);
        mOperationLogDao.insertAll(OperationLogs);
    }
}
