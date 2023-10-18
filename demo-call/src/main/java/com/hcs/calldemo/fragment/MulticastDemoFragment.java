package com.hcs.calldemo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.hcs.android.call.api.LinphonePreferences;
import com.hcs.android.common.util.FileUtil;
import com.hcs.android.ui.mvvm.BaseFragment;
import com.hcs.calldemo.R;
import com.hcs.calldemo.databinding.MulticastDemoBinding;
import com.hcs.calldemo.viewmodel.MulticastViewModel;

public class MulticastDemoFragment extends BaseFragment {


    private MulticastDemoBinding mBinding;

    private MulticastViewModel mViewModel;

    private ActivityResultLauncher activityResultLauncher;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinphonePreferences.instance().setTurnEnabled(false);
        LinphonePreferences.instance().setIceEnabled(false);
        activityResultLauncher =  registerForActivityResult(new ActivityResultContracts.OpenDocument(), result->{
            mViewModel.getMulticastBo().setFilePath(FileUtil.getFilePath(getContext(),result));
        });

    }
    public static MulticastDemoFragment newInstance() {
        MulticastDemoFragment multicastcallDemoFragment = new MulticastDemoFragment();
        Bundle args = new Bundle();
        multicastcallDemoFragment.setArguments(args);
        return multicastcallDemoFragment;
    }

    @Override
    public void initContentView(ViewGroup root){
        mBinding =  DataBindingUtil.inflate(LayoutInflater.from(mActivity), onBindLayout(),root,true);

        mViewModel = new MulticastViewModel();
        mBinding.setMulticastBo(mViewModel.getMulticastBo());

        /**
         * 开始播放
         */
        mBinding.btnStartPlay.setOnClickListener(v->{
            mViewModel.startPlay(mBinding.frgVideo);
        });

        /**
         * 停止播放
         */
        mBinding.btnStopPlay.setOnClickListener(v->{
            mViewModel.stopPlay();
        });

        /**
         * 开始广播
         */
        mBinding.btnStartMulticast.setOnClickListener((view)->{
            mViewModel.startMulticast();
        });

        /**
         * 停止广播
         */
        mBinding.btnStopMulticast.setOnClickListener((view)->{
            mViewModel.stopMulticast();
        });

        /**
         * 选择文件
         */
        mBinding.btnChooseFile.setOnClickListener(view -> {
            String[] array = {"image/png", "image/jpeg", "image/gif", "video/mp4"};
//            Intent intent = new Intent();
//            intent.putExtra(
//                    Intent.EXTRA_MIME_TYPES,
//                    array
//            );
//            intent.setAction(Intent.ACTION_GET_CONTENT);
            activityResultLauncher.launch(array);
        });
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.multicast_demo;
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
