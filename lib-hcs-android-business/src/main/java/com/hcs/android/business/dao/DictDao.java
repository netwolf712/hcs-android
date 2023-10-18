package com.hcs.android.business.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.hcs.android.business.entity.Dict;

import java.util.List;

@Dao
public interface DictDao {
    @Query("SELECT * FROM Dict")
    List<Dict> getAll();

    @Query("SELECT * FROM Dict WHERE uid IN (:ids) order by sort asc")
    List<Dict> loadAllByIds(int[] ids);

    @Query("SELECT * FROM Dict WHERE dict_type like :dictType and dict_value IN (:values)  order by sort asc")
    List<Dict> getDictList(String dictType,Integer[] values);

    /**
     * 统计数据库中记录条数
     */
    @Query("SELECT count(*) FROM Dict")
    int countAll();

    /**
     * 根据id获取
     */
    @Query("SELECT * FROM Dict WHERE uid = :uid LIMIT 1")
    Dict findOne(int uid);

    /**
     * 根据字典值
     */
    @Query("SELECT * FROM Dict WHERE dict_type like :dictType and dict_value = :dictValue LIMIT 1")
    Dict findOne(String dictType,int dictValue);


    /**
     * 根据类型获取字典列表
     */
    @Query("SELECT * FROM Dict WHERE dict_type LIKE :dictType  order by sort asc")
    List<Dict> getDictList(String dictType);

    @Insert
    void insertAll(Dict... dicts);

    @Insert
    void insert(Dict dict);

    @Delete
    void delete(Dict dict);

    @Update
    void update(Dict dict);

    /**
     * 删除所有
     */
    @Query("DELETE FROM Dict")
    void deleteAll();

    /**
     * 删除部分
     */
    @Query("DELETE FROM Dict WHERE dict_type LIKE :dictType")
    void deleteByType(String dictType);
}