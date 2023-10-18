package com.hcs.app.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hcs.android.business.constant.AttachmentUseEnum;
import com.hcs.android.business.entity.OperationLogModel;
import com.hcs.android.business.request.viewmodel.AttachmentViewModel;
import com.hcs.android.business.request.viewmodel.ViewModelFactory;
import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.android.ui.mvvm.BaseMvvmFragment;
import com.hcs.android.ui.util.ObservableListUtil;
import com.hcs.app.BR;
import com.hcs.app.R;
import com.hcs.app.adapter.AttachmentAdapter;
import com.hcs.app.databinding.AttachmentDemoBinding;

public class AttachmentDemoFragment extends BaseMvvmFragment<AttachmentDemoBinding, AttachmentViewModel> {
    private AttachmentAdapter mAttachmentAdapter;

    public static AttachmentDemoFragment newInstance() {
        AttachmentDemoFragment attachmentDemoFragment = new AttachmentDemoFragment();
        Bundle args = new Bundle();
        attachmentDemoFragment.setArguments(args);

        return attachmentDemoFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.attachment_demo;
    }

    @Override
    public boolean enableLazyData() {
        return true;
    }

    @Override
    public void initView(View view) {
        mAttachmentAdapter = new AttachmentAdapter(mActivity, mViewModel.getList());
        mAttachmentAdapter.setItemClickListener(new BaseBindAdapter.OnItemClickListener<OperationLogModel>() {
            @Override
            public void onItemClick(OperationLogModel  callLogBo, int position) {
            }
        });
        mViewModel.getList().addOnListChangedCallback(ObservableListUtil.getListChangedCallback(mAttachmentAdapter));
        mBinding.recview.setAdapter(mAttachmentAdapter);
        mViewModel.refreshData();
        mBinding.recview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mBinding.btnAudioMulticast.setOnClickListener(view1 -> {
            mViewModel.setUseEnum(AttachmentUseEnum.GroupMulticast);
            mViewModel.refreshData();
        });

        mBinding.btnRing.setOnClickListener(view1 -> {
            mViewModel.setUseEnum(AttachmentUseEnum.RING);
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
    public Class<AttachmentViewModel> onBindViewModel() {
        return AttachmentViewModel.class;
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
