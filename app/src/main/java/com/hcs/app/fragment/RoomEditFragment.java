package com.hcs.app.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.hcs.android.business.BusinessApplication;
import com.hcs.android.business.request.viewmodel.PlaceDetailViewModel;
import com.hcs.android.common.util.FileUtil;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.ui.util.KeyboardUtil;
import com.hcs.app.R;
import com.hcs.app.databinding.DialogRoomBinding;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class RoomEditFragment extends DialogFragment {

    private DialogRoomBinding mBinding;

    private PlaceDetailViewModel roomDetailViewModel;

    private IBedEditListener mBedEditListener;

    /**
     * 医护图片，随便放了两张
     */
    private final static String NURSE_IMAGE_FILE = "nurse_image.txt";
    private final static String DOCTOR_IMAGE_FILE = "doctor_image.txt";
    @NonNull
    private String loadImageTxt(String fileName){
        try {
            String filePath = BusinessApplication.getAppContext().getFilesDir().getAbsolutePath() + "/" + fileName;
            if(NURSE_IMAGE_FILE.equalsIgnoreCase(fileName)) {
                FileUtil.copyIfNotExist(BusinessApplication.getAppContext(), R.raw.nurse_image, filePath);
            }else {
                FileUtil.copyIfNotExist(BusinessApplication.getAppContext(), R.raw.doctor_image, filePath);
            }
            byte[] orgData = FileUtil.getFileByte(filePath);
            return new String(orgData, StandardCharsets.UTF_8);
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(com.hcs.android.ui.R.layout.fragment_root1, container, false);

        mBinding =  DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dialog_room,view.findViewById(com.hcs.android.ui.R.id.view_stub_content),true);

        roomDetailViewModel = new PlaceDetailViewModel();

        Bundle arguments = getArguments();
        if(arguments != null){
            String deviceId = arguments.getString("deviceId");
            String placeUid = arguments.getString("placeUid");
            Integer roomSn = arguments.getInt("roomSn");

            if(!StringUtil.isEmpty(placeUid)){
                roomDetailViewModel.loadPlaceDetail(placeUid);
            }
        }

        mBinding.setRoomModel(roomDetailViewModel.getPlaceModel());

        /**
         * 保存修改
         */
        mBinding.btnCommonDialogRight.setOnClickListener(v->{
            //只是为了方便测试，土就土一点吧，全手工填写
            //修改房间编号
            roomDetailViewModel.getPlaceModel().getPlace().setPlaceNo(mBinding.txtBedNoValue.getText().toString());
            roomDetailViewModel.changePlaceInfo();
            dismiss();
        });

        /**
         * 取消
         */
        mBinding.btnCommonDialogLeft.setOnClickListener(v->{
            dismiss();
        });


        /**
         * 点击卡片空白区域时隐藏键盘
         */
        mBinding.friendDetail.setOnClickListener(v->{
            KeyboardUtil.hideKeyboard(getActivity());
        });

        return view;
    }

    public void setFriendEditListener(IBedEditListener friendEditListener){
        mBedEditListener = friendEditListener;
    }

    public interface IBedEditListener{
        /**
         * 告诉调用者需要重新加载
         */
        void onReload();
    }
}
