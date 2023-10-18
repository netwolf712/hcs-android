package com.hcs.app.adapter;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableArrayList;

import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.app.R;
import com.hcs.app.databinding.ItemFriendListBinding;

public class FriendAdapter extends BaseBindAdapter<PlaceModel, ItemFriendListBinding> {


    public FriendAdapter(Context context, ObservableArrayList<PlaceModel> items) {
        super(context, items);
    }

    @Override
    protected int getLayoutItemId(int viewType) {
        return R.layout.item_friend_list;
    }

    @Override
    protected void onBindItem(ItemFriendListBinding binding, final PlaceModel item, final int position) {
        binding.setFriend(item);
        binding.friendDetail.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(mItemClickListener != null){
                    mItemClickListener.onItemClick(item,position);
                }
            }
        });
        binding.friendDetail.setOnLongClickListener(view -> {
            if(mOnItemLongClickListener != null){
                mOnItemLongClickListener.onItemLongClick(item,position);
            }
            return true;
        });
    }


}
