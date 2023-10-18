package com.hcs.calldemo;

import androidx.fragment.app.Fragment;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.ui.mvvm.BaseActivity;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.android.ui.util.KeyboardUtil;
import com.hcs.calldemo.fragment.FriendListFragment;
import com.hcs.calldemo.fragment.MenuListFragment;

public class MainActivity extends BaseActivity {

    private Fragment mFriendListFragment;

    private Fragment mMenuListFragment;
    @Override
    public int onBindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

        if(mFriendListFragment == null){
            mFriendListFragment = FriendListFragment.newInstance();
        }
        if(mMenuListFragment == null){
            mMenuListFragment = MenuListFragment.newInstance();
        }
        if(mFriendListFragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mFriendListFragment, "好友").commit();
        }

        if(mMenuListFragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.menu_content, mMenuListFragment, "菜单").commit();
            ((MenuListFragment)mMenuListFragment).setMenuListener((path,name)->{
                try {
                    IFragmetProvider fragment =  (IFragmetProvider)ARouter.getInstance().build(path).navigation();
                    if(fragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment.getFragment(), name).commit();
                    }
                    KeyboardUtil.hideKeyboard(this);
                }catch (Exception e){
                    KLog.e(e);
                }
            });
        }
    }

    @Override
    public void initData() {

    }
}