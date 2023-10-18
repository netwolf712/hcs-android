package com.hcs.app.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.app.fragment.BaseInfoDemoFragment;

@Route(path = "/menu/baseInfo",name = "基本信息")
public class BaseInfoDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return BaseInfoDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
