package com.hcs.app.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.View;


import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.hcs.android.business.constant.PlaceTypeEnum;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.request.viewmodel.PlaceViewModel;
import com.hcs.android.business.request.viewmodel.ViewModelFactory;
import com.hcs.android.ui.util.KeyboardUtil;
import com.hcs.android.ui.util.UIThreadUtil;
import com.hcs.app.BR;
import com.hcs.app.R;
import com.hcs.app.adapter.FriendAdapter;
import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.android.ui.mvvm.BaseMvvmFragment;
import com.hcs.android.ui.util.ObservableListUtil;
import com.hcs.app.databinding.FriendListBinding;
import com.hcs.app.util.DisplayConvertUtil;


public class FriendListFragment extends BaseMvvmFragment<FriendListBinding, PlaceViewModel> {
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
        mViewModel.setPlaceType(PlaceTypeEnum.BED.getValue());
        mFriendAdapter = new FriendAdapter(mActivity, mViewModel.getList());
        mFriendAdapter.setItemClickListener(new BaseBindAdapter.OnItemClickListener<PlaceModel>() {
            @Override
            public void onItemClick(PlaceModel bedModel, int position) {
                //ShowEditDialog(bedModel);
                mViewModel.handleCall(DisplayConvertUtil.getFirstDevice(bedModel));
            }
        });
        mFriendAdapter.setOnItemLongClickListener(new BaseBindAdapter.OnItemLongClickListener<PlaceModel>() {
            @Override
            public boolean onItemLongClick(PlaceModel bedModel, int position) {
                ShowEditDialog(bedModel);
                return true;
            }
        });
        mViewModel.getList().addOnListChangedCallback(ObservableListUtil.getListChangedCallback(mFriendAdapter));
        mBinding.recview.setAdapter(mFriendAdapter);
        mBinding.recview.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        mViewModel.refreshData();
    }
    private void ShowEditDialog(PlaceModel bedModel){
        BedEditFragment bedEditFragment = new BedEditFragment();
        Bundle args = new Bundle();
        args.putString("deviceId",bedModel.getPlace().getMasterDeviceId());
        args.putString("placeUid",bedModel.getPlace().getUid());
        args.putInt("bedSn",bedModel.getPlace().getPlaceSn());
        bedEditFragment.setArguments(args);
        bedEditFragment.setFriendEditListener(()->{
            UIThreadUtil.runOnUiThread(()->{
                mViewModel.refreshData();
                KeyboardUtil.hideKeyboard(getActivity());
            });
        });
        bedEditFragment.show(getActivity().getSupportFragmentManager(),"编辑病床");
    }
    @Override
    public void initData() {
        mViewModel.refreshData();
    }

    @Override
    public void initListener() {
//        mNewsListAdatper.setItemClickListener(new BaseAdapter.OnItemClickListener<NewsDetail>() {
//            @Override
//            public void onItemClick(NewsDetail newsDetail, int position) {
//                NewsDetailActivity.startNewsDetailActivity(mActivity, newsDetail.getId());
//            }
//        });
    }

    @Override
    public String getToolbarTitle() {
        return null;
    }


    @Override
    public Class<PlaceViewModel> onBindViewModel() {
        return PlaceViewModel.class;
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
