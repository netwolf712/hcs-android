package com.hcs.android.maintain.controller;

import com.hcs.android.annotation.annotation.CommandId;
import com.hcs.android.annotation.annotation.CommandMapping;
import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.maintain.FileManager;
import com.hcs.android.maintain.entity.RequestDTO;
import com.hcs.android.maintain.entity.RequestFileList;
import com.hcs.android.maintain.entity.RequestOpFile;
import com.hcs.android.server.entity.RequestFile;
import com.hcs.android.server.web.AjaxResult;


/**
 * 文件
 */
@CommandMapping
public class FileController {
    /**
     * 请求获取文件列表
     */
    @CommandId("maintain-req-get-file-list")
    public AjaxResult getFileList(String str){
        RequestDTO<RequestFileList> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestFileList.class});
        return AjaxResult.success("", FileManager.getInstance().getFileList(requestDTO.getData()));
    }

    /**
     * 文件打包
     */
    @CommandId("maintain-req-package-file")
    public AjaxResult packageFile(String str){
        RequestDTO<RequestFile> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestFile.class});
        return AjaxResult.success("", FileManager.getInstance().getDownloadFileInfo(requestDTO.getData(),false));
    }

    /**
     * 文件操作
     */
    @CommandId("maintain-req-operate-file")
    public AjaxResult operateFile(String str){
        RequestDTO<RequestOpFile> requestDTO = JsonUtils.toObject(str,new Class[]{RequestDTO.class,RequestOpFile.class});
        FileManager.getInstance().handleFiles(requestDTO.getData());
        return AjaxResult.success("");
    }
}
