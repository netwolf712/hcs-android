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
import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.android.ui.mvvm.BaseMvvmFragment;
import com.hcs.android.ui.util.KeyboardUtil;
import com.hcs.android.ui.util.ObservableListUtil;
import com.hcs.android.ui.util.UIThreadUtil;
import com.hcs.app.BR;
import com.hcs.app.R;
import com.hcs.app.adapter.RoomAdapter;
import com.hcs.app.databinding.RoomListBinding;
import com.hcs.app.util.DisplayConvertUtil;


public class RoomListFragment extends BaseMvvmFragment<RoomListBinding, PlaceViewModel> {
    private RoomAdapter mRoomAdapter;

    public static RoomListFragment newInstance() {
        RoomListFragment friendListFragment = new RoomListFragment();
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
        return R.layout.room_list;
    }

    @Override
    public boolean enableLazyData() {
        return true;
    }

    @Override
    public void initView(View view) {
        mViewModel.setPlaceType(PlaceTypeEnum.ROOM.getValue());
        mRoomAdapter = new RoomAdapter(mActivity, mViewModel.getList());
        mRoomAdapter.setItemClickListener(new BaseBindAdapter.OnItemClickListener<PlaceModel>() {
            @Override
            public void onItemClick(PlaceModel roomModel, int position) {
                mViewModel.handleCall(DisplayConvertUtil.getFirstDevice(roomModel));
            }
        });
        mRoomAdapter.setOnItemLongClickListener(new BaseBindAdapter.OnItemLongClickListener<PlaceModel>() {
            @Override
            public boolean onItemLongClick(PlaceModel RoomModel, int position) {
                ShowEditDialog(RoomModel);
                return true;
            }
        });
        mViewModel.getList().addOnListChangedCallback(ObservableListUtil.getListChangedCallback(mRoomAdapter));
        mBinding.recview.setAdapter(mRoomAdapter);
        mBinding.recview.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        mViewModel.refreshData();
    }
    private void ShowEditDialog(PlaceModel roomModel){
        RoomEditFragment roomEditFragment = new RoomEditFragment();
        Bundle args = new Bundle();
        args.putString("deviceId",roomModel.getPlace().getMasterDeviceId());
        args.putString("placeUid",roomModel.getPlace().getUid());
        args.putInt("bedSn",roomModel.getPlace().getPlaceSn());
        roomEditFragment.setArguments(args);
        roomEditFragment.setFriendEditListener(()->{
            UIThreadUtil.runOnUiThread(()->{
                mViewModel.refreshData();
                KeyboardUtil.hideKeyboard(getActivity());
            });
        });
        roomEditFragment.show(getActivity().getSupportFragmentManager(),"编辑病房");
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
