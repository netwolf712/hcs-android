package com.hcs.commondemo.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.commondemo.fragment.RecordDemoFragment;

@Route(path = "/menu/record",name = "音视频录制")
public class RecordDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return RecordDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
