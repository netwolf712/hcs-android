package com.hcs.app.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hcs.android.business.entity.OperationLogModel;
import com.hcs.android.business.request.viewmodel.CallLogViewModel;
import com.hcs.android.business.request.viewmodel.ViewModelFactory;
import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.android.ui.mvvm.BaseMvvmFragment;
import com.hcs.android.ui.util.ObservableListUtil;
import com.hcs.app.BR;
import com.hcs.app.R;
import com.hcs.app.adapter.CallLogAdapter;
import com.hcs.app.databinding.CallLogDemoBinding;

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
        mCallLogAdapter.setItemClickListener(new BaseBindAdapter.OnItemClickListener<OperationLogModel>() {
            @Override
            public void onItemClick(OperationLogModel  callLogBo, int position) {
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
