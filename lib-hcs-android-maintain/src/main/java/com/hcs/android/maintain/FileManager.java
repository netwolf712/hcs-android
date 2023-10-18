package com.hcs.android.maintain;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.settings.SettingsHelper;
import com.hcs.android.common.util.DateUtil;
import com.hcs.android.common.util.ExeCommand;
import com.hcs.android.common.util.FileUtil;
import com.hcs.android.common.util.FileZipParam;
import com.hcs.android.common.util.RobustTimer;
import com.hcs.android.common.util.RobustTimerTask;
import com.hcs.android.common.util.StringUtil;
import com.hcs.android.common.util.ZipFileUtil;
import com.hcs.android.common.util.log.KLog;
import com.hcs.android.maintain.constant.FileTypeEnum;
import com.hcs.android.maintain.constant.PreferenceConstant;
import com.hcs.android.maintain.entity.RequestFileList;
import com.hcs.android.maintain.entity.RequestOpFile;
import com.hcs.android.maintain.entity.RequestRecoverConfig;
import com.hcs.android.server.entity.FileInfo;
import com.hcs.android.server.entity.RequestFile;
import com.hcs.android.server.web.WebServer;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;

import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 文件管理
 */
public class FileManager {

    /**
     * 打包状态，主要用于异步下载
     */
    public enum ZipStatus{
        /**
         * 打包中
         */
        ZIPPING,
        /**
         * 打包结束
         */
        ZIPPED
    }
    private final Context mContext;
    /**
     * 日志文件的主类型
     */
    private final static String LOG_FILE_MAIN_TYPE = "log";

    /**
     * 备份文件的文件类型
     */
    private final static String BACKUP_FILE_MAIN_TYPE = "other";
    private final static String BACKUP_FILE_SUB_TYPE = "backup";

    /**
     * 文件缓存路径
     */
    private final static String TEMP_FILE_CACHE = "/download_file_cache/";

    /**
     * 数据库存放相对目录
     */
    private final static String DATABASE_FOLDER = "/databases";

    /**
     * linphone数据库存放相对目录
     */
    private final static String OTHER_FILE_FOLDER = "/files";

    /**
     * shared_prefs配置存放相对目录
     */
    private final static String SHARED_PREFS_FOLDER = "/shared_prefs";

    /**
     * 存放截屏图片的目录
     */
    private final static String SCREEN_CAPTURE_FOLDER = "/screen_capture";

    /**
     * 截屏图片后缀名
     */
    private final static String SCREEN_CAPTURE_SUFFIX = ".png";

    /**
     * 存放打包文件的映射
     */
    private final Map<String,ZipStatus> mZipFileMap;
    /**
     * 名字分隔符
     */
    private final static String NAME_SEPARATOR = "-";

    private final static String FILE_SEPARATOR = "/";
    /**
     * 缓存清除定时器
     */
    private RobustTimer mCacheCleanTimer;

    public FileManager(Context context){
        mContext = context;
        mZipFileMap = new HashMap<>();
    }

    private static final class MInstanceHolder {
        @SuppressLint("StaticFieldLeak")
        static final FileManager mInstance = new FileManager(BaseApplication.getAppContext());
    }

    public static FileManager getInstance(){
        return MInstanceHolder.mInstance;
    }
    /**
     * 开启文件管理器
     */
    public void start(){
        //开启缓存管理器
        startCacheTimer();
        //注册下载回调
        WebServer.getInstance().setFileDownloadListener(requestFile -> getDownloadFileInfo(requestFile,true));
        //注册上传回调
        WebServer.getInstance().setFileUploadListener(this::saveUploadFile);
    }

    /**
     * 关闭文件管理器
     */
    public void stop(){
        stopCacheTimer();
        WebServer.getInstance().setFileDownloadListener(null);
    }

    /**
     * 获取临时缓存的路径
     */
    @NonNull
    private String getTempFileCacheDir(){
        return FileUtil.getAppFileDir() + TEMP_FILE_CACHE;
    }

