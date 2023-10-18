package com.hcs.app.adapter;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableArrayList;

import com.hcs.android.business.entity.OperationLogModel;
import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.app.R;
import com.hcs.app.databinding.ItemCallLogListBinding;

public class CallLogAdapter extends BaseBindAdapter<OperationLogModel, ItemCallLogListBinding> {


    public CallLogAdapter(Context context, ObservableArrayList<OperationLogModel> items) {
        super(context, items);
    }

    @Override
    protected int getLayoutItemId(int viewType) {
        return R.layout.item_call_log_list;
    }

    @Override
    protected void onBindItem(ItemCallLogListBinding binding, final OperationLogModel item, final int position) {
        binding.setOperationLogModel(item);
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
