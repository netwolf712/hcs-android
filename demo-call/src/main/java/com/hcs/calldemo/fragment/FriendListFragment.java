package com.hcs.calldemo.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.View;


import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.hcs.android.common.util.log.KLog;
import com.hcs.android.ui.util.KeyboardUtil;
import com.hcs.android.ui.util.UIThreadUtil;
import com.hcs.calldemo.BR;
import com.hcs.calldemo.R;
import com.hcs.calldemo.adapter.FriendAdapter;
import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.android.ui.mvvm.BaseMvvmFragment;
import com.hcs.android.ui.util.ObservableListUtil;
import com.hcs.calldemo.databinding.FriendListBinding;
import com.hcs.calldemo.entity.FriendBo;
import com.hcs.calldemo.factory.ViewModelFactory;
import com.hcs.calldemo.viewmodel.FriendViewModel;

public class FriendListFragment extends BaseMvvmFragment<FriendListBinding, FriendViewModel> {
    private FriendAdapter mFriendAdapter;

    public static FriendListFragment newInstance() {
        FriendListFragment friendListFragment = new FriendListFragment();
        Bundle args = new Bundle();
        friendListFragment.setArguments(args);

        return friendListFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.friend_list;
    }

    @Override
    public boolean enableLazyData() {
        return true;
    }

    @Override
    public void initView(View view) {
        mFriendAdapter = new FriendAdapter(mActivity, mViewModel.getList());
        mFriendAdapter.setItemClickListener(new BaseBindAdapter.OnItemClickListener<FriendBo>() {
            @Override
            public void onItemClick(FriendBo friendBo, int position) {
                KLog.i("clicked friend " + friendBo.getName());
                ShowEditDialog(friendBo);
            }
        });
        mViewModel.getList().addOnListChangedCallback(ObservableListUtil.getListChangedCallback(mFriendAdapter));
        mBinding.recview.setAdapter(mFriendAdapter);
        mViewModel.refreshData();
        mBinding.recview.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

        mBinding.btnFindGroup.setOnClickListener(v->{
            mViewModel.refreshData();
        });

        mBinding.btnChangePresence.setOnClickListener(v->{

        });
    }

    private void ShowEditDialog(FriendBo friendBo){
        FriendEditFragment friendEditFragment = new FriendEditFragment();
        Bundle args = new Bundle();
        args.putString("refId",friendBo.getRefId());
        friendEditFragment.setArguments(args);
        friendEditFragment.setFriendEditListener(()->{
            UIThreadUtil.runOnUiThread(()->{
                mViewModel.refreshData();
                KeyboardUtil.hideKeyboard(getActivity());
            });
        });
        friendEditFragment.show(getActivity().getSupportFragmentManager(),"编辑好友");
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
    public Class<FriendViewModel> onBindViewModel() {
        return FriendViewModel.class;
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
