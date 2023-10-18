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
import com.hcs.android.business.entity.DictModel;
import com.hcs.android.business.entity.Patient;
import com.hcs.android.business.entity.PatientModel;
import com.hcs.android.business.entity.PlaceModel;
import com.hcs.android.business.request.model.DictRequestModel;
import com.hcs.android.business.request.model.PatientRequestModel;
import com.hcs.android.business.request.viewmodel.PlaceDetailViewModel;
import com.hcs.android.business.util.PlaceUtil;
import com.hcs.android.common.util.FileUtil;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.ui.util.KeyboardUtil;
import com.hcs.app.R;
import com.hcs.app.databinding.DialogBedBinding;

import java.nio.charset.StandardCharsets;

public class BedEditFragment extends DialogFragment {

    private DialogBedBinding mBinding;

    private PlaceDetailViewModel bedDetailViewModel;

    private DictRequestModel dictRequestModel;

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

        mBinding =  DataBindingUtil.inflate(LayoutInflater.from(getActivity()), R.layout.dialog_bed,view.findViewById(com.hcs.android.ui.R.id.view_stub_content),true);

        bedDetailViewModel = new PlaceDetailViewModel();
        dictRequestModel = new DictRequestModel();
        Bundle arguments = getArguments();
        if(arguments != null){
            String deviceId = arguments.getString("deviceId");
            String placeUid = arguments.getString("placeUid");
            Integer bedSn = arguments.getInt("bedSn");
            if(!StringUtil.isEmpty(placeUid)){
                bedDetailViewModel.loadPlaceDetail(placeUid);
            }
        }

        mBinding.setFriend(bedDetailViewModel.getPlaceModel());

        /**
         * 添加病员
         */
        mBinding.btnCommonDialogRight.setOnClickListener(v->{
            //只是为了方便测试，土就土一点吧，全手工填写
            PlaceModel placeModel = bedDetailViewModel.getPlaceModel();
            placeModel.getPlace().setPlaceNo(mBinding.txtBedNoValue.getText().toString());
            bedDetailViewModel.changePlaceInfo();
            PatientModel patientModel = PlaceUtil.getFirstPatient(placeModel);
            if(patientModel == null){
                patientModel = new PatientModel();
                placeModel.getPatientModelList().add(patientModel);
            }
            Patient patient = patientModel.getPatient();
            patient.setName(mBinding.txtPatientNameValue.getText().toString());
            patient.setSerialNumber(mBinding.txtPatientSnValue.getText().toString());
            patient.setBedSn(placeModel.getPlace().getPlaceSn());
            patient.setMasterDeviceId(placeModel.getPlace().getMasterDeviceId());
            //下面就写死吧
            final DictModel dictModel = dictRequestModel.getDictModel();
            patient.setProtection(String.valueOf(dictModel.getProtectionDictList().get(0).getDictValue()));
            patient.setAllergy(String.valueOf(dictModel.getAllergyDictList().get(0).getDictValue()));
            patient.setBloodType(dictModel.getBloodTypeDictList().get(0).getDictValue());
            patient.setCriticalType(dictModel.getCriticalTypeDictList().get(0).getDictValue());
            patient.setMedicalInsuranceType(dictModel.getMedicalInsuranceTypeDictList().get(0).getDictValue());
            patient.setNursingLevel(dictModel.getNursingLevelDictList().get(0).getDictValue());
            patient.setMetering(dictModel.getMeteringDictList().get(0).getDictValue());
            patient.setDiet(dictModel.getDietDictList().get(0).getDictValue());
            patient.setSex(dictModel.getSexDictList().get(0).getDictValue());
            patient.setAge(12);
            patient.setUpdateTime(System.currentTimeMillis());
            patient.setDoctorAdvice("doctor");
            patient.setDoctorId(1);
            patient.setDoctorName("zhang san");
            patient.setIllness("very bad");
            patient.setInHospitalTime(System.currentTimeMillis());
            patient.setIsolate(false);
            patient.setNurseId(1);
            patient.setNurseName("li si");
            patient.setDoctorImage("https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E5%8C%BB%E6%8A%A4%E4%BA%BA%E5%91%98%E7%AE%80%E7%AC%94%E7%94%BB&step_word=&hs=0&pn=35&spn=0&di=7084067677328637953&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=2&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=-1&cs=3747984239%2C392084207&os=2799871722%2C3979283422&simid=3747984239%2C392084207&adpicid=0&lpn=0&ln=1666&fr=&fmq=1654075036064_R&fm=result&ic=0&s=undefined&hd=&latest=&copyright=&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=https%3A%2F%2Fgimg2.baidu.com%2Fimage_search%2Fsrc%3Dhttp%3A%2F%2Ftqjimg.tianqistatic.com%2Ftoutiao%2Fjianbihuadaquan%2Fimages%2F202006%2F03%2F65f34c78f06446a8.jpg%2Ftqj_pc%26refer%3Dhttp%3A%2F%2Ftqjimg.tianqistatic.com%26app%3D2002%26size%3Df9999%2C10000%26q%3Da80%26n%3D0%26g%3D0n%26fmt%3Dauto%3Fsec%3D1656667085%26t%3Dc93f710f94d0f4a5afbd51d56da2621e&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bptwgqt37g_z%26e3Bv54AzdH3F3twgkti7w1wq7wgAzdH3F15vAzdH3Fnnmal_z%26e3Bip4s&gsm=23&rpstart=0&rpnum=0&islist=&querylist=&nojc=undefined");
            patient.setNurseImage("https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E5%8C%BB%E6%8A%A4%E4%BA%BA%E5%91%98%E7%AE%80%E7%AC%94%E7%94%BB&step_word=&hs=0&pn=32&spn=0&di=7084067677328637953&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&istype=2&ie=utf-8&oe=utf-8&in=&cl=2&lm=-1&st=-1&cs=3984559809%2C2015891587&os=4018882098%2C514133314&simid=3984559809%2C2015891587&adpicid=0&lpn=0&ln=1666&fr=&fmq=1654075036064_R&fm=result&ic=0&s=undefined&hd=&latest=&copyright=&se=&sme=&tab=0&width=&height=&face=undefined&ist=&jit=&cg=&bdtype=0&oriquery=&objurl=https%3A%2F%2Fgimg2.baidu.com%2Fimage_search%2Fsrc%3Dhttp%3A%2F%2Ftqjimg.tianqistatic.com%2Ftoutiao%2Fjianbihuadaquan%2Fimages%2F202006%2F03%2F9f54c27b5e0c0ec7.jpg%2Ftqj_pc%26refer%3Dhttp%3A%2F%2Ftqjimg.tianqistatic.com%26app%3D2002%26size%3Df9999%2C10000%26q%3Da80%26n%3D0%26g%3D0n%26fmt%3Dauto%3Fsec%3D1656667049%26t%3Dbb6820ef542bc3d5da2fac5863807436&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bptwgqt37g_z%26e3Bv54AzdH3F3twgkti7w1wq7wgAzdH3F15vAzdH3Fnnmaa_z%26e3Bip4s&gsm=21&rpstart=0&rpnum=0&islist=&querylist=&nojc=undefined");
            PatientRequestModel patientRequestModel = new PatientRequestModel();
            patientRequestModel.changePatientInfo(patientModel);
            dismiss();
        });

        /**
         * 删除病员
         */
        mBinding.btnCommonDialogMiddle.setOnClickListener(v->{

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
