package com.hcs.calldemo.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.hcs.android.common.util.StringUtil;
import com.hcs.android.ui.util.KeyboardUtil;
import com.hcs.calldemo.R;
import com.hcs.calldemo.databinding.DialogFriendBinding;
import com.hcs.calldemo.viewmodel.FriendDetailViewModel;

public class FriendEditFragment extends DialogFragment {

    private DialogFriendBinding mBinding;

    private FriendDetailViewModel friendDetailViewModel;

    private IFriendEditListener mFriendEditListener;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.hcs.android.ui.R.layout.fragment_root1, container, false);

        mBinding =  DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dialog_friend,view.findViewById(com.hcs.android.ui.R.id.view_stub_content),true);

        friendDetailViewModel = new FriendDetailViewModel();

        Bundle arguments = getArguments();
        if(arguments != null){
            String refId = arguments.getString("refId");
            if(!StringUtil.isEmpty(refId)){
                friendDetailViewModel.loadFriend(refId);
            }
        }

        mBinding.setFriend(friendDetailViewModel.getFriendBo());

        /**
         * 添加好友
         */
        mBinding.btnCommonDialogRight.setOnClickListener(v->{
            if(friendDetailViewModel.saveFriend()) {
                if (mFriendEditListener != null) {
                    mFriendEditListener.onReload();
                }
            }
            dismiss();
        });

        /**
         * 删除好友
         */
        mBinding.btnCommonDialogMiddle.setOnClickListener(v->{
            if(friendDetailViewModel.removeFriend()){
                mFriendEditListener.onReload();
            }
            dismiss();
        });

        /**
         * 取消
         */
        mBinding.btnCommonDialogLeft.setOnClickListener(v->{
            dismiss();
        });


        /**
         * 点击卡片空白区域时隐藏键盘
         */
        mBinding.friendDetail.setOnClickListener(v->{
            KeyboardUtil.hideKeyboard(getActivity());
        });

        return view;
    }

    public void setFriendEditListener(IFriendEditListener friendEditListener){
        mFriendEditListener = friendEditListener;
    }

    public interface IFriendEditListener{
        /**
         * 告诉调用者需要重新加载
         */
        void onReload();
    }
}