    /**
     * 获取压缩包文件路径
     */
    @NonNull
    private String getZipFilePath(@NonNull RequestFile requestFile){
        String date = DateUtil.formatDate(new Date(), DateUtil.FormatType.yyyyMMdd_HHmmss);
        return getTempFileCacheDir() + getZipFileName(requestFile);
    }
    @NonNull
    private String getZipFileName(@NonNull RequestFile requestFile){
        String date = DateUtil.formatDate(new Date(), DateUtil.FormatType.yyyyMMdd_HHmmss);
        return requestFile.getSubType() + NAME_SEPARATOR + date + ZipFileUtil.ZIP_FILE_SUFFIX;
    }
    /**
     * 获取下载文件
     * @param requestFile 请求参数
     * @return 下载路径
     */
    public String getDownloadFile(@NonNull RequestFile requestFile){
        //检查此文件是否正在打包中
        if(!StringUtil.isEmpty(requestFile.getFileList())){
            synchronized (mZipFileMap){
                ZipStatus zipStatus = mZipFileMap.get(requestFile.getFileList().get(0));
                if(zipStatus != null){
                    return requestFile.getFileList().get(0);
                }
            }
        }
        String tempFileDir = getTempFileCacheDir();
        FileUtil.createOrExistsDir(new File(tempFileDir));
        //在生成文件前先删除超出缓存大小的文件
        clearOutOfMemoryCache();
        if(StringUtil.equalsIgnoreCase(requestFile.getMainType(),LOG_FILE_MAIN_TYPE)){
            //如果是日志文件，则直接整个目录下载
            String zipFilePath = getZipFilePath(requestFile);
            synchronized (mZipFileMap){
                mZipFileMap.put(zipFilePath,ZipStatus.ZIPPING);
            }
            new Thread(()->{
                try {
                    if (FileTypeEnum.LOG_PCAP.getSubType().equalsIgnoreCase(requestFile.getSubType())) {
                        ZipFileUtil.zipFolder(LogManager.getInstance().getPcapLogDir(), zipFilePath);
                    }else if (FileTypeEnum.LOG_LOGCAT.getSubType().equalsIgnoreCase(requestFile.getSubType())) {
                        ZipFileUtil.zipFolder(LogManager.getInstance().getLogcatLogDir(), zipFilePath);
                    }else if (FileTypeEnum.LOG_DMESG.getSubType().equalsIgnoreCase(requestFile.getSubType())) {
                        ZipFileUtil.zipFolder(LogManager.getInstance().getDmesgLogDir(), zipFilePath);
                    }
                    synchronized (mZipFileMap){
                        mZipFileMap.put(zipFilePath,ZipStatus.ZIPPED);
                    }
                }catch (Exception e){
                    KLog.e(e);
                }
            }).start();
            return zipFilePath;
        }else if(BACKUP_FILE_MAIN_TYPE.equalsIgnoreCase(requestFile.getMainType()) && BACKUP_FILE_SUB_TYPE.equalsIgnoreCase(requestFile.getSubType())){
          //如果是备份文件，则整个data根目录打包下载
            String zipFilePath = getZipFilePath(requestFile);
            synchronized (mZipFileMap){
                mZipFileMap.put(zipFilePath,ZipStatus.ZIPPING);
            }
            new Thread(()->{
                try{
                List<FileZipParam> fileZipParams = new ArrayList<>();
                //备份数据库
                fileZipParams.add(new FileZipParam(FileUtil.getAppDataDir() + DATABASE_FOLDER));
                //备份配置
                fileZipParams.add(new FileZipParam(FileUtil.getAppDataDir() + SHARED_PREFS_FOLDER));
                    ZipFileUtil.zipFolders(fileZipParams, zipFilePath);
                    synchronized (mZipFileMap){
                        mZipFileMap.put(zipFilePath,ZipStatus.ZIPPED);
                    }
                }catch (Exception e){
                    KLog.e(e);
                }
            }).start();

            return zipFilePath;
        } else{
            if(!StringUtil.isEmpty(requestFile.getFileList())){
                if(requestFile.getFileList().size() == 1 && !requestFile.isZipAll()){
                    //如果只有一个文件，且不用打包，则直接返回文件路径
                    synchronized (mZipFileMap){
                        mZipFileMap.put(requestFile.getFileList().get(0),ZipStatus.ZIPPED);
                    }
                    return requestFile.getFileList().get(0);
                }else {
                    //否则需要打包
                    String zipFilePath = getZipFilePath(requestFile);
                    synchronized (mZipFileMap){
                        mZipFileMap.put(zipFilePath,ZipStatus.ZIPPING);
                    }
                    new Thread(()->{
                        for(String filePath : requestFile.getFileList()){
                            try {
                                ZipFileUtil.generateFile(filePath, getTempFileCacheDir(), getZipFileName(requestFile));
                                synchronized (mZipFileMap){
                                    mZipFileMap.put(zipFilePath,ZipStatus.ZIPPED);
                                }
                            }catch (Exception e){
                                KLog.e(e);
                            }
                        }
                    }).start();

                    return zipFilePath;
                }
            }
        }
        return "";
    }

