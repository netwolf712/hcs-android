package com.hcs.android.ui.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hcs.android.ui.R;


public class VideoViewFragment extends Fragment {

    private PlayerView mVideoView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_view, container, false);
        mVideoView = view.findViewById(R.id.player);
        Bundle arguments = getArguments();
        String title = null;
        String filePath = null;
        if (arguments != null) {
            title = arguments.getString("title");
            filePath = arguments.getString("filePath");
            mVideoView.startPlay(filePath);
        }

        return view;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mVideoView != null){
            mVideoView.destroy();
        }
    }
}


