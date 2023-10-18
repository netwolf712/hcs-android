package com.hcs.app.adapter;

import android.content.Context;

import androidx.databinding.ObservableArrayList;

import com.hcs.android.business.entity.Attachment;
import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.app.R;
import com.hcs.app.databinding.ItemAttachmentListBinding;

public class AttachmentAdapter extends BaseBindAdapter<Attachment, ItemAttachmentListBinding> {


    public AttachmentAdapter(Context context, ObservableArrayList<Attachment> items) {
        super(context, items);
    }

    @Override
    protected int getLayoutItemId(int viewType) {
        return R.layout.item_attachment_list;
    }

    @Override
    protected void onBindItem(ItemAttachmentListBinding binding, final Attachment item, final int position) {
        binding.setAttachment(item);
    }


}
