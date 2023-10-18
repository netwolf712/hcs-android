package com.hcs.app.provider;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.app.fragment.FriendListFragment;
import com.hcs.app.fragment.RoomListFragment;

@Route(path = "/menu/room",name = "房间管理")
public class RoomProvider implements IFragmetProvider {
    @Override
    public Fragment getFragment() {
        return RoomListFragment.newInstance();
    }

    @Override
    public void init(Context context) {

    }
}
