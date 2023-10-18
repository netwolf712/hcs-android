package com.hcs.android.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.hcs.android.common.util.ISimpleCustomer;
import com.hcs.android.ui.R;
import com.hcs.android.ui.util.DisplayUtil;


public class SingleCloseDialog extends DialogFragment {

    private Fragment mFragment;

    /**
     * 关闭按钮
     */
    private Button mCloseBtn;
    /**
     * 是否显示关闭按钮
     */
    private boolean mShowCloseBtn = false;
    public SingleCloseDialog(){
        super();
    }
    public SingleCloseDialog(Fragment fragment){
        mFragment = fragment;
    }
    public SingleCloseDialog(Fragment fragment,boolean showCloseBtn){
        mShowCloseBtn = showCloseBtn;
        mFragment = fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_single_close, container, false);

        mCloseBtn = view.findViewById(R.id.btnClose);
        if(mShowCloseBtn) {
            mCloseBtn.setVisibility(View.VISIBLE);
            mCloseBtn.setOnClickListener(v -> {
                dismiss();
            });
        }
        if(mFragment != null) {
            FragmentManager manager = getChildFragmentManager();
            manager.beginTransaction().replace(R.id.frame, mFragment).commit();
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(getResources().getDisplayMetrics().widthPixels - DisplayUtil.dip2px(40) * 2,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//        getDialog().getWindow().setLayout(getResources().getDisplayMetrics().widthPixels * 6/8,
//                getResources().getDisplayMetrics().heightPixels * 7/8);
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private ISimpleCustomer<Object> mCloseListener;
    public void setCloseListener(ISimpleCustomer<Object> closeListener){
        mCloseListener = closeListener;
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
        if(mCloseListener != null){
            mCloseListener.accept(null);
        }
    }
}


