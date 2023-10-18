package com.hcs.commondemo.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.commondemo.fragment.MulticastDemoFragment;
import com.hcs.commondemo.fragment.RecordDemoFragment;

@Route(path = "/menu/msgMulticast",name = "消息广播")
public class MulticastDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return MulticastDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
