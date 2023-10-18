package com.hcs.commondemo.viewmodel;

import android.app.Activity;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.network.NetworkManager;
import com.hcs.commondemo.entity.NetConfigBo;

public class NetConfigViewModel {

    private final static String NET_DEV_NAME = "eth0";
    private NetworkManager mNetworkManager;

    private NetConfigBo netConfigBo;

    private Activity mActivity;
    public void setNetConfigBo(NetConfigBo netConfigBo){
        this.netConfigBo = netConfigBo;
    }
    public NetConfigBo getNetConfigBo(){
        return netConfigBo;
    }

    public NetConfigViewModel(Activity activity) {
        mActivity = activity;
        mNetworkManager = new NetworkManager();
        netConfigBo = new NetConfigBo();
        netConfigBo.copy(mNetworkManager.getAddressInfo(activity.getApplicationContext(),NET_DEV_NAME));
    }

    public void saveConfig(){
        mNetworkManager.saveConfig(mActivity,netConfigBo.asNetConfig());
    }
}

