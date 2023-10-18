package com.hcs.app.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.app.fragment.FriendListFragment;

@Route(path = "/menu/friend",name = "好友管理")
public class FriendProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return FriendListFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
