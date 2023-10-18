package com.hcs.calldemo.adapter;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableArrayList;

import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.calldemo.R;
import com.hcs.calldemo.databinding.ItemMessageListBinding;
import com.hcs.calldemo.entity.ChatMessageBo;

public class MessageAdapter extends BaseBindAdapter<ChatMessageBo, ItemMessageListBinding> {


    public MessageAdapter(Context context, ObservableArrayList<ChatMessageBo> items) {
        super(context, items);
    }

    @Override
    protected int getLayoutItemId(int viewType) {
        return R.layout.item_message_list;
    }

    @Override
    protected void onBindItem(ItemMessageListBinding binding, final ChatMessageBo item, final int position) {
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
