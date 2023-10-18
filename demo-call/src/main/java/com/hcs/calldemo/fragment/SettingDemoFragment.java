package com.hcs.calldemo.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.hcs.android.call.api.CallHelper;
import com.hcs.android.call.api.PhoneManager;
import com.hcs.android.call.fragment.CallVideoFragment;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.ToastUtil;
import com.hcs.android.ui.mvvm.BaseFragment;
import com.hcs.calldemo.R;
import com.hcs.calldemo.databinding.SettingDemoBinding;
import com.hcs.calldemo.viewmodel.CallViewModel;
import com.hcs.calldemo.viewmodel.SettingViewModel;

import org.linphone.core.Reason;

public class SettingDemoFragment extends BaseFragment {


    private SettingDemoBinding mBinding;

    private SettingViewModel mViewModel;


    public static SettingDemoFragment newInstance() {
        SettingDemoFragment settingDemoFragment = new SettingDemoFragment();
        Bundle args = new Bundle();
        settingDemoFragment.setArguments(args);
        return settingDemoFragment;
    }

    @Override
    public void initContentView(ViewGroup root){
        mBinding =  DataBindingUtil.inflate(LayoutInflater.from(mActivity), onBindLayout(),root,true);

        mViewModel = new SettingViewModel();
        mBinding.setPhoneSetting(mViewModel.getPhoneSettingBo());

        mBinding.btnSetUserInfo.setOnClickListener(v->{
            mViewModel.saveUserInfo();
        });

        mBinding.btnSetAutoAnswer.setOnClickListener(v->{
            mViewModel.saveAutoAnswerSet();
        });

        mBinding.btnSetVideoEnable.setOnClickListener(v->{
            mViewModel.saveVideo();
        });

        mBinding.btnSetUseIPv6.setOnClickListener(v->{
            mViewModel.saveUseIPv6();
        });
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.setting_demo;
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
