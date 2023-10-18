package com.hcs.commondemo.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.hcs.android.ui.mvvm.BaseFragment;
import com.hcs.commondemo.R;
import com.hcs.commondemo.databinding.NetconfigDemoBinding;
import com.hcs.commondemo.viewmodel.NetConfigViewModel;

public class NetConfigDemoFragment extends BaseFragment {


    private NetconfigDemoBinding mBinding;

    private NetConfigViewModel mViewModel;
    

    public static NetConfigDemoFragment newInstance() {
        NetConfigDemoFragment netConfigDemoFragment = new NetConfigDemoFragment();
        Bundle args = new Bundle();
        netConfigDemoFragment.setArguments(args);
        return netConfigDemoFragment;
    }

    @Override
    public void initContentView(ViewGroup root){
        mBinding =  DataBindingUtil.inflate(LayoutInflater.from(mActivity), onBindLayout(),root,true);

        mViewModel = new NetConfigViewModel(getActivity());
        mBinding.setNetconfig(mViewModel.getNetConfigBo());

        /**
         * 保存配置
         */
        mBinding.btnSet.setOnClickListener(v->{
            mViewModel.saveConfig();
        });
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.netconfig_demo;
    }

    @Override
    public boolean enableLazyData() {
        return true;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {

    }

    @Override
    public String getToolbarTitle() {
        return null;
    }



}
