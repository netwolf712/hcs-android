package com.hcs.android.common.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.hcs.android.common.BaseApplication;
import com.hcs.android.common.util.log.KLog;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: <FileUtil><br>
 * Author:      mxdl<br>
 * Date:        2018/7/13<br>
 * Version:     V1.0.0<br>
 * Update:     <br>
 */
public class FileUtil {
    public static boolean isImageFile(String url){
        if(TextUtils.isEmpty(url)){
            return false;
        }
        String reg = ".+(\\.jpeg|\\.jpg|\\.gif|\\.bmp|\\.png).*";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(url.toLowerCase());
        return matcher.find();
    }
    public static boolean isVideoFile(String url){
        if(TextUtils.isEmpty(url)){
            return false;
        }
        String reg = ".+(\\.avi|\\.wmv|\\.mpeg|\\.mp4|\\.mp3|\\.mov|\\.mkv|\\.flv|\\.f4v|\\.m4v|\\.rm|\\.rmvb|\\.3gp|\\.dat|\\.ts|\\.mts|\\.vob).*";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(url.toLowerCase());
        return matcher.find();
    }
    public static boolean isUrl(String url){
        if(TextUtils.isEmpty(url)){
            return false;
        }
        String reg = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
        return url.matches(reg);
    }
    public static byte[] getFileByte(String filename) {
        File f = new File(filename);
        if (!f.exists()) {
            return null;
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            in.close();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getNameFromFilePath(String filePath) {
        String name = filePath;
        int i = filePath.lastIndexOf('/');
        if (i > 0) {
            name = filePath.substring(i + 1);
        }
        return name;
    }

    public static String getExtensionFromFileName(String fileName) {
        String extension = null;
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public static Boolean isExtensionImage(String path) {
        String extension = getExtensionFromFileName(path);
        if (extension != null) extension = extension.toLowerCase();
        return (extension != null && extension.matches("(png|jpg|jpeg|bmp|gif)"));
    }


    public static void recursiveFileRemoval(File root) {
        if (!root.delete()) {
            if (root.isDirectory()) {
                File[] files = root.listFiles();
                if (files != null) {
                    for (File f : files) {
                        recursiveFileRemoval(f);
                    }
                }
            }
        }
    }

    public static String getFilePath(final Context context, final Uri uri) {
        if (uri == null) return null;

        String result = null;
        String name = getNameFromUri(uri, context);

        try {
            File localFile = createFile(context, name);
            InputStream remoteFile = context.getContentResolver().openInputStream(uri);

            if (copyToFile(remoteFile, localFile)) {
                result = localFile.getAbsolutePath();
            }

            remoteFile.close();
        } catch (IOException e) {
            KLog.e("Enable to get sharing file", e);
        }

        return result;
    }

    private static String getNameFromUri(Uri uri, Context context) {
        String name = null;
        if (uri.getScheme().equals("content")) {
            Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);
            if (returnCursor != null) {
                returnCursor.moveToFirst();
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                name = returnCursor.getString(nameIndex);
                returnCursor.close();
            }
        } else if (uri.getScheme().equals("file")) {
            name = uri.getLastPathSegment();
        }
        return name;
    }

    /**
     * Copy data from a source stream to destFile. Return true if succeed, return false if failed.
     */
    private static boolean copyToFile(InputStream inputStream, File destFile) {
        if (inputStream == null || destFile == null) return false;
        try {
            try (OutputStream out = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static File createFile(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) fileName = getStartDate();

        if (!fileName.contains(".")) {
            fileName = fileName + ".unknown";
        }

        final File root;
        root = context.getExternalCacheDir();

        if (root != null && !root.exists()) root.mkdirs();
        return new File(root, fileName);
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String result = cursor.getString(column_index);
            cursor.close();
            return result;
        }
        return null;
    }


    private static String getContactNameFromVcard(String vcard) {
        if (vcard != null) {
            String contactName = vcard.substring(vcard.indexOf("FN:") + 3);
            contactName = contactName.substring(0, contactName.indexOf("\n") - 1);
            contactName = contactName.replace(";", "");
            contactName = contactName.replace(" ", "");
            return contactName;
        }
        return null;
    }

    private static String getStartDate() {
        try {
            return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
        } catch (RuntimeException e) {
            return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        }
    }

    public static String getMimeFromFile(String path) {
        if (isExtensionImage(path)) {
            return "image/" + getExtensionFromFileName(path);
        }
        return "file/" + getExtensionFromFileName(path);
    }

    /**
     * 文件重命名
     * @param orgFileName 原名
     * @param dstFileName 新名
     * @return 是否重命名成功
     */
    public static boolean renameFile(String orgFileName,String dstFileName){
        File orgFile = new File(orgFileName);
        return orgFile.renameTo(new File(dstFileName));
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        // 如果存在，是文件则返回 true，是目录则返回 false
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(final File file) {
        // 如果存在，是目录则返回 true，是文件则返回 false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 将文件大小转换为可读模式
     * @param size 原始大小，单位B
     * @return 转换后的字符串
     */
    public static String getReadableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#").format(size / Math.pow(1024, digitGroups)) + units[digitGroups];
    }

    /**
     * 获取文件长度
     *
     * @param file 文件
     * @return 文件长度
     */
    public static long getFileLength(final File file) {
        if (!isFile(file)) return -1;
        return file.length();
    }

    /**
     * 判断是否是文件
     *
     * @param file 文件
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }
    public static class FileComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            if(f1 == f2) {
                return 0;
            }
            if(f1.isDirectory() && f2.isFile()) {
                // Show directories above files
                return -1;
            }
            if(f1.isFile() && f2.isDirectory()) {
                // Show files below directories
                return 1;
            }
            // Sort the directories alphabetically
            return f1.getName().compareToIgnoreCase(f2.getName());
        }
    }
    /**
     * 查找目录下的指定的文件列表
     * @param path 需要遍历的目录（只遍历根目录）
     * @param filter 文件过滤器
     * @return 文件列表
     */
    public static List<File> getFileListByDirPath(String path, FileFilter filter) {
        File directory = new File(path);
        File[] files = directory.listFiles(filter);
        List<File> result = new ArrayList<>();
        if (files == null) {
            return new ArrayList<>();
        }

        for (int i = 0; i < files.length; i++) {
            result.add(files[i]);
        }
        Collections.sort(result, new FileComparator());
        return result;
    }

    /**
     * 返回图片文件过滤器
     */
    public static FileFilter getImageFileFilter(){
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(isImageFile(file.getAbsolutePath())){
                    return true;
                }
                return false;
            }
        };
        return fileFilter;
    }

    /**
     * 返回音/视频文件过滤器
     */
    public static FileFilter getVideoFileFilter(){
        FileFilter fileFilter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                if(isVideoFile(file.getAbsolutePath())){
                    return true;
                }
                return false;
            }
        };
        return fileFilter;
    }

