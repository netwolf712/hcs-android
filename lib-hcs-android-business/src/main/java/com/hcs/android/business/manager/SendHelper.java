package com.hcs.android.business.manager;

import androidx.annotation.NonNull;

import com.hcs.android.business.constant.CommandEnum;
import com.hcs.android.business.constant.Constant;
import com.hcs.android.business.constant.UpdateTypeEnum;
import com.hcs.android.business.entity.DeviceModel;
import com.hcs.android.business.entity.Place;
import com.hcs.android.business.entity.RequestDTO;
import com.hcs.android.business.entity.ResponseList;
import com.hcs.android.call.api.ChatHelper;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息发送助手
 */
public class SendHelper<T> {
    /**
     * 计算得到每条消息能发送的记录条数
     * （粗略计算）
     * @return 记录条数
     */
    private int getPageSize(List<T> dataList){
        int zoomSize =  (JsonUtils.toJsonString(dataList).length()  + Constant.MAX_MESSAGE_BODY_SIZE - 1) / Constant.MAX_MESSAGE_BODY_SIZE;
        return (dataList.size() + zoomSize - 1)/ zoomSize;
    }

    /**
     * 以流控的形式发送数据
     * （担心数据太大导致在网络中被拆包，如果是SIP消息就可能造成消息异常）
     * @param sendTo 数据接收方
     * @param sendFrom 数据发送方
     * @param dataList 数据列表
     * @param commandEnum 消息命令
     * @param updateType 更新模式
     */
    public void sendDataFlow(DeviceModel sendTo, DeviceModel sendFrom, @NonNull List<T> dataList, CommandEnum commandEnum,int updateType){
        List<ResponseList<T>> responseLists = packageData(dataList,updateType);
        if(!StringUtil.isEmpty(responseLists)){
            for(ResponseList<T> responseList : responseLists){
                RequestDTO<ResponseList<T>> requestDTO = new RequestDTO<>(sendFrom,sendTo, commandEnum);
                requestDTO.setData(responseList);
                ChatHelper.sendMessage(sendTo.getPhoneNumber(),requestDTO);
            }
        }
    }

    /**
     * 异步的形式发送数据
     * @param sendTo 数据接收方
     * @param sendFrom 数据发送方
     * @param dataList 数据列表
     * @param commandEnum 消息命令
     * @param updateType 更新模式
     * @param timeSpan 发送间隔，单位毫秒
     */
    public void sendDataFlowAsyn(DeviceModel sendTo, DeviceModel sendFrom, @NonNull List<T> dataList, CommandEnum commandEnum,int updateType,long timeSpan){
        List<ResponseList<T>> responseLists = packageData(dataList,updateType);
        if(!StringUtil.isEmpty(responseLists)){
            new Thread(()->{
                for(ResponseList<T> responseList : responseLists){
                    RequestDTO<ResponseList<T>> requestDTO = new RequestDTO<>(sendFrom,sendTo, commandEnum);
                    requestDTO.setData(responseList);
                    ChatHelper.sendMessage(sendTo.getPhoneNumber(),requestDTO);
                    if(timeSpan > 0){
                        try{
                            Thread.sleep(timeSpan);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

        }
    }

    /**
     * 只做数据打包，发送的事情还是交给调用者自己实现
     */
    public List<ResponseList<T>> packageData(@NonNull List<T> dataList, int updateType){
        int totalLength = dataList.size();
        if(totalLength == 0){
            //如果没有数据就别发了
            return null;
        }
        int currentPage = 0;
        int pageSize = getPageSize(dataList);
        List<ResponseList<T>> responseLists = new ArrayList<>();
        //每10条一个包开始发送
        for(int i = 0; i < totalLength; i += pageSize){
            int realSize = Math.min(pageSize,totalLength - currentPage * pageSize);
            List<T> subList = dataList.subList(i,i + realSize);
            ResponseList<T> tmpList = new ResponseList<>(subList);
            tmpList.setCurrentPage(currentPage);
            tmpList.setUpdateType(updateType);
            tmpList.setPageSize(pageSize);
            tmpList.setTotalLength(totalLength);
            responseLists.add(tmpList);
            currentPage++;
        }
        return responseLists;
    }

}
