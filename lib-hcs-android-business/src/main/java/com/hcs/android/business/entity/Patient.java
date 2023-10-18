package com.hcs.android.business.entity;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import androidx.databinding.library.baseAdapters.BR;

@Entity
public class Patient extends BaseObservable {
    @PrimaryKey(autoGenerate = true)
    private Integer uid;
    public Integer getUid(){
        return uid;
    }
    public void setUid(Integer uid){
        this.uid = uid;
    }

    /**
     * 此病床绑定的主机id
     */
    @ColumnInfo(name = "master_device_id")
    private String masterDeviceId;
    public String getMasterDeviceId() {
        return masterDeviceId;
    }
    public void setMasterDeviceId(String masterDeviceId) {
        this.masterDeviceId = masterDeviceId;
    }

    /**
     * 病床序号，从1开始
     * 这个只能针对某个主机来说的
     */
    @ColumnInfo(name = "bed_sn")
    private Integer bedSn;
    public Integer getBedSn(){
        return bedSn;
    }
    public void setBedSn(Integer bedSn){
        this.bedSn = bedSn;
    }

    /**
     * 病员名称
     */
    @ColumnInfo(name = "name")
    private String name;
    @Bindable
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    /**
     * 病员编号
     */
    @ColumnInfo(name = "serial_number")
    private String serialNumber;
    @Bindable
    public String getSerialNumber(){
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber){
        this.serialNumber = serialNumber;
        notifyPropertyChanged(BR.serialNumber);
    }

    /**
     * 病员身份证号
     */
    @ColumnInfo(name = "id_card")
    private String idCard;
    @Bindable
    public String getIdCard(){
        return idCard;
    }
    public void setIdCard(String idCard){
        this.idCard = idCard;
        notifyPropertyChanged(BR.idCard);
    }

    /**
     * 性别（字典值）
     */
    @ColumnInfo(name = "sex")
    private Integer sex;
    public Integer getSex(){
        return sex;
    }
    public void setSex(Integer sex){
        this.sex = sex;
    }

    /**
     * 年龄
     */
    @ColumnInfo(name = "age")
    private Integer age;
    @Bindable
    public Integer getAge(){
        return age;
    }
    public void setAge(Integer age){
        this.age = age;
        notifyPropertyChanged(BR.age);
    }

    /**
     * 入院时间
     */
    @ColumnInfo(name = "in_hospital_time")
    private Long inHospitalTime;
    @Bindable
    public Long getInHospitalTime(){
        return inHospitalTime;
    }
    public void setInHospitalTime(Long inHospitalTime){
        this.inHospitalTime = inHospitalTime;
        notifyPropertyChanged(BR.inHospitalTime);
    }

    /**
     * 出院时间
     */
    @ColumnInfo(name = "out_hospital_time")
    private Long outHospitalTime;
    @Bindable
    public Long getOutHospitalTime(){
        return outHospitalTime;
    }
    public void setOutHospitalTime(Long outHospitalTime){
        this.outHospitalTime = outHospitalTime;
        notifyPropertyChanged(BR.outHospitalTime);
    }

    /**
     * 信息更新时间
     * 此可以作为设备上的人员信息是否需要更新的判断标准
     */
    @ColumnInfo(name = "update_time")
    private Long updateTime;
    public Long getUpdateTime(){
        return updateTime;
    }
    public void setUpdateTime(Long updateTime){
        this.updateTime = updateTime;
    }

    /**
     * 是否被删除
     */
    @ColumnInfo(name = "deleted")
    private Integer deleted;
    public Integer getDeleted(){
        return deleted;
    }
    public void setDeleted(Integer deleted){
        this.deleted = deleted;
    }

    /**
     * 病情
     */
    @ColumnInfo(name = "illness")
    private String illness;
    @Bindable
    public String getIllness(){
        return illness;
    }
    public void setIllness(String illness){
        this.illness = illness;
        notifyPropertyChanged(BR.illness);
    }

    /**
     * 医嘱
     */
    @ColumnInfo(name = "doctor_advice")
    private String doctorAdvice;
    @Bindable
    public String getDoctorAdvice(){
        return doctorAdvice;
    }
    public void setDoctorAdvice(String doctorAdvice){
        this.doctorAdvice = doctorAdvice;
        notifyPropertyChanged(BR.doctorAdvice);
    }

    /**
     * 计量（字典值）
     */
    @ColumnInfo(name = "metering")
    private Integer metering;
    @Bindable
    public Integer getMetering(){
        return metering;
    }
    public void setMetering(Integer metering){
        this.metering = metering;
        notifyPropertyChanged(BR.metering);
    }

    /**
     * 护理级别（字典值）
     */
    @ColumnInfo(name = "nursing_level")
    private Integer nursingLevel;
    @Bindable
    public Integer getNursingLevel(){
        return nursingLevel;
    }
    public void setNursingLevel(Integer nursingLevel){
        this.nursingLevel = nursingLevel;
        notifyPropertyChanged(BR.nursingLevel);
    }

    /**
     * 危重类型（字典值）
     */
    @ColumnInfo(name = "critical_type")
    private Integer criticalType;
    @Bindable
    public Integer getCriticalType(){
        return criticalType;
    }
    public void setCriticalType(Integer criticalType){
        this.criticalType = criticalType;
        notifyPropertyChanged(BR.criticalType);
    }

