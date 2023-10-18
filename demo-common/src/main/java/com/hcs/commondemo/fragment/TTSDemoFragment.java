package com.hcs.commondemo.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hcs.android.common.util.TTSUtil;
import com.hcs.commondemo.R;

public class TTSDemoFragment extends Fragment{

    public static TTSDemoFragment newInstance() {
        TTSDemoFragment logDemoFragment = new TTSDemoFragment();
        Bundle args = new Bundle();
        logDemoFragment.setArguments(args);
        return logDemoFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tts_demo, container, false);

        view.findViewById(R.id.startTestBtn).setOnClickListener(view1 -> {
            TTSUtil.getInstance(getContext()).speakText(getString(R.string.tts_test));
        });

        return view;
    }
}
