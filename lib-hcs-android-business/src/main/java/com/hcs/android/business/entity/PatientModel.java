package com.hcs.android.business.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.hcs.android.business.service.DictService;
import com.hcs.android.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import androidx.databinding.library.baseAdapters.BR;
/**
 * 根据数据库内容二次转换后的病员信息
 */
public class PatientModel extends BaseObservable {
    /**
     * 对应数据库表的病员信息
     */
    private Patient patient;
    @Bindable
    public Patient getPatient(){
        return patient;
    }
    public void setPatient(Patient patient){
        this.patient = patient;
        notifyPropertyChanged(BR.patient);
    }

    /**
     * 性别
     */
    private Dict sexDict;
    public Dict getSexDict(){
        return sexDict;
    }
    public void setSexDict(@NonNull Dict sexDict){
        this.sexDict = sexDict;
        this.patient.setSex(sexDict.getDictValue());
    }

    /**
     * 计量
     */
    private Dict meteringDict;
    public Dict getMeteringDict(){
        return meteringDict;
    }
    public void setMeteringDict(@NonNull Dict meteringDict){
        this.meteringDict = meteringDict;
        this.patient.setMetering(meteringDict.getDictValue());
    }

    /**
     * 护理等级
     */
    private Dict nursingLevelDict;
    public Dict getNursingLevelDict(){
        return nursingLevelDict;
    }
    public void setNursingLevelDict(@NonNull Dict nursingLevelDict){
        this.nursingLevelDict = nursingLevelDict;
        this.patient.setNursingLevel(nursingLevelDict.getDictValue());
    }

    /**
     * 危重类型
     */
    private Dict criticalTypeDict;
    public Dict getCriticalTypeDict(){
        return criticalTypeDict;
    }
    public void setCriticalTypeDict(@NonNull Dict criticalTypeDict){
        this.criticalTypeDict = criticalTypeDict;
        this.patient.setCriticalType(criticalTypeDict.getDictValue());
    }

    /**
     * 饮食类型
     */
    private Dict dietDict;
    public Dict getDietDict(){
        return dietDict;
    }
    public void setDietDict(@NonNull Dict dietDict){
        this.dietDict = dietDict;
        this.patient.setDiet(dietDict.getDictValue());
    }

    /**
     * 过敏列表
     */
    private List<Dict> allergyList;
    public List<Dict> getAllergyList(){
        return allergyList;
    }
    public void setAllergyList(List<Dict> allergyList){
        this.allergyList = allergyList;
        this.patient.setAllergy(DictService.convertValueListToString(allergyList));
    }

    /**
     * 医保类型
     */
    private Dict medicalInsuranceTypeDict;
    public Dict getMedicalInsuranceTypeDict(){
        return medicalInsuranceTypeDict;
    }
    public void setMedicalInsuranceTypeDict(@NonNull Dict medicalInsuranceTypeDict){
        this.medicalInsuranceTypeDict = medicalInsuranceTypeDict;
        this.patient.setMedicalInsuranceType(medicalInsuranceTypeDict.getDictValue());
    }

    /**
     * 防护列表
     */
    private List<Dict> protectionList;
    public List<Dict> getProtectionList(){
        return protectionList;
    }
    public void setProtectionList(List<Dict> protectionList){
        this.protectionList = protectionList;
        this.patient.setProtection(DictService.convertValueListToString(protectionList));
    }

    /**
     * 血型
     */
    private Dict bloodTypeDict;
    public Dict getBloodTypeDict(){
        return bloodTypeDict;
    }
    public void setBloodTypeDict(@NonNull Dict bloodTypeDict){
        this.bloodTypeDict = bloodTypeDict;
        this.patient.setBloodType(bloodTypeDict.getDictValue());
    }

    public PatientModel(){
        this.patient = new Patient();
    }

    public void reloadPatient(@NonNull Patient patient){
        try {
            this.patient = patient;
            DictModel dictModel = DictService.getInstance().getDictModelFromCache();
            this.sexDict = DictModel.findDictByValue(dictModel.getSexDictList(),patient.getSex());
            this.meteringDict = DictModel.findDictByValue(dictModel.getMeteringDictList(),patient.getMetering());
            this.nursingLevelDict = DictModel.findDictByValue(dictModel.getNursingLevelDictList(),patient.getNursingLevel());
            this.criticalTypeDict = DictModel.findDictByValue(dictModel.getCriticalTypeDictList(),patient.getCriticalType());
            this.dietDict = DictModel.findDictByValue(dictModel.getDietDictList(),patient.getDiet());
            this.allergyList = DictModel.convertToDictList(dictModel.getAllergyDictList(),patient.getAllergy());
            this.medicalInsuranceTypeDict = DictModel.findDictByValue(dictModel.getMedicalInsuranceTypeDictList(),patient.getMedicalInsuranceType());
            this.protectionList = DictModel.convertToDictList(dictModel.getProtectionDictList(),patient.getProtection());
            this.bloodTypeDict = DictModel.findDictByValue(dictModel.getBloodTypeDictList(),patient.getBloodType());
            notifyChange();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public PatientModel(@NonNull Patient patient){
        reloadPatient(patient);
    }

    /**
     * 比较是否需要更新
     */
    public boolean needUpdate(@NonNull PatientModel patientModel){
        return StringUtil.equalsIgnoreCase(patientModel.getPatient().getSerialNumber(), this.getPatient().getSerialNumber())
                && patientModel.getPatient().getUpdateTime().equals(this.getPatient().getUpdateTime());
    }
}
