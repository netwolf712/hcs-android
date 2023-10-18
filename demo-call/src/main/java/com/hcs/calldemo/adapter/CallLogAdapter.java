package com.hcs.calldemo.adapter;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableArrayList;

import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.calldemo.R;
import com.hcs.calldemo.databinding.ItemCallLogListBinding;
import com.hcs.calldemo.databinding.ItemMessageListBinding;
import com.hcs.calldemo.entity.CallLogBo;
import com.hcs.calldemo.entity.ChatMessageBo;

public class CallLogAdapter extends BaseBindAdapter<CallLogBo, ItemCallLogListBinding> {


    public CallLogAdapter(Context context, ObservableArrayList<CallLogBo> items) {
        super(context, items);
    }

    @Override
    protected int getLayoutItemId(int viewType) {
        return R.layout.item_call_log_list;
    }

    @Override
    protected void onBindItem(ItemCallLogListBinding binding, final CallLogBo item, final int position) {
        binding.setCallLog(item);
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
