package com.hcs.commondemo.viewmodel;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.FileUtil;
import com.hcs.android.common.util.MediaScanner;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;
import com.hcs.android.ui.util.UIThreadUtil;

import com.hcs.commondemo.config.Config;
import com.hcs.commondemo.entity.RecordBo;
import com.hcs.commondemo.factory.CommonViewModel;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecordLogViewModel extends BaseRefreshViewModel<RecordBo, CommonViewModel> {

    private Activity mActivity;
    public Activity getActivity(){
        return mActivity;
    }
    public void setActivity(Activity activity){
        this.mActivity = activity;
    }


    public RecordLogViewModel(@NonNull Application application, CommonViewModel model) {
        super(application, model);
    }


    @Override
    public void refreshData() {
        postShowNoDataViewEvent(true);

        postShowNoDataViewEvent(false);
        scanFile();
        postStopRefreshEvent();
    }

    @Override
    public void loadMore() {
        refreshData();
    }

    public void scanFile(){
        if(mActivity != null) {
            mList.clear();
            List<File> fileList = FileUtil.getFileListByDirPath(Config.getRecordPath(BaseApplication.getAppContext()),FileUtil.getVideoFileFilter());
            //List<File> fileList = FileUtil.getFileListByDirPath("/mnt/media_rw/E461-607E/ ",FileUtil.getVideoFileFilter());
            if(!StringUtil.isEmpty(fileList)){
                for(File file : fileList){
                    RecordBo recordBo = new RecordBo();
                    recordBo.analyzeFile(file.getAbsolutePath());
                    mList.add(recordBo);
                }
            }
        }
    }

}
