package com.hcs.app.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.app.fragment.CallLogDemoFragment;


@Route(path = "/menu/callLog",name = "呼叫日志")
public class CallLogDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return CallLogDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
