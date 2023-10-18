package com.hcs.commondemo;

import androidx.fragment.app.Fragment;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.ui.mvvm.BaseActivity;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.android.ui.util.KeyboardUtil;
import com.hcs.commondemo.fragment.MenuListFragment;
import com.hcs.commondemo.fragment.RecordDemoFragment;

public class MainActivity extends BaseActivity {

    private Fragment mFirstFragment;

    private Fragment mMenuListFragment;
    @Override
    public int onBindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

        if(mFirstFragment == null){
            mFirstFragment = RecordDemoFragment.newInstance();
        }
        if(mMenuListFragment == null){
            mMenuListFragment = MenuListFragment.newInstance();
        }
        if(mFirstFragment != null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, mFirstFragment, "").commit();
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