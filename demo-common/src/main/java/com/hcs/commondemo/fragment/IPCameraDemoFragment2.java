package com.hcs.commondemo.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hcs.android.common.util.ToastUtil;
import com.hcs.android.ui.view.IPCameraView;
import com.hcs.commondemo.R;

public class IPCameraDemoFragment2 extends Fragment{
    /**
     * 网络摄像头ONVIF协议的访问账号
     */
    private final static String ONVIF_USER = "hcsxxx";
    private final static String ONVIF_PASSWORD = "hcsxxx";
    private final static String ONVIF_ADDRESS = "xxx";

    private String onvifUser;
    private String onvifPassword;
    private String onvifAddress;
    private IPCameraView mIpCameraView;
    public static IPCameraDemoFragment2 newInstance() {
        IPCameraDemoFragment2 logDemoFragment = new IPCameraDemoFragment2();
        Bundle args = new Bundle();
        logDemoFragment.setArguments(args);
        return logDemoFragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ip_camera_demo, container, false);
        mIpCameraView = view.findViewById(R.id.ipCameraView);

        view.findViewById(R.id.btnOnvifStart).setOnClickListener(view1 -> {
            EditText edtOnvifUser = view.findViewById(R.id.edtOnvifUser);
            onvifUser = edtOnvifUser.getText().toString();
            EditText edtOnvifPassword = view.findViewById(R.id.edtOnvifPassword);
            onvifPassword = edtOnvifPassword.getText().toString();
            EditText edtOnvifAddress = view.findViewById(R.id.edtOnvifAddress);
            onvifAddress = edtOnvifAddress.getText().toString();
            if(onvifUser.length() == 0){
                ToastUtil.showToast(R.string.ip_camera_onvif_user_notify);
                return;
            }
            if(onvifPassword.length() == 0){
                ToastUtil.showToast(R.string.ip_camera_onvif_password_notify);
                return;
            }
            if(onvifAddress.length() == 0){
                ToastUtil.showToast(R.string.ip_camera_onvif_address_notify);
                return;
            }
            mIpCameraView.openCamera(onvifAddress,onvifUser, onvifPassword);
        });
        return view;
    }

}
