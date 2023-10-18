package com.hcs.android.business.service;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.hcs.android.business.dao.DataBaseHelper;
import com.hcs.android.business.dao.MulticastGroupDao;
import com.hcs.android.business.entity.MulticastGroup;
import com.hcs.android.common.util.StringUtil;

import java.util.List;

/**
 * 病区服务
 */
public class MulticastGroupService {
    private final MulticastGroupDao mMulticastGroupDao;

    private static final class MInstanceHolder {
        static final MulticastGroupService mInstance = new MulticastGroupService();
    }

    public static MulticastGroupService getInstance(){
        return MInstanceHolder.mInstance;
    }

    public MulticastGroupService(){
        mMulticastGroupDao = DataBaseHelper.getInstance().multicastGroupDao();
    }

    public MulticastGroup getMulticastGroup(Integer groupSn){
        if(groupSn == null ){
            return null;
        }
        return mMulticastGroupDao.findOne(groupSn);
    }

    public MulticastGroup getMulticastGroupByTimeSlot(Integer timeSlotId){
        if(timeSlotId == null ){
            return null;
        }
        return mMulticastGroupDao.findOneByTimeSlot(timeSlotId);
    }

    public MulticastGroup getMulticastGroupById(int uid){
        return mMulticastGroupDao.findOneById(uid);
    }

    public LiveData<List<MulticastGroup>> getMulticastGroupList(int offset, int limit){
        return mMulticastGroupDao.getAll(limit,offset);
    }

    public List<MulticastGroup> getMulticastGroupList(){
        return mMulticastGroupDao.getAll();
    }

    /**
     * 获取所有分区的基本设置
     */
    public List<MulticastGroup> getBaseMulticastGroupList(){
        return mMulticastGroupDao.getBaseGroups();
    }

    /**
     * 获取带定时功能的分区的设置
     */
    public List<MulticastGroup> getTimeSlotMulticastGroupList(){
        return mMulticastGroupDao.getTimeSlotGroups();
    }

    public Integer getMulticastGroupCount(){
        return mMulticastGroupDao.countAll();
    }

    /**
     * 更新或添加
     */
    public void updateMulticastGroup(@NonNull MulticastGroup multicastGroup){
        MulticastGroup oldMulticastGroup = getMulticastGroup(multicastGroup.getGroupSn());
        multicastGroup.setUpdateTime(System.currentTimeMillis());
        if(oldMulticastGroup != null){
            multicastGroup.setUid(oldMulticastGroup.getUid());
            mMulticastGroupDao.update(multicastGroup);
        }else{
            mMulticastGroupDao.insert(multicastGroup);
        }
    }

    public void deleteMulticastGroup(@NonNull MulticastGroup multicastGroup){
        mMulticastGroupDao.delete(multicastGroup);
    }


    public void deleteAll(){
        mMulticastGroupDao.deleteAll();
    }

    public void insertAll(List<MulticastGroup> sectionList){
        if(StringUtil.isEmpty(sectionList)){
            return;
        }
        MulticastGroup[] MulticastGroups = sectionList.toArray(new MulticastGroup[0]);
        mMulticastGroupDao.insertAll(MulticastGroups);
    }
}
