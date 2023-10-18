package com.hcs.android.common.util;

import android.text.TextUtils;


import com.hcs.android.common.util.log.KLog;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 压缩包助手
 */
public class ZipFileUtil {
    /**
     * 压缩文件的后缀名
     */
    public final static String ZIP_FILE_SUFFIX = ".zip";

    /**
     * 根据文件名从压缩包中读取数据流
     * @param zipFile 压缩包
     * @param fileName 需要读取的文件名
     * @return 文件数据
     * @throws Exception 异常
     */
    public static BufferedReader readZipFile(String zipFile,String fileName) throws Exception {
        ZipFile zf = new ZipFile(zipFile);
        ZipEntry ze = zf.getEntry(fileName);
        InputStream in = zf.getInputStream(ze);
        return new BufferedReader(new InputStreamReader(in));
    }

    /**
     * 根据文件名从压缩包中读取二进制格式的数据流
     * @param zipFile 压缩包
     * @param fileName 需要读取的文件名
     * @return 文件数据
     * @throws Exception 异常
     */
    public static byte[] readZipFileBinary(String zipFile,String fileName) throws Exception {
        ZipFile zf = new ZipFile(zipFile);
        ZipEntry ze = zf.getEntry(fileName);
        InputStream in = zf.getInputStream(ze);
        byte[] readData = new byte[in.available()];
        in.read(readData);
        return readData;
    }
    /**
     * @param inPath   要压缩的文件路径
     * @param outDir  压缩之后的文件目录
     * @param outputFileName  压缩之后的文件名
     */
    public static void generateFile(String inPath,String outDir,String outputFileName) throws Exception {

        File file = new File(inPath);
        // 压缩文件的路径不存在
        if (!file.exists()) {
            throw new Exception("路径 " + inPath + " 不存在文件，无法进行压缩...");
        }
        // 用于存放压缩文件的文件夹
        File compress = new File(outDir);
        // 如果文件夹不存在，进行创建
        if( !compress.exists() ){
            compress.mkdirs();
        }

        // 输出流
        FileOutputStream outputStream = new FileOutputStream(outDir + File.separator + outputFileName);

        // 压缩输出流
        ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(outputStream));

        generateFile(zipOutputStream,file,"");