    /**
     * 获取要下载的文件
     * @param requestFile 下载参数
     * @param download 是否真正下载
     * @return 下载的文件信息
     */
    public FileInfo getDownloadFileInfo(RequestFile requestFile,boolean download){
        String filePath = getDownloadFile(requestFile);
        if(!StringUtil.isEmpty(filePath)){
            FileInfo fileInfo = new FileInfo();
            fileInfo.setName(FileUtil.getNameFromFilePath(filePath));
            fileInfo.setFilePath(filePath);
            fileInfo.setDir(false);
            fileInfo.setLength(FileUtil.getFileLength(new File(filePath)));
            synchronized (mZipFileMap){
                ZipStatus zipStatus = mZipFileMap.get(filePath);
                fileInfo.setReady(zipStatus != ZipStatus.ZIPPING);
                if(zipStatus == ZipStatus.ZIPPED && download){
                    mZipFileMap.remove(filePath);
                }
            }
            return fileInfo;
        }else{
            return null;
        }
    }

    /**
     * 开启缓存管理定时器
     */
    private void startCacheTimer(){
        if(mCacheCleanTimer == null){
            mCacheCleanTimer = new RobustTimer(true);
            RobustTimerTask timerTask = new RobustTimerTask() {
                @Override
                public void run() {
                    //执行文件清理操作
                    clearTimeoutCache();
                }
            };
            int timeSpan = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PREF_KEY_CACHE_KEEP_TIME,mContext.getResources().getInteger(R.integer.default_maintain_cache_keep_time));
            mCacheCleanTimer.schedule(timerTask, 0, timeSpan);
        }
    }

    /**
     * 关闭缓存管理定时器
     */
    private void stopCacheTimer(){
        if(mCacheCleanTimer != null){
            mCacheCleanTimer.cancel();
            mCacheCleanTimer = null;
        }
    }

    /**
     * 查找目录下的资源包列表
     * @param dir 需要遍历的目录（只遍历根目录）
     * @return 资源包列表
     */
    @Nullable
    private File[] findFiles(String dir){
        File file = new File(dir);
        File[] files = file.listFiles();
        if(files==null){
            return null;
        } else if(files.length!=0) {
            //按文件的修改日期递减排序
            Arrays.sort(files, (f1, f2) -> {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return -1;
                else if (diff == 0)
                    return 0;
                else
                    return 1;
            });
            return files;
        }
        return null;
    }

    /**
     * 清理超时的缓存
     */
    private void clearTimeoutCache(){
        File[] files = findFiles(getTempFileCacheDir());
        if(files != null && files.length > 0){
            int timeSpan = SettingsHelper.getInstance(mContext).getInt(PreferenceConstant.PREF_KEY_CACHE_KEEP_TIME,mContext.getResources().getInteger(R.integer.default_maintain_cache_keep_time));
            long passTime = System.currentTimeMillis() - timeSpan;
            for(File file : files){
                if(file.isFile()){
                    try{
                        synchronized (mZipFileMap){
                            //如果还在打包的，则不要删除了。
                            ZipStatus zipStatus = mZipFileMap.get(file.getAbsolutePath());
                            if(zipStatus == ZipStatus.ZIPPING){
                                continue;
                            }
                        }
                        long fileTime = file.lastModified();
                        if(passTime >= fileTime){
                            mZipFileMap.remove(file.getAbsolutePath());
                            ExeCommand.executeSuCmd("rm \"" + file.getAbsolutePath() + "\"");
                        }else{
                            break;
                        }
                    }catch (Exception e){
                        KLog.e(e);
                    }
                }
            }
        }
    }

    /**
     * 获取文件大小
     */
    private long getTotalSize(File[] files){
        long fileSize = 0L;
        if(files != null && files.length > 0){
            for(File file : files){
                fileSize += file.length();
            }
        }
        return fileSize;
    }
    /**
     * 清理超出缓存的文件
     */
    private void clearOutOfMemoryCache(){
        File[] files = findFiles(getTempFileCacheDir());
        if(files != null && files.length > 0){
            long fileSize = getTotalSize(files);
            long maxFileSize = SettingsHelper.getInstance(mContext).getLong(PreferenceConstant.PREF_KEY_MAX_CACHE_SIZE,mContext.getResources().getInteger(R.integer.default_maintain_max_cache_size));
            if(fileSize > maxFileSize){
                for(File file : files){
                    synchronized (mZipFileMap){
                        //如果还在打包的，则不要删除了。
                        ZipStatus zipStatus = mZipFileMap.get(file.getAbsolutePath());
                        if(zipStatus == ZipStatus.ZIPPING){
                            continue;
                        }
                    }
                    fileSize -= file.length();
                    mZipFileMap.remove(file.getAbsolutePath());
                    ExeCommand.executeSuCmd("rm \"" + file.getAbsolutePath() + "\"");
                    if(fileSize < maxFileSize){
                        break;
                    }
                }
            }
        }
    }

    /**
     * 获取保存上传文件的目录
     */
    @NonNull
    private String getUploadDir(){
        String date = DateUtil.formatDate(new Date(), DateUtil.FormatType.yyyyMMdd);
        String basePath = FileUtil.getAppFileDir() + FILE_SEPARATOR + SettingsHelper.getInstance(mContext).getString(PreferenceConstant.PREF_KEY_UPLOAD_FILE_PATH,mContext.getString(R.string.default_maintain_upload_file_path)) + FILE_SEPARATOR;
        return basePath + date;
    }

    /**
     * 将上传的文件移动到指定位置
     * @param multipartFile 临时文件
     * @return 移动后的文件路径
     */
    public FileInfo saveUploadFile(@NonNull MultipartFile multipartFile){
        String uploadDir = getUploadDir();
        FileUtil.createOrExistsDir(new File(uploadDir));
        String date = DateUtil.formatDate(new Date(), DateUtil.FormatType.yyyyMMdd_HHmmss);
        String filePath = uploadDir + FILE_SEPARATOR + date + "." + FileUtil.getExtensionFromFileName(Objects.requireNonNull(multipartFile.getFilename()));
        File file = new File(filePath);
        try {
            multipartFile.transferTo(file);
        }catch (Exception e){
            KLog.e(e);
        }
        return new FileInfo(multipartFile.getFilename(),filePath,file.length(),false);
    }

    /**
     * 根据文件类型获取文件过滤器
     */
    @NonNull
    @Contract(pure = true)
    private FileFilter getFileFilter(RequestFileList requestFileList){
        return file -> {
            if(StringUtil.isEmpty(requestFileList.getMainType())){
                if(requestFileList.isWithDir()) {
                    //类型为空且包含目录，则返回所有
                    return true;
                }else{
                    return !file.isDirectory();
                }
            }
            FileTypeEnum fileTypeEnum = FileTypeEnum.find(requestFileList.getMainType(),requestFileList.getSubType());
            switch (fileTypeEnum){
                case OTHER_ANY:
                    return true;
                case LOG_PCAP:
                case LOG_LOGCAT:
                case LOG_DMESG:
                    if(file.isFile() && file.getAbsolutePath().contains(requestFileList.getSubType())){
                        return true;
                    }else return file.isDirectory() && requestFileList.isWithDir();
                case MEDIA_AUDIO:
                case MEDIA_VIDEO:
                    if(file.isFile() && FileUtil.isVideoFile(file.getAbsolutePath())){
                        return true;
                    }else return file.isDirectory() && requestFileList.isWithDir();
                case MEDIA_IMAGE:
                    if(file.isFile() && FileUtil.isImageFile(file.getAbsolutePath())){
                        return true;
                    }else return file.isDirectory() && requestFileList.isWithDir();
                case OTHER_DATABASE:
                    if(file.isFile() && file.getAbsolutePath().contains(".db")){
                        return true;
                    }else return file.isDirectory() && requestFileList.isWithDir();
            }
            return false;
        };
    }
    public List<FileInfo> getFileList(RequestFileList requestFileList){
        List<FileInfo> fileInfoList = new ArrayList<>();
        if(requestFileList != null && !StringUtil.isEmpty(requestFileList.getDir())){
            String absPath = FileUtil.getAppFileDir() + FILE_SEPARATOR + requestFileList.getDir();
            List<File> fileList = FileUtil.getFileListByDirPath(absPath,getFileFilter(requestFileList));
            if(!StringUtil.isEmpty(fileList)){
                for(File file : fileList){
                    FileInfo fileInfo = new FileInfo(file.getName(),file.getAbsolutePath(),file.length(),file.isDirectory());
                    fileInfoList.add(fileInfo);
                }
            }
        }
        return fileInfoList;
    }

    /**
     * 恢复配置
     * （直接把压缩包解压回数据区域）
     */
    public void recoverConfig(@NonNull RequestRecoverConfig requestRecoverConfig){
        if(FileUtil.isFile(new File(requestRecoverConfig.getRecoverFilePath()))){
            try {
                ZipFileUtil.unzipFiles(requestRecoverConfig.getRecoverFilePath(), FileUtil.getAppDataDir());
                //删除压缩包
                ExeCommand.executeSuCmd("rm \"" + requestRecoverConfig.getRecoverFilePath() + "\"");
            }catch (Exception e){
                KLog.e(e);
            }
        }
    }

    /**
     * 获取截屏文件的存放目录
     */
    @NonNull
    private String getScreenCaptureFolder(){
        return FileUtil.getAppFileDir() + SCREEN_CAPTURE_FOLDER;
    }
    /**
     * 获取截屏文件的路径
     */
    @NonNull
    private String getScreenCaptureFileName(){
        String date = DateUtil.formatDate(new Date(), DateUtil.FormatType.yyyyMMdd_HHmmss);
        String folder = getScreenCaptureFolder();
        FileUtil.createOrExistsDir(new File(folder));
        return folder + FILE_SEPARATOR + date + SCREEN_CAPTURE_SUFFIX;
    }
    /**
     * 截屏操作
     */
    public FileInfo handleScreenCapture(){
        //通过命令行的形式生成截屏数据
        String fileName = getScreenCaptureFileName();
        ExeCommand.executeSuCmd("screencap \"" + fileName + "\"");
        File file = new File(fileName);
        return new FileInfo(file.getName(),fileName,file.length(),false);
    }

    public void handleFiles(@NonNull RequestOpFile requestOpFile){
        List<String> fileList = requestOpFile.getSrcFileList();
        if(!StringUtil.isEmpty(fileList)){
            for(String filePath : fileList){
                StringBuilder cmd = new StringBuilder();
                if(requestOpFile.getOperateType() == RequestOpFile.OPERATE_TYPE_MOVE) {
                    cmd.append("mv ");
                    if(StringUtil.isEmpty(requestOpFile.getDstFolder())){
                        return;
                    }
                }else if(requestOpFile.getOperateType() == RequestOpFile.OPERATE_TYPE_COPY) {
                    cmd.append("cp ");
                    if(StringUtil.isEmpty(requestOpFile.getDstFolder())){
                        return;
                    }
                }else{
                    cmd.append("rm ");
                }

                File file = new File(filePath);
                if(file.isDirectory()){
                    cmd.append("-rf ");
                }
                cmd.append(" \"");
                cmd.append(filePath);
                cmd.append(" \"");
                if(requestOpFile.getOperateType() == RequestOpFile.OPERATE_TYPE_MOVE
                || requestOpFile.getOperateType() == RequestOpFile.OPERATE_TYPE_COPY){
                    //移动和拷贝都需要目的地址
                    cmd.append(" \"");
                    cmd.append(requestOpFile.getDstFolder());
                    cmd.append(" \"");
                }
                ExeCommand.executeSuCmd(cmd.toString());
            }
        }
    }
}
