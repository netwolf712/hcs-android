package com.hcs.android.business.manager;


import com.hcs.android.business.dao.DataBaseHelper;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.business.entity.UpdateDataModel;
import com.hcs.android.business.service.IDbService;

import java.util.List;

/**
 * 数据更新管理器
 */
public class UpdateDataHelper<T> {

    /**
     * 位置
     */
    private final UpdateDataModel<T> mDataContainer;

    private final IDbService<T> mDbService;
    public UpdateDataHelper(IDbService<T> dbService){
        mDataContainer = new UpdateDataModel<>();
        mDbService = dbService;
    }

    /**
     * 新增位置数据
     * @return 数据是否完整，是则返回true，代表数据库更新完毕，需要更新缓存
     */
    public boolean addData(ResponseList<T> responseList){
        mDataContainer.insertData(responseList);
        if(mDataContainer.checkComplete()){
            //如果数据获取完全，则开始更新数据库
            DataBaseHelper.getInstance().runInTransaction(()->{
                mDbService.deleteAll();
                List<ResponseList<T>> responseListList = mDataContainer.getList();
                for(ResponseList<T> tmp : responseListList){
                    mDbService.insertAll(tmp.getList());
                }
                mDataContainer.clearData();
            });
            return true;
        }
        return false;
    }
}
