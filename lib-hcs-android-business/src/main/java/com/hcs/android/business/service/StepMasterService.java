package com.hcs.android.business.service;

import androidx.annotation.NonNull;

import com.hcs.android.business.dao.DataBaseHelper;
import com.hcs.android.business.dao.StepMasterDao;
import com.hcs.android.business.entity.StepMaster;
import com.hcs.android.common.util.StringUtil;

import java.util.List;

/**
 * 继任主机
 */
public class StepMasterService {
    private final StepMasterDao mStepMasterDao;
    private static StepMasterService mInstance = null;
    public static StepMasterService getInstance(){
        if(mInstance == null){
            synchronized (StepMasterService.class){
                if(mInstance == null) {
                    mInstance = new StepMasterService();
                }
            }
        }
        return mInstance;
    }

    public StepMasterService(){
        mStepMasterDao = DataBaseHelper.getInstance().stepMasterDao();
    }

    public StepMaster getStepMasterByNo(String masterNo){
        return mStepMasterDao.findOneByMasterNo(masterNo);
    }

    public List<StepMaster> getStepMasterList(int offset, int limit){
        return mStepMasterDao.getAll(limit,offset);
    }

    public List<StepMaster> getStepMasterList(){
        return mStepMasterDao.getAll();
    }

    public Integer getStepMasterCount(){
        return mStepMasterDao.countAll();
    }

    public List<StepMaster> getStepMasterList(Integer masterType,int offset, int limit){
        if(masterType == null){
            return mStepMasterDao.getAll(limit,offset);
        }else {
            return mStepMasterDao.getListByType(masterType, limit, offset);
        }
    }

    public List<StepMaster> getStepMasterList(int masterType){
        return mStepMasterDao.getListByType(masterType);
    }

    public Integer getStepMasterCount(Integer masterType){
        if(masterType == null){
            return mStepMasterDao.countAll();
        }else {
            return mStepMasterDao.countByType(masterType);
        }
    }

    /**
     * 更新或添加
     */
    public void updateStepMaster(@NonNull StepMaster stepMaster){
        StepMaster oldStepMaster = getStepMasterByNo(stepMaster.getMasterNo());
        stepMaster.setUpdateTime(System.currentTimeMillis());
        if(oldStepMaster != null){
            mStepMasterDao.update(stepMaster);
        }else{
            mStepMasterDao.insert(stepMaster);
        }
    }

    public void deleteStepMaster(@NonNull StepMaster stepMaster){
        mStepMasterDao.delete(stepMaster);
    }

    public void deleteByType(int masterType){
        mStepMasterDao.deleteByType(masterType);
    }
    public void deleteAll(){
        mStepMasterDao.deleteAll();
    }

    public void insertAll(List<StepMaster> stepMasterList){
        if(StringUtil.isEmpty(stepMasterList)){
            return;
        }
        for(StepMaster stepMaster : stepMasterList){
            stepMaster.setUid(null);
        }
        StepMaster[] stepMasters = stepMasterList.toArray(new StepMaster[0]);
        mStepMasterDao.insertAll(stepMasters);
    }
}
