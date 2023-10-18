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
import com.hcs.commondemo.adapter.MessageAdapter;
import com.hcs.commondemo.databinding.MulticastMessageDemoBinding;
import com.hcs.commondemo.entity.MulticastMessageBo;
import com.hcs.commondemo.factory.ViewModelFactory;
import com.hcs.commondemo.viewmodel.MulticastViewModel;

public class MulticastDemoFragment extends BaseMvvmFragment<MulticastMessageDemoBinding, MulticastViewModel> {
    private MessageAdapter mMessageAdapter;

    public static MulticastDemoFragment newInstance() {
        MulticastDemoFragment multicastMessageDemoFragment = new MulticastDemoFragment();
        Bundle args = new Bundle();
        multicastMessageDemoFragment.setArguments(args);

        return multicastMessageDemoFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.multicast_message_demo;
    }

    @Override
    public boolean enableLazyData() {
        return true;
    }

    @Override
    public void initView(View view) {
        mViewModel.start(getActivity());
        mMessageAdapter = new MessageAdapter(mActivity, mViewModel.getList());
        mMessageAdapter.setItemClickListener(new BaseBindAdapter.OnItemClickListener<MulticastMessageBo>() {
            @Override
            public void onItemClick(MulticastMessageBo multicastMessageBo, int position) {
                KLog.i("clicked message " + multicastMessageBo.getMessage());
            }
        });
        mViewModel.getList().addOnListChangedCallback(ObservableListUtil.getListChangedCallback(mMessageAdapter));
        mBinding.recview.setAdapter(mMessageAdapter);
        mViewModel.refreshData();
        mBinding.recview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mBinding.btnSendMessage.setOnClickListener(v->{
            mViewModel.setMessage();
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
    public Class<MulticastViewModel> onBindViewModel() {
        return MulticastViewModel.class;
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
