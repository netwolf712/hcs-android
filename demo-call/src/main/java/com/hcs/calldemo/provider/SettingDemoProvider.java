package com.hcs.calldemo.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.calldemo.fragment.SettingDemoFragment;

@Route(path = "/menu/callSet",name = "设置")
public class SettingDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return SettingDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
