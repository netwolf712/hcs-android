package com.hcs.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.common.util.NetUtil;
import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class FriendViewModel extends BaseRefreshViewModel<DeviceModel, CommonViewModel> {

    public FriendViewModel(@NonNull Application application, CommonViewModel model) {
        super(application, model);
    }

    public void refreshData() {
        postShowNoDataViewEvent(false);
        if (!NetUtil.checkNetToast()) {
            postShowNetWorkErrViewEvent(true);
            return;
        }

    }

    @Override
    public void loadMore() {
        refreshData();
    }
}

