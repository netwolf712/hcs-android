package com.hcs.android.onvif.onvif;

import android.content.Context;


import androidx.annotation.NonNull;

import com.hcs.android.onvif.onvifBean.Digest;
import com.hcs.android.onvif.onvifBean.OnvifDevice;
import com.hcs.android.onvif.util.Gsoap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnvifUtils {

    /**
     * 通过用户名/密码/assets 文件获取对应需要发送的String
     * @param fileName assets文件名
     * @param context context
     * @param device onvif设备
     * @param needDigest 是否需要鉴权
     * @param params XML 参数
     * @return 需要发送的 string
     */

    public static String getPostString(String fileName, Context context, OnvifDevice device, boolean needDigest, String... params) throws IOException {
        //读取文件内容
        String postString = "";
        InputStream is = context.getAssets().open(fileName);
        byte[] postData = new byte[is.available()];
        if (is.read(postData) > 0) {
            postString = new String(postData, "utf-8");
        }
        if(needDigest){
            //获取digest
            Digest digest = Gsoap.getDigest(device.getUsername(), device.getPassword());
            //需要digest
            if (digest != null) {
                if (params.length > 0) {
                    List<String> listParams = new ArrayList<>();
                    listParams.add(digest.getUserName());
                    listParams.add(digest.getEncodePsw());
                    listParams.add(digest.getNonce());
                    listParams.add(digest.getCreatedTime());
                    listParams.addAll(Arrays.asList(params));
//                postString = String.format(postString, digest.getUserName(),
//                        digest.getEncodePsw(), digest.getNonce(), digest.getCreatedTime(), params[0]);
                    postString = String.format(postString, listParams.toArray());
                } else {
                    postString = String.format(postString, digest.getUserName(),
                            digest.getEncodePsw(), digest.getNonce(), digest.getCreatedTime());
                }

            }
        } else {
            postString = String.format(postString, Arrays.asList(params).toArray());
        }
        return postString;
    }

    /**
     * 转换RTSP的地址，加上用户名、密码，使播放器能直接播放
     * @param rtspUrl RTSP的地址 rtsp://xxx.xxxx
     * @param username onvif用户名
     * @param password onvif密码
     * @return 组合后的地址
     */
    @NonNull
    public static String getVideoStream(@NonNull String rtspUrl, String username, String password){
        return rtspUrl.replace("rtsp://", String.format("rtsp://%s:%s@", username, password));
    }
}
