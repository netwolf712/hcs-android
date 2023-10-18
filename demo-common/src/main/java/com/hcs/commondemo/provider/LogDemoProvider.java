package com.hcs.commondemo.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.commondemo.fragment.LogDemoFragment;

@Route(path = "/menu/log",name = "日志管理")
public class LogDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return LogDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
