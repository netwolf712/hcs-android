package com.hcs.commondemo.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.commondemo.fragment.NetConfigDemoFragment;

@Route(path = "/menu/netConfig",name = "IP设置")
public class NetConfigDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return NetConfigDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
