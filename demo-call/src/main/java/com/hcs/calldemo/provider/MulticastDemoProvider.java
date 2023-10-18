package com.hcs.calldemo.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.calldemo.fragment.MulticastDemoFragment;

@Route(path = "/menu/multicast",name = "广播")
public class MulticastDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return MulticastDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
