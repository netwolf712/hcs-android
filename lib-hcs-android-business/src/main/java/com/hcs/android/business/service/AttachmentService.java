package com.hcs.android.business.service;

import androidx.annotation.NonNull;

import com.hcs.android.business.dao.AttachmentDao;
import com.hcs.android.business.dao.DataBaseHelper;
import com.hcs.android.business.entity.Attachment;
import com.hcs.android.common.util.StringUtil;

import java.util.List;

/**
 * 附件
 */
public class AttachmentService {
    private final AttachmentDao mAttachmentDao;
    private static AttachmentService mInstance = null;
    public static AttachmentService getInstance(){
        if(mInstance == null){
            synchronized (AttachmentService.class){
                if(mInstance == null) {
                    mInstance = new AttachmentService();
                }
            }
        }
        return mInstance;
    }

    public AttachmentService(){
        mAttachmentDao = DataBaseHelper.getInstance().attachmentDao();
    }


    public Attachment getAttachmentById(int uid){
        return mAttachmentDao.findOneById(uid);
    }

    public List<Attachment> getAttachmentList(int offset, int limit){
        return mAttachmentDao.getAll(limit,offset);
    }

    public List<Attachment> getAttachmentList(){
        return mAttachmentDao.getAll();
    }

    public Integer getAttachmentCount(){
        return mAttachmentDao.countAll();
    }

    public List<Attachment> getAttachmentList(String use,int offset, int limit){
        return mAttachmentDao.getListByUse(use,limit,offset);
    }

    public Integer getAttachmentCount(String use){
        return mAttachmentDao.countByUse(use);
    }

    /**
     * 更新或添加
     */
    public void updateAttachment(@NonNull Attachment attachment){
        attachment.setUpdateTime(System.currentTimeMillis());
        if(attachment.getUid() != null){
            mAttachmentDao.update(attachment);
        }else{
            mAttachmentDao.insert(attachment);
        }
    }

    public void deleteAttachment(@NonNull Attachment attachment){
        mAttachmentDao.delete(attachment);
    }


    public void deleteAll(){
        mAttachmentDao.deleteAll();
    }

    public void insertAll(List<Attachment> sectionList){
        if(StringUtil.isEmpty(sectionList)){
            return;
        }
        Attachment[] attachments = sectionList.toArray(new Attachment[0]);
        mAttachmentDao.insertAll(attachments);
    }
}
