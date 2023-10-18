package com.hcs.android.business.request.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hcs.android.business.entity.HandoverLog;
import com.hcs.android.business.service.HandoverLogService;
import com.hcs.android.common.util.NetUtil;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;
import com.hcs.android.ui.util.UIThreadUtil;

import java.util.List;

public class HandoverViewModel extends BaseRefreshViewModel<HandoverLog, CommonViewModel> {

    private Integer mCurrentPage = 0;
    public void setCurrentPage(Integer currentPage) {
        this.mCurrentPage = currentPage;
    }
    public Integer getCurrentPage() {
        return mCurrentPage;
    }

    private Integer mPageSize = 30;
    public void setPageSize(Integer pageSize) {
        this.mPageSize = pageSize;
    }
    public Integer getPageSize() {
        return mPageSize;
    }

    private Integer mTotalSize;
    public void setTotalSize(Integer totalSize){
        this.mTotalSize = totalSize;
    }
    public Integer getTotalSize(){
        return mTotalSize;
    }

    /**
     * 记录主题筛选器
     */
    private Integer filterState;
    public Integer getFilterState() {
        return filterState;
    }
    public void setFilterState(Integer filterState) {
        this.filterState = filterState;
    }
    

    public HandoverViewModel(@NonNull Application application, CommonViewModel model) {
        super(application, model);
    }

    public void refreshData() {
        postShowNoDataViewEvent(false);
//        if (!NetUtil.checkNetToast()) {
//            postShowNetWorkErrViewEvent(true);
//            return;
//        }
        mList.clear();
        loadData();
    }

    void loadData(){
        new Thread(()->{
            setTotalSize(HandoverLogService.getInstance().count(filterState));
            List<HandoverLog> handoverLogList = HandoverLogService.getInstance().getHandoverLogList(filterState,mPageSize,mCurrentPage * mPageSize);
            if(!StringUtil.isEmpty(handoverLogList)){
                UIThreadUtil.runOnUiThread(()->{
                    mList.addAll(handoverLogList);
                });
            }
        }).start();

    }

    @Override
    public void loadMore() {
        refreshData();
    }

}

