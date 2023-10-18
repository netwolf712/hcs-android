package com.hcs.android.business.request.model;

import androidx.annotation.NonNull;

import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.RequestDTO;

/**
 * 请求的进一步封装
 */
public class RequestHelper {
    /**
     * 开始请求
     */
    public final static int REQUEST_STATUS_START = 0;

    /**
     * 请求返回
     */
    public final static int REQUEST_STATUS_RETURN = 1;

    /**
     * 请求错误
     */
    public final static int REQUEST_STATUS_ERROR = 2;

    /**
     * 请求完成
     */
    public final static int REQUEST_STATUS_COMPLETE = 3;

    private RequestModel mRequestModel;
    public RequestHelper(){
        mRequestModel = new RequestModel();
    }


    /**
     * 采用接口的形式接收数据
     */
    public interface IReceiveData{
        /**
         * 读取数据
         * @param res 返回数据
         */
        void onReadData(RequestDTO<Object> res);
    }

    /**
     * 接收状态改变接口
     */
    public interface IReceiveStatus{
        /**
         * 接收状态改变事件
         * @param status 0开始请求，1数据返回，2请求识别，3请求完成
         */
        void onReadStatusChange(int status);
    }

    /**
     * 获取设备列表
     * @param selfModel 本机信息
     * @param groupName 设备分组名称
     * @param currentPage 当前页
     * @param pageSize 每页大小
     * @param receiveData 数据接收回调
     * @param receiveStatus 数据接收状态回调
     */
    public void getDeviceList(DeviceModel selfModel, String groupName, int currentPage, int pageSize,IReceiveData receiveData, IReceiveStatus receiveStatus){
        if(receiveData == null){
            return;
        }
        //todo: 添加获取设备列表的服务器接口调用
    }

    public void updateDeviceInfo(@NonNull RequestDTO<DeviceModel> requestDTO){
        try {
            mRequestModel.updateDeviceInfo(requestDTO);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
