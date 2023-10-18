package com.hcs.commondemo.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.commondemo.fragment.CameraDemoFragment;
import com.hcs.commondemo.fragment.LogDemoFragment;

@Route(path = "/menu/camera",name = "摄像头测试")
public class CameraDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return CameraDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
