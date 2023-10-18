package com.hcs.android.business.request.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import com.hcs.android.business.entity.OperationLogModel;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.business.service.OperationLogService;
import com.hcs.android.common.util.NetUtil;
import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;
import com.hcs.android.ui.util.UIThreadUtil;

import org.simple.eventbus.EventBus;

public class CallLogViewModel extends BaseRefreshViewModel<OperationLogModel, CommonViewModel> {

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

    private Integer mTotalSize = 0;
    public void setTotalSize(Integer totalSize){
        this.mTotalSize = totalSize;
    }
    public Integer getTotalSize(){
        return mTotalSize;
    }

    /**
     * 设备类型筛选器
     */
    private Integer filterDeviceType;
    public Integer getFilterDeviceType() {
        return filterDeviceType;
    }
    public void setFilterDeviceType(Integer filterDeviceType) {
        this.filterDeviceType = filterDeviceType;
    }

    /**
     * 设备名称筛选器
     */
    private String filterDeviceName;
    public String getFilterDeviceName() {
        return filterDeviceName;
    }
    public void setFilterDeviceName(String filterDeviceName) {
        this.filterDeviceName = filterDeviceName;
    }

    /**
     * 设备序列号筛选器
     */
    private String filterDeviceId;
    public String getFilterDeviceId() {
        return filterDeviceId;
    }
    public void setFilterDeviceId(String filterDeviceId) {
        this.filterDeviceId = filterDeviceId;
    }

    /**
     * 起始日期筛选器
     */
    private String filterStartDate;
    public String getFilterStartDate() {
        return filterStartDate;
    }
    public void setFilterStartDate(String filterStartDate) {
        this.filterStartDate = filterStartDate;
    }

    /**
     * 结束日期筛选器
     */
    private String filterEndDate;
    public String getFilterEndDate() {
        return filterEndDate;
    }
    public void setFilterEndDate(String filterEndDate) {
        this.filterEndDate = filterEndDate;
    }

    public CallLogViewModel(@NonNull Application application, CommonViewModel model) {
        super(application, model);
        //注册事件管道
        EventBus.getDefault().register(this);
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

    void loadData() {
        new Thread(()->{
            mTotalSize =  OperationLogService.getInstance().count(filterDeviceType, filterDeviceName, filterDeviceId, filterStartDate, filterEndDate);
        }).start();

        ObservableArrayList<OperationLogModel> observableArrayList = WorkManager.getInstance().getOperationLogList(filterDeviceType, filterDeviceName, filterDeviceId, filterStartDate, filterEndDate, mPageSize, mCurrentPage * mPageSize);
        if(observableArrayList != null){
            observableArrayList.addOnListChangedCallback(new ObservableList.OnListChangedCallback<ObservableArrayList<OperationLogModel>>() {
                @Override
                public void onChanged(ObservableArrayList<OperationLogModel> sender) {

                }

                @Override
                public void onItemRangeChanged(ObservableArrayList<OperationLogModel> sender, int positionStart, int itemCount) {

                }

                @Override
                public void onItemRangeInserted(ObservableArrayList<OperationLogModel> sender, int positionStart, int itemCount) {
                    UIThreadUtil.runOnUiThread(()->{
                        mList.addAll(sender);
                    });
                }

                @Override
                public void onItemRangeMoved(ObservableArrayList<OperationLogModel> sender, int fromPosition, int toPosition, int itemCount) {

                }

                @Override
                public void onItemRangeRemoved(ObservableArrayList<OperationLogModel> sender, int positionStart, int itemCount) {

                }
            });
        }
    }

    @Override
    public void loadMore() {
        if((mCurrentPage + 1) * mPageSize < mTotalSize){
            mCurrentPage++;
        }
        loadData();
    }
}

