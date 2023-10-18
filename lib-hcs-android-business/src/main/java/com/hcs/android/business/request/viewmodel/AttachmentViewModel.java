package com.hcs.android.business.request.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.AttachmentUseEnum;
import com.hcs.android.business.entity.Attachment;
import com.hcs.android.business.request.model.AttachmentRequestModel;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.NetUtil;
import com.hcs.android.server.entity.ObservableData;
import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;
import com.hcs.android.ui.util.UIThreadUtil;

import java.util.List;

public class AttachmentViewModel extends BaseRefreshViewModel<Attachment, CommonViewModel> {


    private final AttachmentRequestModel mAttachmentRequestModel;
    public AttachmentViewModel(@NonNull Application application, CommonViewModel model) {
        super(application, model);
        mAttachmentRequestModel = new AttachmentRequestModel();
    }

  private AttachmentUseEnum useEnum = AttachmentUseEnum.RING;

    public AttachmentUseEnum getUseEnum() {
        return useEnum;
    }

    public void setUseEnum(AttachmentUseEnum useEnum) {
        this.useEnum = useEnum;
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

        ObservableData<List<Attachment>> listObservableData = null;
        switch (useEnum){
            case RING:
                listObservableData = mAttachmentRequestModel.getRingFiles();
                break;
            case GroupMulticast:
                listObservableData = mAttachmentRequestModel.getAudioMulticastFiles();
                break;
        }
        if(listObservableData != null) {
            if(listObservableData.getT() != null) {
                mList.addAll(listObservableData.getT());
            }
            listObservableData.addObserver((observable, o) -> {
                UIThreadUtil.runOnUiThread(()->{
                    if(o instanceof  List) {
                        mList.addAll(JsonUtils.toObject(JsonUtils.toJsonString(o),List.class,Attachment.class));
                    }
                });
            });
        }
    }

    @Override
    public void loadMore() {
        loadData();
    }
}