    /**
     * 是否需要隔离
     */
    @ColumnInfo(name = "isolate")
    private boolean isolate;
    @Bindable
    public boolean isIsolate(){
        return isolate;
    }
    public void setIsolate(boolean isolate){
        this.isolate = isolate;
        notifyPropertyChanged(BR.isolate);
    }

    /**
     * 饮食类型(字典值)
     */
    @ColumnInfo(name = "diet")
    private Integer diet;
    @Bindable
    public Integer getDiet(){
        return diet;
    }
    public void setDiet(Integer diet){
        this.diet = diet;
        notifyPropertyChanged(BR.diet);
    }

    /**
     * 过敏类型(字典值组成的字符串，每个值间用英文,隔开)
     */
    @ColumnInfo(name = "allergy")
    private String allergy;
    @Bindable
    public String getAllergy(){
        return allergy;
    }
    public void setAllergy(String allergy){
        this.allergy = allergy;
        notifyPropertyChanged(BR.allergy);
    }

    /**
     * 医保类型（字典值组成的字符串，每个值间用英文,隔开）
     */
    @ColumnInfo(name = "medical_insurance_type")
    private Integer medicalInsuranceType;
    @Bindable
    public Integer getMedicalInsuranceType(){
        return medicalInsuranceType;
    }
    public void setMedicalInsuranceType(Integer medicalInsuranceType){
        this.medicalInsuranceType = medicalInsuranceType;
        notifyPropertyChanged(BR.medicalInsuranceType);
    }

    /**
     * 防护内容（字典值组成的字符串，每个值间用英文,隔开）
     */
    @ColumnInfo(name = "protection")
    private String protection;
    @Bindable
    public String getProtection(){
        return protection;
    }
    public void setProtection(String protection){
        this.protection = protection;
        notifyPropertyChanged(BR.protection);
    }

    /**
     * 血型
     */
    @ColumnInfo(name = "blood_type")
    private Integer bloodType;
    public Integer getBloodType(){
        return bloodType;
    }
    public void setBloodType(Integer bloodType){
        this.bloodType = bloodType;
    }

    /**
     * 责任医生姓名
     */
    @ColumnInfo(name = "doctor_name")
    private String doctorName;
    @Bindable
    public String getDoctorName(){
        return doctorName;
    }
    public void setDoctorName(String doctorName){
        this.doctorName = doctorName;
        notifyPropertyChanged(BR.doctorName);
    }

    /**
     * 责任医生id
     */
    @ColumnInfo(name = "doctor_id")
    private Integer doctorId;
    public Integer getDoctorId(){
        return doctorId;
    }
    public void setDoctorId(Integer doctorId){
        this.doctorId = doctorId;
    }

    /**
     * 责任医生头像
     */
    @ColumnInfo(name = "doctor_image")
    private String doctorImage;
    @Bindable
    public String getDoctorImage(){
        return doctorImage;
    }
    public void setDoctorImage(String doctorImage){
        this.doctorImage = doctorImage;
        notifyPropertyChanged(BR.doctorImage);
    }

    /**
     * 责任护士姓名
     */
    @ColumnInfo(name = "nurse_name")
    private String nurseName;
    @Bindable
    public String getNurseName(){
        return nurseName;
    }
    public void setNurseName(String nurseName){
        this.nurseName = nurseName;
        notifyPropertyChanged(BR.nurseName);
    }

    /**
     * 责任护士id
     */
    @ColumnInfo(name = "nurse_id")
    private Integer nurseId;
    public Integer getNurseId(){
        return nurseId;
    }
    public void setNurseId(Integer nurseId){
        this.nurseId = nurseId;
    }

    /**
     * 责任护士头像
     */
    @ColumnInfo(name = "nurse_image")
    private String nurseImage;
    @Bindable
    public String getNurseImage(){
        return nurseImage;
    }
    public void setNurseImage(String nurseImage){
        this.nurseImage = nurseImage;
        notifyPropertyChanged(BR.nurseImage);
    }

    public void copy(@NonNull Patient patient){
        setUid(patient.getUid());
        setMasterDeviceId(patient.masterDeviceId);
        setBedSn(patient.getBedSn());
        setName(patient.getName());
        setSerialNumber(patient.getSerialNumber());
        setSex(patient.getSex());
        setAge(patient.getAge());
        setInHospitalTime(patient.getInHospitalTime());
        setOutHospitalTime(patient.getOutHospitalTime());
        setUpdateTime(patient.getUpdateTime());
        setDeleted(patient.getDeleted());
        setIllness(patient.getIllness());
        setDoctorAdvice(patient.getDoctorAdvice());
        setMetering(patient.getMetering());
        setNursingLevel(patient.getNursingLevel());
        setCriticalType(patient.getCriticalType());
        setIsolate(patient.isIsolate());
        setDiet(patient.getDiet());
        setAllergy(patient.getAllergy());
        setMedicalInsuranceType(patient.getMedicalInsuranceType());
        setProtection(patient.getProtection());
        setBloodType(patient.getBloodType());
        setDoctorName(patient.getDoctorName());
        setDoctorId(patient.getDoctorId());
        setDoctorImage(patient.getDoctorImage());
        setNurseName(patient.getNurseName());
        setNurseId(patient.getNurseId());
        setNurseImage(patient.getNurseImage());
    }
}
