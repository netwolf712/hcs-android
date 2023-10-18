package com.hcs.calldemo.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.android.ui.mvvm.BaseMvvmFragment;
import com.hcs.android.ui.util.ObservableListUtil;
import com.hcs.android.ui.view.SingleCloseDialog;
import com.hcs.android.ui.view.VideoViewFragment;
import com.hcs.calldemo.BR;
import com.hcs.calldemo.R;
import com.hcs.calldemo.adapter.CallLogAdapter;
import com.hcs.calldemo.databinding.CallLogDemoBinding;
import com.hcs.calldemo.entity.CallLogBo;
import com.hcs.calldemo.factory.ViewModelFactory;
import com.hcs.calldemo.viewmodel.CallLogViewModel;

public class CallLogDemoFragment extends BaseMvvmFragment<CallLogDemoBinding, CallLogViewModel> {
    private CallLogAdapter mCallLogAdapter;

    public static CallLogDemoFragment newInstance() {
        CallLogDemoFragment callLogDemoFragment = new CallLogDemoFragment();
        Bundle args = new Bundle();
        callLogDemoFragment.setArguments(args);

        return callLogDemoFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.call_log_demo;
    }

    @Override
    public boolean enableLazyData() {
        return true;
    }

    @Override
    public void initView(View view) {
        mCallLogAdapter = new CallLogAdapter(mActivity, mViewModel.getList());
        mCallLogAdapter.setItemClickListener(new BaseBindAdapter.OnItemClickListener<CallLogBo>() {
            @Override
            public void onItemClick(CallLogBo  callLogBo, int position) {
                KLog.i("clicked id " + callLogBo.getCallId());
                ShowVideoDialog(callLogBo);
            }
        });
        mViewModel.getList().addOnListChangedCallback(ObservableListUtil.getListChangedCallback(mCallLogAdapter));
        mBinding.recview.setAdapter(mCallLogAdapter);
        mViewModel.refreshData();
        mBinding.recview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mBinding.btnRefreshCallLog.setOnClickListener(v->{
            mViewModel.refreshData();
        });
    }
    public String comboCallTitle(CallLogBo callLogBo){
        return String.format("%s %s %s",callLogBo.getDuration(),callLogBo.getRemoteAddress(),callLogBo.getCallDir());
    }

    private void ShowVideoDialog(CallLogBo callLogBo){
        if(StringUtil.isEmpty(callLogBo.getCallRef())){
            return;
        }

        VideoViewFragment videoViewFragment = new VideoViewFragment();
        Bundle args = new Bundle();
        args.putString("title", comboCallTitle(callLogBo));
        args.putString("filePath", callLogBo.getCallRef());
        videoViewFragment.setArguments(args);
        SingleCloseDialog singleCloseDialog = new SingleCloseDialog(videoViewFragment);
        singleCloseDialog.show(mActivity.getSupportFragmentManager(), "");


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
    public Class<CallLogViewModel> onBindViewModel() {
        return CallLogViewModel.class;
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

}
