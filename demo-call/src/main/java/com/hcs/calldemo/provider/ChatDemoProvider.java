package com.hcs.calldemo.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.calldemo.fragment.ChatDemoFragment;

@Route(path = "/menu/chat",name = "消息发送")
public class ChatDemoProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return ChatDemoFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
