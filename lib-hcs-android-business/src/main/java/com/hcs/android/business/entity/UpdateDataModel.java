package com.hcs.android.business.entity;

import android.os.Build;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据更新模块
 */
public class UpdateDataModel<T> {
    /**
     * 同步对象
     */
    private final Object mSynObj = new Object();

    /**
     * 数据总长度
     */
    private Integer mTotalLength = 0;

    /**
     * 等待更新的容器，在数据收齐之后才开始更新
     * page,data
     */
    Map<Integer, ResponseList<T>> mWaitToChangeData = new HashMap<>();

    /**
     * 完整性校验
     * @return true表示数据获取完全
     */
    public boolean checkComplete(){
        synchronized (mSynObj) {
            if (mWaitToChangeData == null) {
                return false;
            }
            int lengthCount = 0;
            Integer startPage = 0;
            while (lengthCount < mTotalLength) {
                ResponseList<T> data = mWaitToChangeData.get(startPage);
                if (data == null) {
                    return false;
                }
                if (!StringUtil.isEmpty(data.getList())) {
                    lengthCount += data.getList().size();
                }
                startPage++;
            }
            return true;
        }
    }

    /**
     * 插入数据
     */
    public void insertData(@NonNull ResponseList<T> data){
        mTotalLength = Math.max(mTotalLength,data.getTotalLength());
        synchronized (mSynObj) {
            mWaitToChangeData.put(data.getCurrentPage(), data);
        }
    }

    /**
     * 清除所有数据
     */
    public void clearData(){
        synchronized (mSynObj){
            mWaitToChangeData.clear();
            mTotalLength = 0;
        }
    }

    /**
     * 对列表进行重排序
     * 否则从map里取上来的是乱的
     */
    private List<ResponseList<T>> sortList(List<ResponseList<T>> orgList){
        //排序
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            orgList.sort(Comparator.comparingInt(RequestListBase::getCurrentPage));
        }
        return orgList;
    }

    /**
     * 返回数据列表
     */
    public List<ResponseList<T>> getList(){
        return sortList(new ArrayList<>(mWaitToChangeData.values()));
    }
}
