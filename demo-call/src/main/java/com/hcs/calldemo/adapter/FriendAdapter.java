package com.hcs.calldemo.adapter;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableArrayList;

import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.calldemo.R;
import com.hcs.calldemo.databinding.ItemFriendListBinding;
import com.hcs.calldemo.entity.FriendBo;

public class FriendAdapter extends BaseBindAdapter<FriendBo, ItemFriendListBinding> {


    public FriendAdapter(Context context, ObservableArrayList<FriendBo> items) {
        super(context, items);
    }

    @Override
    protected int getLayoutItemId(int viewType) {
        return R.layout.item_friend_list;
    }

    @Override
    protected void onBindItem(ItemFriendListBinding binding, final FriendBo item, final int position) {
        binding.setFriend(item);
        binding.friendDetail.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(mItemClickListener != null){
                    mItemClickListener.onItemClick(item,position);
                }
            }
        });
    }


}
