package com.hcs.android.common.util;

import com.hcs.android.common.util.log.KLog;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * MD5加密工具类
 * @author pibigstar
 *
 */
public class Md5Util {

    //盐，用于混交md5
   // private static final String slat = "Cmo3fc";
    public static String encrypt(String dataStr) {
        try {
            //dataStr = dataStr + slat;
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(dataStr.getBytes("UTF8"));
            byte s[] = m.digest();
            String result = "";
            for (int i = 0; i < s.length; i++) {
                result += Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 计算文件 MD5
     * @param strFilePath
     * @return 返回文件的md5字符串 如果有异常，返回空字符串
     */
    public static String CalculateMd5AccordingFilePath(String strFilePath)
    {
        try
        {
            InputStream stream = new FileInputStream(strFilePath);
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] readFileBuf = new byte[8192];
            int len;    //total number of bytes read into the buffer
            while ((len = stream.read(readFileBuf)) > 0)
            {
                digest.update(readFileBuf, 0, len);
            }
            stream.close();
            return ByteStringUtils.toHexString(digest.digest()).toUpperCase();
        }
        catch (Exception e)
        {
            KLog.e(e);
            return "";
        }
    }
}




