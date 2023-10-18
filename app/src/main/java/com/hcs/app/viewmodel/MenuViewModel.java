package com.hcs.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;
import com.hcs.app.entity.Menu;


public class MenuViewModel extends BaseRefreshViewModel<Menu, CommonViewModel> {
    /**
     * 菜单名称
     */
    private final static String[] menuArray = {"基本信息","呼叫测试","bed管理","呼叫日志","附件管理","录音/录像管理","广播","基础设置","呼叫设置","Onvif摄像头测试"};

    /**
     * 菜单路由
     */
    private final static String[] menuRouterArray = {"/menu/baseInfo","/menu/callTest","/menu/friend","/menu/callLog","/menu/attachment","/menu/record","/menu/multicast","/menu/baseSet","/menu/callSet","/menu/onvifTest"};

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

