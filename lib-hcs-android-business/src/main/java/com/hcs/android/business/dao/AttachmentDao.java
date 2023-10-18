package com.hcs.android.business.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hcs.android.business.entity.Attachment;

import java.util.List;

@Dao
public interface AttachmentDao {
    @Query("SELECT * FROM Attachment")
    List<Attachment> getAll();

    @Query("SELECT * FROM Attachment LIMIT :limit OFFSET :offset")
    List<Attachment> getAll(int limit, int offset);


    @Query("SELECT count(1) FROM Attachment")
    Integer countAll();


    /**
     * 根据用途查询附件列表
     * @param use 用途
     */
    @Query("SELECT * FROM Attachment where use like '%' || :use || '%'  LIMIT :limit OFFSET :offset")
    List<Attachment> getListByUse(String use,int limit, int offset);

    @Query("SELECT count(1) FROM Attachment where use like '%' || :use || '%'")
    Integer countByUse(String use);

    @Query("SELECT * FROM Attachment WHERE uid = :uid LIMIT 1")
    Attachment findOneById(int uid);

    @Insert
    void insertAll(Attachment... attachments);

    @Insert
    void insert(Attachment attachment);

    @Delete
    void delete(Attachment attachment);

    @Update
    void update(Attachment attachment);

    @Query("DELETE FROM Attachment")
    void deleteAll();
}