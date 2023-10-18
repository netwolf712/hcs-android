package com.hcs.android.business.entity;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.ObservableArrayList;

import java.util.List;
import androidx.databinding.library.baseAdapters.BR;
public class PlaceModel extends BaseObservable {

    public PlaceModel(){
        this.children = new ObservableArrayList<>();
        this.deviceModelList = new ObservableArrayList<>();
        this.patientModelList = new ObservableArrayList<>();
    }
    /**
     * 位置信息
     */
    private Place place;

    @Bindable
    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
        notifyPropertyChanged(BR.place);
    }

    /**
     * 此位置下挂的设备列表
     */
    private ObservableArrayList<DeviceModel> deviceModelList;

    public ObservableArrayList<DeviceModel> getDeviceModelList() {
        return deviceModelList;
    }

    public void setDeviceModelList(ObservableArrayList<DeviceModel> deviceModelList) {
        this.deviceModelList = deviceModelList;
    }

    /**
     * 子位置
     */
    private ObservableArrayList<PlaceModel> children;

    public ObservableArrayList<PlaceModel> getChildren() {
        return children;
    }

    public void setChildren(ObservableArrayList<PlaceModel> children) {
        this.children = children;
    }

    /**
     * 病人列表
     */
    private ObservableArrayList<PatientModel> patientModelList;

    public ObservableArrayList<PatientModel> getPatientModelList() {
        return patientModelList;
    }

    public void setPatientModelList(ObservableArrayList<PatientModel> patientModelList) {
        this.patientModelList = patientModelList;
    }

    /**
     * 网络摄像头
     */
    private IPCamera ipCamera;

    public IPCamera getIpCamera() {
        return ipCamera;
    }

    public void setIpCamera(IPCamera ipCamera) {
        this.ipCamera = ipCamera;
    }
}
