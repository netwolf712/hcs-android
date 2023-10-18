package com.hcs.commondemo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;
import com.hcs.commondemo.entity.Menu;
import com.hcs.commondemo.factory.CommonViewModel;

import java.util.List;

public class MenuViewModel extends BaseRefreshViewModel<Menu, CommonViewModel> {
    /**
     * 菜单名称
     */
    private final static String[] menuArray = {"音视频录制","视频列表","TTS测试","音量设置测试","摄像头测试","网络摄像头测试","网络摄像头云台测试","IP设置","日志管理"};

    /**
     * 菜单路由
     */
    private final static String[] menuRouterArray = {"/menu/record","/menu/recordLog","/menu/tts","/menu/volume","/menu/camera","/menu/ipcamera","/menu/ipcamera2","/menu/netConfig","/menu/log"};

    public MenuViewModel(@NonNull Application application, CommonViewModel model) {
        super(application, model);
    }

    public void refreshData() {
        postShowInitLoadViewEvent(true);
        mList.clear();
        for(int i = 0; i < menuArray.length; i++){
            String strMenu = menuArray[i];
            String strRouter = menuRouterArray[i];
            Menu menu = new Menu();
            menu.setName(strMenu);
            menu.setRouter(strRouter);
            mList.add(menu);
        }
        postShowInitLoadViewEvent(false);
    }

    @Override
    public void loadMore() {
        refreshData();
    }
}

