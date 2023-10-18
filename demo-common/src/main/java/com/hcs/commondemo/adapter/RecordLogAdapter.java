package com.hcs.commondemo.adapter;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableArrayList;

import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.commondemo.R;
import com.hcs.commondemo.databinding.ItemRecordLogListBinding;
import com.hcs.commondemo.entity.RecordBo;


public class RecordLogAdapter extends BaseBindAdapter<RecordBo, ItemRecordLogListBinding> {


    public RecordLogAdapter(Context context, ObservableArrayList<RecordBo> items) {
        super(context, items);
    }

    @Override
    protected int getLayoutItemId(int viewType) {
        return R.layout.item_record_log_list;
    }

    @Override
    protected void onBindItem(ItemRecordLogListBinding binding, final RecordBo item, final int position) {
        binding.setRecord(item);
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
