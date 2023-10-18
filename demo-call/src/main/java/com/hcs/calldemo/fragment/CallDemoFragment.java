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

import com.hcs.android.call.api.CallHelper;
import com.hcs.android.call.api.PhoneManager;
import com.hcs.android.call.fragment.CallVideoFragment;
import com.hcs.android.common.util.FileUtil;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.ToastUtil;
import com.hcs.android.ui.mvvm.BaseFragment;
import com.hcs.calldemo.R;
import com.hcs.calldemo.databinding.CallDemoBinding;
import com.hcs.calldemo.viewmodel.CallViewModel;
import org.linphone.core.Reason;

public class CallDemoFragment extends BaseFragment {


    private CallDemoBinding mBinding;

    private CallViewModel mViewModel;

    private CallVideoFragment mCallVideo;

    private ActivityResultLauncher activityResultLauncher;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityResultLauncher =  registerForActivityResult(new ActivityResultContracts.OpenDocument(), result->{
            mViewModel.getCallVo().setFilePath(FileUtil.getFilePath(getContext(),result));
        });

    }
    public static CallDemoFragment newInstance() {
        CallDemoFragment callDemoFragment = new CallDemoFragment();
        Bundle args = new Bundle();
        callDemoFragment.setArguments(args);
        return callDemoFragment;
    }

    @Override
    public void initContentView(ViewGroup root){
        mBinding =  DataBindingUtil.inflate(LayoutInflater.from(mActivity), onBindLayout(),root,true);

        mViewModel = new CallViewModel();
        mBinding.setCallVo(mViewModel.getCallVo());

        /**
         * 开始呼叫
         */
        mBinding.btnStartCall.setOnClickListener((view)->{
            if(StringUtil.isEmpty(mViewModel.getCallVo().getRemoteAddress())){
                ToastUtil.showToast(R.string.call_notice_address_can_not_empty);
                return;
            }
            CallHelper.startCall(mViewModel.getCallVo().getRemoteAddress(),mViewModel.getCallVo().getLocalName());
        });

        /**
         * 停止呼叫
         */
        mBinding.btnStopCall.setOnClickListener((view)->{
            CallHelper.stopCall(mViewModel.getCurrentCall());
        });

        /**
         * 暂停呼叫
         */
        mBinding.btnPauseCall.setOnClickListener(view -> {
            CallHelper.pauseCall(mViewModel.getCurrentCall());
            mBinding.btnResumeCall.setVisibility(View.VISIBLE);
            mBinding.btnPauseCall.setVisibility(View.GONE);
        });

        /**
         * 恢复呼叫
         */
        mBinding.btnResumeCall.setOnClickListener(view -> {
            CallHelper.resumeCall(mViewModel.getCurrentCall());
            mBinding.btnResumeCall.setVisibility(View.GONE);
            mBinding.btnPauseCall.setVisibility(View.VISIBLE);
        });

        /**
         * 接受呼叫
         */
        mBinding.btnAcceptCall.setOnClickListener(view -> {
            CallHelper.acceptCall(mViewModel.getCurrentCall());
        });

        /**
         * 拒绝呼叫
         */
        mBinding.btnRejectCall.setOnClickListener(view -> {
            CallHelper.declineCall(mViewModel.getCurrentCall(), Reason.Busy);
        });

        /**
         * 转为视频通话
         */
        mBinding.btnVideoCall.setOnClickListener(view -> {
            CallHelper.updateCallVideo(true);
            mBinding.btnVideoCall.setVisibility(View.GONE);
            mBinding.btnAudioCall.setVisibility(View.VISIBLE);
        });

        /**
         * 转为语音通话
         */
        mBinding.btnAudioCall.setOnClickListener(view -> {
            CallHelper.updateCallVideo(false);
            mBinding.btnVideoCall.setVisibility(View.VISIBLE);
            mBinding.btnAudioCall.setVisibility(View.GONE);
        });

        /**
         * 开始播放语音
         */
        mBinding.btnStartPlay.setOnClickListener(view -> {
            mViewModel.startPlay(null);
        });

        /**
         * 停止播放语音
         */
        mBinding.btnStopPlay.setOnClickListener(view -> {
            mViewModel.stopPlay();
        });

        /**
         * 选择文件
         */
        mBinding.btnChooseFile.setOnClickListener(view -> {
            String[] array = {"image/*","video/*","audio/*","text/plain"};
            activityResultLauncher.launch(array);
        });
        PhoneManager.getInstance().setActivityContext(getActivity());

        mCallVideo = new CallVideoFragment();
        mActivity.getSupportFragmentManager().beginTransaction().replace(R.id.frgVideo, mCallVideo).commit();
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.call_demo;
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
