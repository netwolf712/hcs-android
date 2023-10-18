package com.hcs.calldemo.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hcs.android.common.util.log.KLog;
import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.android.ui.mvvm.BaseMvvmFragment;
import com.hcs.android.ui.util.ObservableListUtil;
import com.hcs.calldemo.BR;
import com.hcs.calldemo.R;
import com.hcs.calldemo.adapter.MessageAdapter;
import com.hcs.calldemo.databinding.ChatDemoBinding;
import com.hcs.calldemo.entity.ChatMessageBo;
import com.hcs.calldemo.factory.ViewModelFactory;
import com.hcs.calldemo.viewmodel.ChatViewModel;

public class ChatDemoFragment extends BaseMvvmFragment<ChatDemoBinding, ChatViewModel> {
    private MessageAdapter mMessageAdapter;

    public static ChatDemoFragment newInstance() {
        ChatDemoFragment chatDemoFragment = new ChatDemoFragment();
        Bundle args = new Bundle();
        chatDemoFragment.setArguments(args);

        return chatDemoFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.chat_demo;
    }

    @Override
    public boolean enableLazyData() {
        return true;
    }

    @Override
    public void initView(View view) {
        mMessageAdapter = new MessageAdapter(mActivity, mViewModel.getList());
        mMessageAdapter.setItemClickListener(new BaseBindAdapter.OnItemClickListener<ChatMessageBo>() {
            @Override
            public void onItemClick(ChatMessageBo chatMessageBo, int position) {
                KLog.i("clicked message " + chatMessageBo.getMessage());
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
    public Class<ChatViewModel> onBindViewModel() {
        return ChatViewModel.class;
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
