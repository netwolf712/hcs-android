package com.hcs.commondemo.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.commondemo.fragment.IPCameraDemoFragment2;

@Route(path = "/menu/ipcamera2",name = "网络摄像头测试2")
public class IPCameraDemoProvider2 implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return IPCameraDemoFragment2.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
