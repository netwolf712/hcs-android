package com.hcs.calldemo.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.calldemo.fragment.CallDemoFragment;

@Route(path = "/menu/callTest",name = "呼叫测试")
public class CallDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return CallDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
