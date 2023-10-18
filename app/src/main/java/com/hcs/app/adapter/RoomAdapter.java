package com.hcs.app.adapter;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableArrayList;

import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.app.R;
import com.hcs.app.databinding.ItemRoomListBinding;

public class RoomAdapter extends BaseBindAdapter<PlaceModel, ItemRoomListBinding> {


    public RoomAdapter(Context context, ObservableArrayList<PlaceModel> items) {
        super(context, items);
    }

    @Override
    protected int getLayoutItemId(int viewType) {
        return R.layout.item_room_list;
    }

    @Override
    protected void onBindItem(ItemRoomListBinding binding, final PlaceModel item, final int position) {
        binding.setRoomModel(item);
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
