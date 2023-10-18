package com.hcs.commondemo.fragment;


import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.hcs.android.common.util.log.KLog;
import com.hcs.android.ui.adapter.BaseBindAdapter;
import com.hcs.android.ui.mvvm.BaseMvvmFragment;
import com.hcs.android.ui.util.ObservableListUtil;
import com.hcs.commondemo.BR;
import com.hcs.commondemo.R;
import com.hcs.commondemo.adapter.MessageAdapter;
import com.hcs.commondemo.databinding.MulticastMessageDemoBinding;
import com.hcs.commondemo.entity.MulticastMessageBo;
import com.hcs.commondemo.factory.ViewModelFactory;
import com.hcs.commondemo.viewmodel.MulticastViewModel;

public class VolumeDemoFragment extends BaseMvvmFragment<MulticastMessageDemoBinding, MulticastViewModel> {

    public static VolumeDemoFragment newInstance() {
        VolumeDemoFragment volumeDemoFragment = new VolumeDemoFragment();
        Bundle args = new Bundle();
        volumeDemoFragment.setArguments(args);

        return volumeDemoFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.volume_demo;
    }

    @Override
    public boolean enableLazyData() {
        return true;
    }

    @Override
    public void initView(View view) {
        AudioManager am = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
        EditText editText = view.findViewById(R.id.mediaVolume);
        editText.setText(String.valueOf(am.getStreamVolume(AudioManager.STREAM_MUSIC)));

        view.findViewById(R.id.btnSave).setOnClickListener(view1 -> {
            int volume = Integer.parseInt(editText.getText().toString());
            am.setStreamVolume(AudioManager.STREAM_MUSIC,volume,AudioManager.FLAG_PLAY_SOUND);
        });
    }

    @Override
    public void initData() {
        mViewModel.refreshData();
    }

    @Override
    public void initListener() {
    }

    @Override
    public String getToolbarTitle() {
        return null;
    }


    @Override
    public Class<MulticastViewModel> onBindViewModel() {
        return MulticastViewModel.class;
    }

    @Override
    public ViewModelProvider.Factory onBindViewModelFactory() {
        return ViewModelFactory.getInstance(mActivity.getApplication());
    }

    @Override
    public void initViewObservable() {

    }

    @Override
    public int onBindVariableId() {
        return BR.viewModel;
    }

}
