package com.hcs.commondemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.hcs.android.common.util.StringUtil;
import com.hcs.android.ui.view.WaveView;
import com.hcs.commondemo.R;

public class AudioRecordDialog extends DialogFragment {

    /**
     * 文件录制目录参数
     */
    public static final String PARAM_RECORD_DIR = "RECORD_DIR";
    /**
     * 录制文件名称参数
     */
    public static final String PARAM_RECORD_NAME = "RECORD_NAME";
    /**
     * 文件绝对路径参数
     */
    public static final String PARAM_PLAY_FILE_PATH = "PLAY_FILE_PATH";
    /**
     * 视频展示视图
     */
    private WaveView mWaveView;

    /**
     * 录制按钮
     */
    private Button mRecordBtn;

    /**
     * 暂停按钮
     */
    private Button mPauseBtn;

    /**
     * 文件录制路径
     */
    private String mRecordDir;

    /**
     * 文件录制名称
     */
    private String mRecordName;

    /**
     * 用于播放的文件的绝对路径
     */
    private String mPlayFilePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.record_audio_dialog, container, false);
        mWaveView = view.findViewById(R.id.waveView);
        Bundle arguments = getArguments();
        String title = null;
        String filePath = null;
        if (arguments != null) {
            mRecordDir = arguments.getString(PARAM_RECORD_DIR);
            mRecordName = arguments.getString(PARAM_RECORD_NAME);
            mPlayFilePath = arguments.getString(PARAM_PLAY_FILE_PATH);
        }

        view.findViewById(R.id.closeBtn).setOnClickListener(v->{
            mWaveView.stopPlay();
            mWaveView.stopRecord();
            dismiss();
        });

        mPauseBtn = view.findViewById(R.id.pauseBtn);
        mPauseBtn.setOnClickListener(view1 -> {
            if(mWaveView.isPaused()){
                mWaveView.resume();
                mPauseBtn.setText(R.string.pause);
            }else{
                mWaveView.pause();
                mPauseBtn.setText(R.string.resume);
            }
        });

        mRecordBtn = view.findViewById(R.id.recordBtn);
        if(!StringUtil.isEmpty(mPlayFilePath)) {
            mRecordBtn.setText(R.string.start_play);
            mRecordBtn.setVisibility(View.GONE);
            mWaveView.setLoadListener(()->{
                mRecordBtn.setVisibility(View.VISIBLE);
            });
            mWaveView.loadAudioFile(mPlayFilePath,getActivity());
            mRecordBtn.setOnClickListener(v -> {
                if(!mWaveView.isPlaying()){
                    mWaveView.playFile(0);
                    mRecordBtn.setText(R.string.stop_play);
                }else{
                    mWaveView.stopPlay();
                    mRecordBtn.setText(R.string.start_play);
                }
            });
        }else{
            mRecordBtn.setText(R.string.start_record);
            mRecordBtn.setOnClickListener(v -> {
                if(!mWaveView.isRecording()){
                    mPauseBtn.setVisibility(View.VISIBLE);
                    mWaveView.startRecord(mRecordDir+"/"+mRecordName + ".wav",getActivity());
                    mRecordBtn.setText(R.string.stop_record);
                }else{
                    mPauseBtn.setVisibility(View.INVISIBLE);
                    mWaveView.stopRecord();
                    mRecordBtn.setText(R.string.start_record);
                }
            });
        }
        return view;
    }
}


