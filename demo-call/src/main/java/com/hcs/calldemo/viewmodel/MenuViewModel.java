package com.hcs.calldemo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;

import com.hcs.android.ui.mvvm.viewmodel.BaseRefreshViewModel;
import com.hcs.calldemo.entity.Menu;
import com.hcs.calldemo.factory.CommonViewModel;

import java.util.List;

public class MenuViewModel extends BaseRefreshViewModel<Menu, CommonViewModel> {
    /**
     * 菜单名称
     */
    private final static String[] menuArray = {"呼叫测试","好友管理","消息发送","呼叫日志","广播","设置"};

    /**
     * 菜单路由
     */
    private final static String[] menuRouterArray = {"/menu/callTest","/menu/friend","/menu/chat","/menu/callLog","/menu/multicast","/menu/callSet"};

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