    /**
     * 获取app的文件路径
     */
    public static String getAppFileDir(){
        String fileDir =
                Environment.getExternalStorageDirectory()
                        + "/"
                        + BaseApplication.getAppContext().getString(
                        BaseApplication.getAppContext().getResources()
                                .getIdentifier(
                                        "app_name", "string", BaseApplication.getAppContext().getPackageName()));
        return fileDir;
    }

    /**
     * 获取app数据区的路径
     */
    @SuppressLint("SdCardPath")
    public static String getAppDataDir(){
        return  "/data/user/0/" + BaseApplication.getAppContext().getPackageName();
    }

    public static void copyIfNotExist(Context context,int resourceId, String target) throws IOException {
        File lFileToCopy = new File(target);
        if (!lFileToCopy.exists()) {
            copyFromPackage(context,resourceId, lFileToCopy.getName());
        }
    }

    public static void copyFromPackage(@NonNull Context context, int resourceId, String target) throws IOException {
        FileOutputStream lOutputStream = context.openFileOutput(target, 0);
        InputStream lInputStream = context.getResources().openRawResource(resourceId);
        int readByte;
        byte[] buff = new byte[8048];
        while ((readByte = lInputStream.read(buff)) != -1) {
            lOutputStream.write(buff, 0, readByte);
        }
        lOutputStream.flush();
        lOutputStream.close();
        lInputStream.close();
    }
}
