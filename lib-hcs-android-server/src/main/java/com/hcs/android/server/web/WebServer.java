package com.hcs.android.server.web;

import android.content.Context;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.ISimpleCustomer;
import com.hcs.android.server.R;
import com.hcs.android.server.config.Config;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.server.controller.IWebController;
import com.hcs.android.server.entity.FileInfo;
import com.hcs.android.server.entity.RequestFile;
import com.hcs.android.server.util.AccessObjectUtil;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class WebServer {
    private Server andServer = null;

    private static final class InstanceHolder {
        static final WebServer instance = new WebServer();
    }

    public static WebServer getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * 初始化web服务
     */
    public WebServer(){
        try {
            Context context = BaseApplication.getAppContext();
            String serverAddress = SettingsHelper.getInstance(context).getString(Config.PREF_SERVER_ADDRESS,context.getString(R.string.default_hcs_server_server_address));
            int serverPort = SettingsHelper.getInstance(context).getInt(Config.PREF_SERVER_PORT,context.getResources().getInteger(R.integer.default_hcs_server_server_port));
            andServer = AndServer.webServer(context)
                    .inetAddress(InetAddress.getByName(serverAddress))  //服务器要监听的网络地址
                    .port(serverPort) //服务器要监听的端口
                    .timeout(SettingsHelper.getInstance(context).getInt(Config.PREF_SOCKET_TIMEOUT_TIME,R.integer.default_hcs_server_socket_timeout_time), TimeUnit.SECONDS) //Socket超时时间
                    .listener(new Server.ServerListener() {  //服务器监听接口
                        @Override
                        public void onStarted() {
                            KLog.i("web service started");
                        }

                        @Override
                        public void onStopped() {
                            KLog.i("web service stopped");
                        }

                        @Override
                        public void onException(Exception e) {

                        }
                    })
                    .build();
        }catch (Exception e){
            KLog.e(e);
        }
    }

    public void startServer(){
        if (andServer.isRunning()) {
        } else {
            andServer.startup();
            KLog.i("andServer startup!!");
        }
    }

    public void stopServer(){
        if (andServer.isRunning()) {
            andServer.shutdown();
            KLog.e("andServer shutdown!!");
        }
    }

    /**
     * 提供给第三方的web控制器
     */
    private IWebController webController = null;
    public void setWebController(IWebController webController){
        this.webController = webController;
    }

    public IWebController getWebController(){
        return webController;
    }

    /**
     * 提供给第三方的运维相关操作控制器
     */
    private IWebController maintainController = null;
    public void setMaintainController(IWebController webController){
        this.maintainController = webController;
    }
    public IWebController getMaintainController(){
        return maintainController;
    }

    /**
     * 文件上传结束之后的监听器
     * 客户端-->服务端
     */
    private IUploadListener mFileUploadListener;
    public void setFileUploadListener(IUploadListener fileUploadListener){
        mFileUploadListener = fileUploadListener;
    }
    public IUploadListener getFileUploadListener(){
        return mFileUploadListener;
    }

    /**
     * 文件上传成功
     * 服务器收到列表
     */
    public FileInfo receiveFile(MultipartFile file){
        if(mFileUploadListener != null){
            return mFileUploadListener.uploadFile(file);
        }
        return null;
    }
    /**
     * 文件下载监听器
     * 服务端-->客户端
     */
    private IDownloadListener mFileDownloadListener;

    public IDownloadListener getFileDownloadListener() {
        return mFileDownloadListener;
    }

    public void setFileDownloadListener(IDownloadListener fileDownloadListener) {
        this.mFileDownloadListener = fileDownloadListener;
    }

    public interface IDownloadListener{
        /**
         * 请求需要下载的文件
         * @param requestFile 请求参数
         * @return 可供下载的文件路径
         */
        FileInfo getFile(RequestFile requestFile);
    }

    public interface IUploadListener{
        /**
         * 上传文件
         * @param multipartFile 临时文件
         * @return 上传后保存的路径
         */
        FileInfo uploadFile(MultipartFile multipartFile);
    }
    /**
     * 获取可供下载的文件
     */
    public FileInfo getFile(RequestFile requestFile){
        if(mFileDownloadListener != null){
            return mFileDownloadListener.getFile(requestFile);
        }
        return null;
    }
}
