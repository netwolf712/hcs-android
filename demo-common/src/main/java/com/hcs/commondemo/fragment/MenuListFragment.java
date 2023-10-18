package com.hcs.commondemo.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hcs.android.common.util.log.KLog;
import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.android.ui.mvvm.BaseMvvmFragment;
import com.hcs.android.ui.util.ObservableListUtil;
import com.hcs.commondemo.BR;
import com.hcs.commondemo.R;
import com.hcs.commondemo.adapter.MenuAdapter;
import com.hcs.commondemo.databinding.MenuListBinding;
import com.hcs.commondemo.entity.Menu;
import com.hcs.commondemo.factory.ViewModelFactory;
import com.hcs.commondemo.viewmodel.MenuViewModel;

public class MenuListFragment extends BaseMvvmFragment<MenuListBinding, MenuViewModel> {
    private MenuAdapter mMenuAdapter;

    /**
     * 菜单监听器
     */
    private IMenuListener mMenuListener;

    public static MenuListFragment newInstance() {
        MenuListFragment friendListFragment = new MenuListFragment();
        Bundle args = new Bundle();
        friendListFragment.setArguments(args);
        return friendListFragment;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.menu_list;
    }

    @Override
    public boolean enableLazyData() {
        return true;
    }

    @Override
    public void initView(View view) {
        mMenuAdapter = new MenuAdapter(mActivity, mViewModel.getList());
        mMenuAdapter.setItemClickListener(new BaseBindAdapter.OnItemClickListener<Menu>() {
            @Override
            public void onItemClick(Menu menu, int position) {
                KLog.i("clicked menu " + menu.getName() + ", router " + menu.getRouter());
                if(mMenuListener != null){
                    mMenuListener.onMenuSelected(menu.getRouter(),menu.getName());
                }
            }
        });
        mViewModel.getList().addOnListChangedCallback(ObservableListUtil.getListChangedCallback(mMenuAdapter));
        mBinding.recview.setAdapter(mMenuAdapter);
        mBinding.recview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mViewModel.refreshData();
    }

    @Override
    public void initData() {
        mViewModel.refreshData();
    }

    @Override
    public void initListener() {

    }

    @Override
    public String getToolbarTitle() {
        return null;
    }


    @Override
    public Class<MenuViewModel> onBindViewModel() {
        return MenuViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mActivity.getApplication());
    }

    @Override
    public void initViewObservable() {

    }

    @Override
    public int onBindVariableId() {
        return BR.viewModel;
    }

    public void setMenuListener(IMenuListener menuListener){
        this.mMenuListener = menuListener;
    }

    /**
     * 菜单改变监听器
     */
    public interface IMenuListener{
        /**
         * 菜单选中
         * @param path 跳转路径
         * @param name 菜单名称
         */
        void onMenuSelected(String path,String name);
    }

}
