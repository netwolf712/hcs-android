package com.hcs.android.business.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcs.android.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class DictModel {

    /**
     * 性别的字典标识
     */
    public final static String DICT_TYPE_SEX = "sex";

    /**
     * 计量的字典标识
     */
    public final static String DICT_TYPE_METERING = "metering";

    /**
     * 护理级别的字典标识
     */
    public final static String DICT_TYPE_NURSING_LEVEL = "nursing_level";

    /**
     * 危重级别的字典标识
     */
    public final static String DICT_TYPE_CRITICAL_TYPE = "critical_type";

    /**
     * 饮食的字典标识
     */
    public final static String DICT_TYPE_DIET = "diet";

    /**
     * 过敏类型的字典标识
     */
    public final static String DICT_TYPE_ALLERGY = "allergy";

    /**
     * 医保类型的字典标识
     */
    public final static String DICT_TYPE_MEDICAL_INSURANCE_TYPE = "medical_insurance_type";

    /**
     * 防护类型的字典标识
     */
    public final static String DICT_TYPE_PROTECTION = "protection";

    /**
     * 血型的字典标识
     */
    public final static String DICT_TYPE_BLOOD_TYPE = "blood_type";

    /**
     * 分机所在的位置的字典标识
     */
    public final static String DICT_TYPE_SLAVE_PLACE = "slave_place";

    /**
     * 性别字典
     */
    private List<Dict> sexDictList;
    public List<Dict> getSexDictList() {
        return sexDictList;
    }
    public void setSexDictList(List<Dict> sexDictList) {
        this.sexDictList = sexDictList;
    }

    /**
     * 计量字典
     */
    private List<Dict> meteringDictList;
    public List<Dict> getMeteringDictList() {
        return meteringDictList;
    }
    public void setMeteringDictList(List<Dict> meteringDictList) {
        this.meteringDictList = meteringDictList;
    }

    /**
     * 护理级别字典
     */
    private List<Dict> nursingLevelDictList;
    public List<Dict> getNursingLevelDictList() {
        return nursingLevelDictList;
    }
    public void setNursingLevelDictList(List<Dict> nursingLevelDictList) {
        this.nursingLevelDictList = nursingLevelDictList;
    }

    /**
     * 危重类型字典
     */
    public List<Dict> criticalTypeDictList;
    public List<Dict> getCriticalTypeDictList() {
        return criticalTypeDictList;
    }
    public void setCriticalTypeDictList(List<Dict> criticalTypeDictList) {
        this.criticalTypeDictList = criticalTypeDictList;
    }

    /**
     * 饮食类型字典
     */
    private List<Dict> dietDictList;
    public List<Dict> getDietDictList() {
        return dietDictList;
    }
    public void setDietDictList(List<Dict> dietDictList) {
        this.dietDictList = dietDictList;
    }

    /**
     * 过敏类型字典
     */
    private List<Dict> allergyDictList;
    public List<Dict> getAllergyDictList() {
        return allergyDictList;
    }
    public void setAllergyDictList(List<Dict> allergyDictList) {
        this.allergyDictList = allergyDictList;
    }

    /**
     * 医保类型字典
     */
    private List<Dict> medicalInsuranceTypeDictList;
    public List<Dict> getMedicalInsuranceTypeDictList() {
        return medicalInsuranceTypeDictList;
    }
    public void setMedicalInsuranceTypeDictList(List<Dict> medicalInsuranceTypeDictList) {
        this.medicalInsuranceTypeDictList = medicalInsuranceTypeDictList;
    }

    /**
     * 防护类型字典
     */
    private List<Dict> protectionDictList;
    public List<Dict> getProtectionDictList() {
        return protectionDictList;
    }
    public void setProtectionDictList(List<Dict> protectionDictList) {
        this.protectionDictList = protectionDictList;
    }

    /**
     * 血型字典
     */
    private List<Dict> bloodTypeDictList;
    public List<Dict> getBloodTypeDictList() {
        return bloodTypeDictList;
    }
    public void setBloodTypeDictList(List<Dict> bloodTypeDictList) {
        this.bloodTypeDictList = bloodTypeDictList;
    }

    /**
     * 位置字典
     */
    private List<Dict> placeDictList;
    public List<Dict> getPlaceDictList() {
        return placeDictList;
    }
    public void setPlaceDictList(List<Dict> placeDictList) {
        this.placeDictList = placeDictList;
    }

    /**
     * 通过字典值找到对应的字典
     */
    @Nullable
    public static Dict findDictByValue(@NonNull List<Dict> totalDictList, Integer dictValue){
        if(dictValue == null){
            return null;
        }
        for(Dict dict : totalDictList){
            if(dict.getDictValue().equals(dictValue)){
                return dict;
            }
        }
        return null;
    }

    /**
     * 将id字符串转换成字典列表
     */
    @Nullable
    public static List<Dict> convertToDictList(List<Dict> totalDictList, String ids){
        if(StringUtil.isEmpty(ids)){
            return null;
        }
        Integer[] valueArray = StringUtil.ConvertStringToIds(ids);
        if(valueArray != null && valueArray.length > 0){
            List<Dict> dictList = new ArrayList<>();
            for(Integer index : valueArray){
                Dict dict = findDictByValue(totalDictList,index);
                if(dict != null){
                    dictList.add(dict);
                }
            }
            return dictList;
        }
        return null;
    }
}