        System.out.println("源文件位置：" + outDir + File.separator  + outputFileName);
        // 关闭 输出流
        zipOutputStream.close();
    }


    /**
     * @param out  输出流
     * @param file 目标文件
     * @param dir  文件夹
     * @throws Exception 异常
     */
    private static void generateFile(ZipOutputStream out, File file, String dir) throws Exception {

        // 当前的是文件夹，则进行一步处理
        if (file.isDirectory()) {
            //得到文件列表信息
            File[] files = file.listFiles();

            //将文件夹添加到下一级打包目录
            out.putNextEntry(new ZipEntry(dir + "/"));

            dir = dir.length() == 0 ? "" : dir + "/";

            //循环将文件夹中的文件打包
            for (int i = 0; i < files.length; i++) {
                generateFile(out, files[i], dir + files[i].getName());
            }

        } else { // 当前是文件

            // 输入流
            FileInputStream inputStream = new FileInputStream(file);
            // 标记要打包的条目
            String tmpPath = StringUtil.isEmpty(dir) ? file.getName() : dir + File.separator + file.getName();
            out.putNextEntry(new ZipEntry(tmpPath));
            // 进行写操作
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = inputStream.read(bytes)) > 0) {
                out.write(bytes, 0, len);
            }
            // 关闭输入流
            inputStream.close();
        }
    }


    /**
     * 往压缩包内写入新文件
     * @param srcFile 源文件
     * @param zipFilePath 需要写入的压缩包
     * @param dir 需要写入压缩包内的相对位置
     */
    public static void writeZipFile(String srcFile,String zipFilePath,String dir)throws Exception {

        File file = new File(srcFile);
        // 压缩文件的路径不存在
        if (!file.exists()) {
            throw new Exception("路径 " + srcFile + " 不存在文件，无法进行压缩...");
        }

        ZipFile zf = new ZipFile(zipFilePath);
        // 输出流
        FileOutputStream outputStream = new FileOutputStream(zipFilePath);
        ZipOutputStream zos = new ZipOutputStream(outputStream);
        try {
            String tmpPath = StringUtil.isEmpty(dir) ? file.getName() : dir + File.separator + file.getName();
            ZipEntry ze = zf.getEntry(tmpPath);
            if (ze != null) {
                InputStream is = new FileInputStream(file);
                zos.putNextEntry(ze);
                byte[] buf = new byte[1024];
                int len;
                while ((len = (is.read(buf))) > 0) {
                    zos.write(buf, 0, len);
                }

                zos.closeEntry();
            } else {
                //否则添加
                generateFile(zos, file, "");
            }
        }catch (Exception e){
            KLog.e("write file " + srcFile + " to zip file " + zipFilePath + " failed",e);
        }finally {
            zos.close();
        }

    }



    /**
     * 解压文件到指定路径
     * @param zipName 压缩包路径
     * @param targetDirName 解压后的路径
     * @throws Exception 异常
     */
    public static File unzipFile(String zipName, String targetDirName) throws Exception {
        if (!targetDirName.endsWith(File.separator)) {
            targetDirName += File.separator;
        }
        try {
            // 根据zip文件创建ZipFile对象，此类的作用是从zip文件读取条目
            ZipFile zipFile = new ZipFile(zipName);
            ZipEntry zn = null;
            String entryName = null;
            String targetFileName = null;
            byte[] buffer = new byte[4096];
            int bytes_read;
            Enumeration entrys = zipFile.entries();			// 获取ZIP文件里所有的文件条目的名字
            entrys.hasMoreElements();
            while (entrys.hasMoreElements()) {				// 循环遍历所有的文件条目的名字
                zn = (ZipEntry) entrys.nextElement();
                entryName = zn.getName();				// 获得每一条文件的名字
                targetFileName = targetDirName + entryName;
                if (zn.isDirectory()) {
                    new File(targetFileName).mkdirs();		// 如果zn是一个目录，则创建目录
                    continue;
                } else {
                    new File(targetFileName).getParentFile().mkdirs();// 如果zn是文件，则创建父目录
                }
                File targetFile = new File(targetFileName);	// 否则创建文件
                System.out.println("正在创建文件：" + targetFile.getAbsolutePath());
                FileOutputStream os = new FileOutputStream(targetFile);// 打开文件输出流
                InputStream is = zipFile.getInputStream(zn);	// 从ZipFile对象中打开entry的输入流
                while ((bytes_read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytes_read);
                }
                os.close();								// 关闭流
                is.close();
                zipFile.close();

                return targetFile;
            }
            KLog.i("解压缩"+zipName+"成功" );
        } catch (IOException err) {
            KLog.e("解压缩"+zipName+"失败: ",err);
        }
        return null;
    }
    public static void unzipFiles(String zipName, String targetDirName) throws Exception {
        if (!targetDirName.endsWith(File.separator)) {
            targetDirName += File.separator;
        }
        File targetFileDir = new File(targetDirName);
        if(!targetFileDir.exists()){
            targetFileDir.mkdirs();
        }
        // 根据zip文件创建ZipFile对象，此类的作用是从zip文件读取条目
        ZipFile zipFile = new ZipFile(zipName);
        try {
            ZipEntry zn = null;
            String entryName = null;
            String targetFileName = null;
            byte[] buffer = new byte[4096];
            int bytes_read;
            Enumeration entrys = zipFile.entries();			// 获取ZIP文件里所有的文件条目的名字
            entrys.hasMoreElements();
            while (entrys.hasMoreElements()) {				// 循环遍历所有的文件条目的名字
                zn = (ZipEntry) entrys.nextElement();
                entryName = zn.getName();				// 获得每一条文件的名字
                targetFileName = targetDirName + entryName;
                if (zn.isDirectory()) {
                    new File(targetFileName).mkdirs();		// 如果zn是一个目录，则创建目录
                    continue;
                } else {
                    new File(targetFileName).getParentFile().mkdirs();// 如果zn是文件，则创建父目录
                }
                File targetFile = new File(targetFileName);	// 否则创建文件
                System.out.println("正在创建文件：" + targetFile.getAbsolutePath());
                FileOutputStream os = new FileOutputStream(targetFile);// 打开文件输出流
                InputStream is = zipFile.getInputStream(zn);	// 从ZipFile对象中打开entry的输入流
                while ((bytes_read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytes_read);
                }
                os.close();								// 关闭流
                is.close();

//                return targetFile;
            }
            KLog.i("解压缩"+zipName+"成功" );
        } catch (IOException err) {
            KLog.e("解压缩"+zipName+"失败: ",err);
        }finally {
            zipFile.close();
        }
//        return null;
    }
    public static void zipFolders(List<FileZipParam> srcFileStringList, String zipFileString) throws Exception {
        //创建ZIP
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));
        //创建文件
        for(FileZipParam fileZipParam: srcFileStringList){
            File file = new File(fileZipParam.getPath());
            //压缩
            String filePath = file.getName();
            if(!TextUtils.isEmpty(fileZipParam.getParentDirPath())){
                filePath = fileZipParam.getParentDirPath() + File.separator + file.getName();
                String folderString = fileZipParam.getPath().replace(filePath,"");
                ZipFiles(folderString, filePath, outZip);
            }else{
                ZipFiles(file.getParent()+ File.separator, filePath, outZip);
            }
        }
        //完成和关闭
        outZip.finish();
        outZip.close();
    }
    public static void zipFolder(String srcFileString, String zipFileString) throws Exception {
        //创建ZIP
        ZipOutputStream outZip = new ZipOutputStream(new FileOutputStream(zipFileString));
        //创建文件
        File file = new File(srcFileString);
        //压缩
        ZipFiles(file.getParent()+ File.separator, file.getName(), outZip);
        //完成和关闭
        outZip.finish();
        outZip.close();
    }
    private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null)
            return;
        File file = new File(folderString + fileString);
        if (file.isFile()) {
            String name = fileString;
            ZipEntry zipEntry = new ZipEntry(name);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {
            //文件夹
            String fileList[] = file.list();
            //没有子文件和压缩
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }
            //子文件和递归
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString, fileString + "/" + fileList[i], zipOutputSteam);
            }
        }
    }
}
