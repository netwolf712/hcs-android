package com.hcs.commondemo.fragment;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hcs.android.common.BaseApplication;
import com.hcs.commondemo.R;
import com.hcs.commondemo.config.Config;

public class CameraDemoFragment extends Fragment{

    public static CameraDemoFragment newInstance() {
        CameraDemoFragment logDemoFragment = new CameraDemoFragment();
        Bundle args = new Bundle();
        logDemoFragment.setArguments(args);
        return logDemoFragment;
    }

    /**
     * 创建Camera对象
     * 用来获取摄像头
     */
    public Camera camera;
    /**
     * 创建surfaceview对象
     * 这个对象是用来显示从Camera返回的图像的
     */
    private SurfaceView surfaceview;
    //创建一个MediaRecorder对象，这是对象可以获取到视频流和音频流
    private MediaRecorder mediaRecorder;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_demo, container, false);

        mediaRecorder = new MediaRecorder();
        Window window = getActivity().getWindow();//得到窗口
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//设置高亮
        surfaceview = (SurfaceView) view.findViewById(R.id.surfaceview);//实例化surfaceview对象
        view.findViewById(R.id.btn_paizhao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置视频流
                viodeo();
            }
        });
        view.findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭并完成保存视频流
                if(mediaRecorder!=null){
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder=null;
                }
            }
        });
        return view;
    }
    //启动视频录制
    public void viodeo(){
        try{
            //实例化camera，其实也没什么多大用处，主要是因为摄像头的角度问题
            camera = Camera.open();
//            Camera.Parameters parameters = camera.getParameters();
//            //parameters.setRotation(90);
//            //这些在获取摄像头中有介绍
//            parameters.setPreviewSize(640, 480);
//            parameters.setPictureSize(640, 480);
//            camera.setParameters(parameters);
//            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(surfaceview.getHolder());
            camera.startPreview();
            camera.unlock();
            mediaRecorder.setCamera(camera);//使用camera的属性
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//导入Camera摄像头
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//设置音频设备

            //设置格式
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);//设置视频编码
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//设置音频编码
            //设置保存路径
            mediaRecorder.setOutputFile(Config.getRecordPath(BaseApplication.getAppContext()) + "/VID_"+System.currentTimeMillis()+".mp4");

            mediaRecorder.setPreviewDisplay(surfaceview.getHolder().getSurface());//在surfaceview中预览图像
            //启动录像功能
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(getActivity(),"录像中。。。",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}
