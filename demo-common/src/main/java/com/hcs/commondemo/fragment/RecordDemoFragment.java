package com.hcs.commondemo.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.FileUtil;
import com.hcs.android.ui.mvvm.BaseFragment;
import com.hcs.android.ui.view.AudioViewFragment;
import com.hcs.android.ui.view.SingleCloseDialog;
import com.hcs.commondemo.R;
import com.hcs.commondemo.config.Config;
import com.hcs.commondemo.databinding.RecordDemoBinding;
import com.hcs.commondemo.viewmodel.RecordViewModel;

import java.io.File;

public class RecordDemoFragment extends BaseFragment {


    private RecordDemoBinding mBinding;

    private RecordViewModel mViewModel;
    

    public static RecordDemoFragment newInstance() {
        RecordDemoFragment recordDemoFragment = new RecordDemoFragment();
        Bundle args = new Bundle();
        recordDemoFragment.setArguments(args);
        return recordDemoFragment;
    }

    @Override
    public void initContentView(ViewGroup root){
        mBinding =  DataBindingUtil.inflate(LayoutInflater.from(mActivity), onBindLayout(),root,true);

        mViewModel = new RecordViewModel();
        mBinding.setRecord(mViewModel.getRecordBo());

        /**
         * 准备开始视频录制
         */
        mBinding.btnPrepareVideo.setOnClickListener(v->{
            mViewModel.prepareVideo(getActivity(),mBinding.frgVideo);
        });
        /**
         * 开始视频录制
         */
        mBinding.btnRecordVideo.setOnClickListener(v->{
            mViewModel.recordVideo(getActivity(),mBinding.frgVideo,RecordViewModel.RECORD_AUDIO_AND_VIDEO);
        });

        /**
         * 开始音频录制
         */
        mBinding.btnRecordAudio.setOnClickListener(v->{
//            mViewModel.recordVideo(getActivity(),mBinding.frgVideo,RecordViewModel.RECORD_AUDIO_ONLY);
//            mBinding.btnRecordStop.setVisibility(View.VISIBLE);
            AudioRecordDialog audioRecordDialog = new AudioRecordDialog();
            Bundle args = new Bundle();
            String fileDir = Config.getRecordPath(BaseApplication.getAppContext());
            FileUtil.createOrExistsDir(new File(fileDir));
            args.putString(AudioRecordDialog.PARAM_RECORD_DIR, fileDir);
            args.putString(AudioRecordDialog.PARAM_RECORD_NAME,mBinding.etRecordAddress.getText().toString());
            audioRecordDialog.setArguments(args);
            audioRecordDialog.show(mActivity.getSupportFragmentManager(), "");
        });

        mBinding.btnPlayAudio.setOnClickListener(v->{
//            AudioRecordDialog audioRecordDialog = new AudioRecordDialog();
//            Bundle args = new Bundle();
//            args.putString(AudioRecordDialog.PARAM_PLAY_FILE_PATH, Config.getRecordPath(BaseApplication.getAppContext()) + "/" + mBinding.etRecordAddress.getText().toString() + ".wav");
//            audioRecordDialog.setArguments(args);
//            audioRecordDialog.show(mActivity.getSupportFragmentManager(), "");
            AudioViewFragment audioViewFragment = new AudioViewFragment();
            Bundle args = new Bundle();
            args.putString("title", mBinding.etRecordAddress.getText().toString());
            args.putString("filePath",Config.getRecordPath(BaseApplication.getAppContext()) + "/" + mBinding.etRecordAddress.getText().toString() + ".mp4");
            audioViewFragment.setArguments(args);
            SingleCloseDialog singleCloseDialog = new SingleCloseDialog(audioViewFragment);
            singleCloseDialog.show(mActivity.getSupportFragmentManager(), "");
        });
        /**
         * 停止录制
         */
        mBinding.btnRecordStop.setOnClickListener(v->{
            mViewModel.stopRecord();
            mBinding.btnRecordStop.setVisibility(View.GONE);
        });
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public int onBindLayout() {
        return R.layout.record_demo;
    }

    @Override
    public boolean enableLazyData() {
        return true;
    }

    @Override
    public void initView(View view) {

    }

    @Override
    public void initData() {
    }

    @Override
    public void initListener() {

    }

    @Override
    public String getToolbarTitle() {
        return null;
    }



}
