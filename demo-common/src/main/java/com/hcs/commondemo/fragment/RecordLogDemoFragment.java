package com.hcs.commondemo.fragment;


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
import com.hcs.commondemo.BR;
import com.hcs.commondemo.R;
import com.hcs.commondemo.adapter.RecordLogAdapter;
import com.hcs.commondemo.databinding.RecordLogDemoBinding;
import com.hcs.commondemo.entity.RecordBo;
import com.hcs.commondemo.factory.ViewModelFactory;
import com.hcs.commondemo.viewmodel.RecordLogViewModel;

public class RecordLogDemoFragment extends BaseMvvmFragment<RecordLogDemoBinding, RecordLogViewModel> {
    private RecordLogAdapter mRecordLogAdapter;

    public static RecordLogDemoFragment newInstance() {
        RecordLogDemoFragment recordLogDemoFragment = new RecordLogDemoFragment();
        Bundle args = new Bundle();
        recordLogDemoFragment.setArguments(args);

        return recordLogDemoFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.record_log_demo;
    }

    @Override
    public boolean enableLazyData() {
        return true;
    }

    @Override
    public void initView(View view) {
        mRecordLogAdapter = new RecordLogAdapter(mActivity, mViewModel.getList());
        mRecordLogAdapter.setItemClickListener(new BaseBindAdapter.OnItemClickListener<RecordBo>() {
            @Override
            public void onItemClick(RecordBo RecordBo, int position) {
                KLog.i("clicked file " + RecordBo.getFilePath());
                ShowVideoDialog(RecordBo);
            }
        });
        mViewModel.getList().addOnListChangedCallback(ObservableListUtil.getListChangedCallback(mRecordLogAdapter));
        mBinding.recview.setAdapter(mRecordLogAdapter);
        mViewModel.setActivity(getActivity());
        mViewModel.refreshData();
        mBinding.recview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        mBinding.btnRefreshCallLog.setOnClickListener(v->{

        });
    }
    public String comboCallTitle(RecordBo RecordBo){
        return String.format("%s",RecordBo.getName());
    }

    private void ShowVideoDialog(RecordBo RecordBo){
        if(StringUtil.isEmpty(RecordBo.getFilePath())){
            return;
        }
        VideoViewFragment videoViewFragment = new VideoViewFragment();
        Bundle args = new Bundle();
        args.putString("title", comboCallTitle(RecordBo));
        args.putString("filePath", RecordBo.getFilePath());
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
    public Class<RecordLogViewModel> onBindViewModel() {
        return RecordLogViewModel.class;
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
