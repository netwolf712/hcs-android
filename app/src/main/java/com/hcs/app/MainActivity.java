package com.hcs.app;

import androidx.fragment.app.Fragment;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hcs.android.business.constant.InitStateEnum;
import com.hcs.android.business.manager.WorkManager;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.maintain.constant.PreferenceConstant;
import com.hcs.android.ui.mvvm.BaseActivity;
import com.hcs.android.ui.provider.IFragmetProvider;
import com.hcs.android.ui.util.UIThreadUtil;
import com.hcs.app.fragment.MenuListFragment;

public class MainActivity extends BaseActivity implements WorkManager.IInitListener {

    private final static String PRODUCT_NAME = "HCS Android 测试工具";

    private boolean mInitOk = false;
    private Fragment mMenuListFragment;
    @Override
    public int onBindLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {

        SettingsHelper.getInstance(this).putData(PreferenceConstant.PREF_KEY_PRODUCT_NAME,PRODUCT_NAME);
        WorkManager.getInstance().preCheckAllPermission(this);
        WorkManager.getInstance().setInitListener(this);
        if(WorkManager.getInstance().canStart()){
            WorkManager.getInstance().start();
        }else{
            loadFragments();
        }
//        //初始化完成后再做整体显示
//        if(WorkManager.getInstance().getInitStateEnum() == InitStateEnum.INITIALIZED){
//            loadFragments();
//        }else if(WorkManager.getInstance().getInitStateEnum() == InitStateEnum.NONE
//        || WorkManager.getInstance().getInitStateEnum() == InitStateEnum.INITIALIZING){
//            WorkManager.getInstance().setInitListener(()->{
//                UIThreadUtil.runOnUiThread(()->{
//                    loadFragments();
//                });
//            });
//        }

    }

    private void loadFragments(){
        if(mMenuListFragment == null){
            mMenuListFragment = MenuListFragment.newInstance();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.menu_content, mMenuListFragment, "菜单").commit();
        ((MenuListFragment)mMenuListFragment).setMenuListener((path,name)->{
            try {
                IFragmetProvider fragment =  (IFragmetProvider)ARouter.getInstance().build(path).navigation();
                if(fragment != null) {
                    if(!mInitOk){
                        //在没初始化好之前（基本信息页面的本机号码需设置）不允许使用其它页面
                        if(!path.equals("/menu/baseInfo")){
                            return;
                        }
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, fragment.getFragment(), name).commit();
                }
            }catch (Exception e){
                KLog.e(e);
            }
        });
    }
    @Override
    public void initData() {

    }

    /**
     * 初始化完成
     */
    public void onInitOK(){
        mInitOk = true;
        UIThreadUtil.runOnUiThread(this::loadFragments);
    }

    /**
     * 初始化失败
     */
    public void onInitFailed(){

    }
}