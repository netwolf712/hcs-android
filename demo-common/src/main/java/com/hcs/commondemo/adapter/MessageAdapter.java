package com.hcs.commondemo.adapter;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableArrayList;

import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.commondemo.R;
import com.hcs.commondemo.databinding.ItemMessageListBinding;
import com.hcs.commondemo.entity.MulticastMessageBo;

public class MessageAdapter extends BaseBindAdapter<MulticastMessageBo, ItemMessageListBinding> {


    public MessageAdapter(Context context, ObservableArrayList<MulticastMessageBo> items) {
        super(context, items);
    }

    @Override
    protected int getLayoutItemId(int viewType) {
        return R.layout.item_message_list;
    }

    @Override
    protected void onBindItem(ItemMessageListBinding binding, final MulticastMessageBo item, final int position) {
        binding.setMessage(item);
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
