package com.hcs.android.common.record;
import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hcs.android.common.util.PermissionCheckUtil;
import com.hcs.android.common.util.log.KLog;

import java.io.IOException;

public class MediaRecord implements SurfaceHolder.Callback{

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private MediaRecorder mVideoRecorder;

    private boolean mCanRecordVideo = false;
    private boolean mWaitToRecordVideo = false;
    private SurfaceView mSurfaceView = null;
    private String mFilePath;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mFrq;
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        KLog.i("camera", "surface destroyed.");
        mSurfaceHolder = null;
        stopRecording();
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        KLog.i("camera", "surface surfaceCreated");
        mSurfaceHolder = holder;
        if(!mCanRecordVideo){
            if(mCamera != null){
                try {
                    mCamera.setPreviewDisplay(mSurfaceHolder);
                    mCamera.startPreview();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        mCanRecordVideo = true;
        if(mWaitToRecordVideo){
            startRecordVideo();
        }
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        KLog.i("camera", "surface changed.");
        mSurfaceHolder = holder;
    }

    /**
     * 停止录制
     */
    public void stopRecording() {
        KLog.i("mVideoRecorder", "stopRecording...");
        if (mVideoRecorder != null) {
            mVideoRecorder.stop();
            mVideoRecorder.reset();
            mVideoRecorder.release();
            mVideoRecorder = null;
        }
        releaseCamera();
        KLog.i("mVideoRecorder", "record stopped");
    }
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }
    private void startRecordVideo(){
        if(mCamera == null){
            KLog.w("startRecordVideo", "Camera is null");
            return;
        }
        mVideoRecorder = new MediaRecorder();
        //给摄像头解锁
        mCamera.unlock();
        //MediaRecorder获取到摄像头的访问权
        mVideoRecorder.setCamera(mCamera);
        //设置视频录制过程中所录制的音频来自手机的麦克风
        mVideoRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置视频源为摄像头
        mVideoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //设置视频录制的输出文件为AMR文件
        mVideoRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //设置音频编码方式为AAC
        mVideoRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //设置录制的视频编码为H.264
        mVideoRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mVideoRecorder.setVideoSize(mVideoWidth, mVideoHeight);
        mVideoRecorder.setVideoFrameRate(mFrq);

        mVideoRecorder.setOutputFile(mFilePath);
        mVideoRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        try {
            mVideoRecorder.prepare();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
        mVideoRecorder.start();
        KLog.i("mVideoRecorder", "beginRecording");
    }

    /**
     * 录制音视频
     * @param activity activity
     * @param surfaceView 显示预览的view
     * @param filePath 保存路径
     * @param videoWidth 视频宽度
     * @param videoHeight 视频高度
     * @param frq 帧率
     */
    public void recordVideo(Activity activity, SurfaceView surfaceView, String filePath, int videoWidth, int videoHeight, int frq) {
        mFilePath = filePath;
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
        mFrq = frq;
        PermissionCheckUtil.checkAndRequestRecordVideoPermissions(activity,accept->{
            if(accept){
                if(mCamera == null){
                    openCamera(activity,surfaceView);
                }
                if(mVideoRecorder != null){
                    return;
                }
                if(!mCanRecordVideo){
                    mWaitToRecordVideo = true;
                    return;
                }
                startRecordVideo();
            }
        });

    }

    /**
     * 录制音频
     * @param activity activity
     * @param filePath 保存路径
     */
    public void recordAudio(Activity activity,String filePath){
        PermissionCheckUtil.checkAndRequestRecordAudioPermissions(activity,accept -> {
            if(accept){
                try {
                    mVideoRecorder = new MediaRecorder();
                    mVideoRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                    mVideoRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    mVideoRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                    mVideoRecorder.setOutputFile(filePath);
                    mVideoRecorder.prepare();
                    mVideoRecorder.start();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 打开（预览）摄像头
     * @param activity activity
     * @param surfaceView 视频预览的view
     */
    public void openCamera(Activity activity, SurfaceView surfaceView) {
        mSurfaceView = surfaceView;
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        PermissionCheckUtil.checkAndRequestCameraVideoPermissions(activity,accept -> {
            if(mCamera == null){
                KLog.w("VideoRecorder", "camera is null");
                return;
            }
            KLog.i("mVideoRecorder", "openCamera.");
            try {
                mCamera = Camera.open(); // attempt to get a Camera instance
            } catch (Exception e) {
                // Camera is not available (in use or does not exist)
                KLog.e("camera", "open camera error!");
                e.printStackTrace();
                return;
            }
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException e) {
                KLog.e("camera", "preview failed.");
                e.printStackTrace();
            }
        });

    }

}
