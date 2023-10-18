package com.hcs.commondemo.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.hcs.android.ui.mvvm.BaseFragment;
import com.hcs.commondemo.R;
import com.hcs.commondemo.databinding.LogDemoBinding;
import com.hcs.commondemo.viewmodel.LogViewModel;

public class LogDemoFragment extends BaseFragment {


    private LogDemoBinding mBinding;

    public static LogDemoFragment newInstance() {
        LogDemoFragment logDemoFragment = new LogDemoFragment();
        Bundle args = new Bundle();
        logDemoFragment.setArguments(args);
        return logDemoFragment;
    }

    @Override
    public void initContentView(ViewGroup root){

        mBinding =  DataBindingUtil.inflate(LayoutInflater.from(mActivity), onBindLayout(),root,true);

        mBinding.setLog(LogViewModel.getInstance().getLogBo());

        mBinding.swEnableDmesg.setOnCheckedChangeListener((btn,changed)->{
            if(changed){
                LogViewModel.getInstance().startLog(LogViewModel.LOG_TYPE_DMESG);
            }else{
                LogViewModel.getInstance().stopLog(LogViewModel.LOG_TYPE_DMESG);
            }
        });

        mBinding.swEnableLogcat.setOnCheckedChangeListener((btn,changed)->{
            if(changed){
                LogViewModel.getInstance().startLog(LogViewModel.LOG_TYPE_LOGCAT);
            }else{
                LogViewModel.getInstance().stopLog(LogViewModel.LOG_TYPE_LOGCAT);
            }
        });

        mBinding.swEnablePcap.setOnCheckedChangeListener((btn,changed)->{
            if(changed){
                LogViewModel.getInstance().startLog(LogViewModel.LOG_TYPE_PCAP);
            }else{
                LogViewModel.getInstance().stopLog(LogViewModel.LOG_TYPE_PCAP);
            }
        });
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.log_demo;
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
