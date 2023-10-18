package com.hcs.commondemo.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.commondemo.fragment.RecordLogDemoFragment;

@Route(path = "/menu/recordLog",name = "音视频列表")
public class RecordLogDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return RecordLogDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
