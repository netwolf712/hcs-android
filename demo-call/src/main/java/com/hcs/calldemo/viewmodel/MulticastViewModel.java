package com.hcs.calldemo.viewmodel;

import android.view.SurfaceView;

import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.ToastUtil;
import com.hcs.calldemo.R;
import com.hcs.calldemo.entity.MulticastBo;

import java.util.ArrayList;
import java.util.List;

/**
 * 广播测试
 */
public class MulticastViewModel{

    /**
     * 广播端口
     */
    private final static int MULTICAST_PORT = 60000;
    private List<Long> mPlayObjects = new ArrayList<>();
    private MulticastBo multicastBo;
    public MulticastBo getMulticastBo(){
        return multicastBo;
    }
    public void setMulticastBo(MulticastBo multicastBo){
        this.multicastBo = multicastBo;
    }

    public MulticastViewModel(){
        multicastBo = new MulticastBo();
        multicastBo.setAddress("237.2.4.5");
        multicastBo.setFilePath("/data/data/com.hcs.calldemo/test3.mp4");
    }
    public void startPlay(SurfaceView surfaceView){
        if(StringUtil.isEmpty(multicastBo.getAddress())){
            ToastUtil.showToast(R.string.multicast_address_can_not_empty);
            return;
        }
        if(StringUtil.isEmpty(multicastBo.getFilePath())){
            ToastUtil.showToast(R.string.multicast_filepath_can_not_empty);
            return;
        }
    }
    public void stopPlay(){
        if(!StringUtil.isEmpty(mPlayObjects)){
            mPlayObjects.clear();
        }
    }

    public void startMulticast(){
        if(StringUtil.isEmpty(multicastBo.getAddress())){
            ToastUtil.showToast(R.string.multicast_address_can_not_empty);
            return;
        }
    }

    public void stopMulticast(){
    }
}

