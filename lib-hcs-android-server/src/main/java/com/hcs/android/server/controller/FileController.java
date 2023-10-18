package com.hcs.android.server.controller;

import com.hcs.android.common.util.JsonUtils;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.server.entity.FileInfo;
import com.hcs.android.server.entity.RequestFile;
import com.hcs.android.server.web.AjaxResult;
import com.hcs.android.server.web.WebServer;
import com.yanzhenjie.andserver.annotation.Addition;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RequestMapping;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.framework.body.JsonBody;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;

import org.apache.httpcore.HttpException;

import java.io.File;
import java.io.IOException;

/**
 * 文件上传下载
 */
@RestController
@RequestMapping(path = "/file")
public class FileController extends BaseController {

    public FileController() {
        KLog.v("FileController init");
    }
    /**
     * 上传文件
     */
    @Addition(stringType = "needLogin", booleanType = true)
    @PostMapping(value = "/upload")
    public String upload( @RequestParam(name = "file") MultipartFile file) {
        try {
            FileInfo fileInfo = WebServer.getInstance().receiveFile(file);
            return AjaxResult.success("",fileInfo).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toAjax(false).toString();
    }

    /**
     * 下载文件
     */
    @Addition(stringType = "needLogin", booleanType = true)
    @PostMapping(value = "/download")
    public ResponseBody download(@RequestBody String requestData){
        RequestFile requestFile = JsonUtils.toObject(requestData,RequestFile.class);
        FileInfo fileInfo = WebServer.getInstance().getFile(requestFile);
        if(fileInfo != null) {
            if(fileInfo.isReady()) {
                return new FileBody(new File(fileInfo.getFilePath()));
            }else{
                return new JsonBody(AjaxResult.success("",fileInfo).toString());
            }
        }else{
            return new JsonBody(AjaxResult.error().toString());
        }


    }

}