package com.hcs.app.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.hcs.android.business.request.viewmodel.BaseInfoViewModel;
import com.hcs.android.common.util.ExeCommand;
import com.hcs.android.common.util.ToastUtil;
import com.hcs.app.databinding.BaseInfoDemoBinding;
import com.hcs.android.ui.mvvm.BaseFragment;
import com.hcs.app.R;


public class BaseInfoDemoFragment extends BaseFragment {


    private BaseInfoDemoBinding mBinding;

    private BaseInfoViewModel mViewModel;

    public static BaseInfoDemoFragment newInstance() {
        BaseInfoDemoFragment baseInfoFragment = new BaseInfoDemoFragment();
        Bundle args = new Bundle();
        baseInfoFragment.setArguments(args);
        return baseInfoFragment;
    }

    @Override
    public void initContentView(ViewGroup root){
        mBinding =  DataBindingUtil.inflate(LayoutInflater.from(mActivity), onBindLayout(),root,true);

        mViewModel = new BaseInfoViewModel();
        mBinding.setDeviceInfo(mViewModel.getDeviceModel());

        mBinding.btnParentNO.setOnClickListener(view -> {
            mViewModel.getDeviceModel().getDevice().setPhoneNo(mBinding.etPhoneNO.getText().toString());
            mViewModel.getDeviceModel().getDevice().setParentNo(mBinding.etParentNO.getText().toString());
            mViewModel.getDeviceModel().setServiceAddress("xxx");
            mViewModel.changeBaseInfo();
        });

        mBinding.btnCheckCode.setOnClickListener(view -> {
            String code = mBinding.etCheckCode.getText().toString();
            if(mViewModel.checkCode(code)){
                ToastUtil.showToast(R.string.check_success);
            }else{
                ToastUtil.showToast(R.string.check_failed);
            }
        });

        //检查是否具有root权限
//        if(ExeCommand.isRoot()){
//            mBinding.etIsRoot.setText(R.string.is_root);
//        }else{
//            mBinding.etIsRoot.setText(R.string.is_not_root);
//        }
        mBinding.etIsRoot.setText(ExeCommand.executeCmd("id"));
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.base_info_demo;
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
