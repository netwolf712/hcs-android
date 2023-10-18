package com.hcs.commondemo.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.commondemo.fragment.VolumeDemoFragment;

@Route(path = "/menu/volume",name = "音量设置测试")
public class VolumeDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return VolumeDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
