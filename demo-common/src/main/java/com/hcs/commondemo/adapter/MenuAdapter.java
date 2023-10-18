package com.hcs.commondemo.adapter;

import android.content.Context;
import android.view.View;

import androidx.databinding.ObservableArrayList;
import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.commondemo.R;
import com.hcs.commondemo.databinding.ItemMenuListBinding;
import com.hcs.commondemo.entity.Menu;

public class MenuAdapter extends BaseBindAdapter<Menu, ItemMenuListBinding> {


    public MenuAdapter(Context context, ObservableArrayList<Menu> items) {
        super(context, items);
    }

    @Override
    protected int getLayoutItemId(int viewType) {
        return R.layout.item_menu_list;
    }

    @Override
    protected void onBindItem(ItemMenuListBinding binding, final Menu item, final int position) {
        binding.setMenu(item);
        binding.menuDetail.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(mItemClickListener != null){
                    mItemClickListener.onItemClick(item,position);
                }
            }
        });
    }


}
