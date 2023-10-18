package com.hcs.commondemo.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.commondemo.fragment.LogDemoFragment;
import com.hcs.commondemo.fragment.TTSDemoFragment;

@Route(path = "/menu/tts",name = "TTS测试")
public class TTSDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return TTSDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
