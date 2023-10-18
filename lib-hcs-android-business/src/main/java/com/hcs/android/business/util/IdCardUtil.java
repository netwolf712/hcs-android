package com.hcs.android.business.util;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.hcs.android.common.util.StringUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class IdCardUtil {

    /**
     * 获取出生日期
     *
     * @return 返回字符串类型
     */
    @NonNull
    public static String getBirthFromIdCard(String idCard) {
        if(StringUtil.isEmpty(idCard)){
            return "";
        }
        if (idCard.length() != 18 && idCard.length() != 15) {
            return "";
        }
        if (idCard.length() == 18) {
            String year = idCard.substring(6).substring(0, 4);// 得到年份
            String month = idCard.substring(10).substring(0, 2);// 得到月份
            String day = idCard.substring(12).substring(0, 2);// 得到日
            return (year + "-" + month + "-" + day);
        } else {
            String year = "19" + idCard.substring(6, 8);// 年份
            String month = idCard.substring(8, 10);// 月份
            String day = idCard.substring(10, 12);// 得到日
            return (year + "-" + month + "-" + day);
        }
    }

    /**
     * 获取出生日期
     *
     * @return 返回日期格式
     */
    public static Date getBirthDayFromIdCard(@NonNull String idCard) throws ParseException {
        Date birth = null;
        if (idCard.length() == 18) {
            String year = idCard.substring(6).substring(0, 4);// 得到年份
            String month = idCard.substring(10).substring(0, 2);// 得到月份
            String day = idCard.substring(12).substring(0, 2);// 得到日
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            birth = format.parse(year + "-" + month + "-" + day);
        } else if (idCard.length() == 15) {
            String year = "19" + idCard.substring(6, 8);// 年份
            String month = idCard.substring(8, 10);// 月份
            String day = idCard.substring(10, 12);// 得到日
            @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            birth = format.parse(year + "-" + month + "-" + day);
        }
        return birth;
    }

    /**
     * 获取性别
     * 0=未知的性别,0=未说明的性别,2=女性,1=男性
     * @return int
     */
    public static int getSexFromIdCard(String idCard) {
        int sex = 0;
        // 身份证号码为空
        if (StringUtil.isEmpty(idCard)){
            return sex;
        }
        if (idCard.length() == 18) {
            if (Integer.parseInt(idCard.substring(16).substring(0, 1)) % 2 == 0) {// 判断性别
                sex = 2; // 女
            } else {
                sex = 1; // 男
            }
        } else if (idCard.length() == 15) {
            String usex = idCard.substring(14, 15);// 用户的性别
            if (Integer.parseInt(usex) % 2 == 0) {
                sex = 2; // 女
            } else {
                sex = 1; // 男
            }
        }
        return sex;
    }

    /**
     * 根据身份证的号码算出当前身份证持有者的年龄
     * @return  -1(表示异常) 0 (身份证号码为空)
     */
    public static int getAgeForIdCard(String idCard) {
        try {
            int age = 0;
            if (StringUtil.isEmpty(idCard)) {
                return age;
            }

            String birth = "";
            if (idCard.length() == 18) {
                birth = idCard.substring(6, 14);
            } else if (idCard.length() == 15) {
                birth = "19" + idCard.substring(6, 12);
            }

            int year = Integer.parseInt(birth.substring(0, 4));
            int month = Integer.parseInt(birth.substring(4, 6));
            int day = Integer.parseInt(birth.substring(6));
            Calendar cal = Calendar.getInstance();
            age = cal.get(Calendar.YEAR) - year;
            //周岁计算
            if (cal.get(Calendar.MONTH) < (month - 1) || (cal.get(Calendar.MONTH) == (month - 1) && cal.get(Calendar.DATE) < day)) {
                age--;
            }
            return age;
        } catch (Exception e) {
            e.getMessage();
        }
        return -1;
    }

    /**
     * 15 位身份证号码转 18 位
     * <p>
     * 15位身份证号码：第7、8位为出生年份（两位数），第9、10位为出生月份，第11、12位代表出生日期，第15位代表性别，奇数为男，偶数为女。
     * 18位身份证号码：第7、8、9、10位为出生年份（四位数），第11、第12位为出生月份，第13、14位代表出生日期，第17位代表性别，奇数为男，偶数为女。
     */
    public static StringBuffer IdCardMethod15To18(String idCard) {
        //将字符串转化为buffer进行操作
        StringBuffer stringBuffer = new StringBuffer(idCard);
        //身份证最后一位校验码，X代表10（顺序固定）
        char[] checkIndex = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        int sum = 0;
        //在第6位插入年份的前两位19
        stringBuffer.insert(6, "19");
        for (int i = 0; i < stringBuffer.length(); i++) {
            char c = stringBuffer.charAt(i);
            //前17位数字
            int ai = Integer.parseInt(String.valueOf(c));
            //前17位每位对应的系数（7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2 ）
            int wi = ((int) Math.pow(2, stringBuffer.length() - i)) % 11;
            //总和（每位数字乘以系数再相加）
            sum = sum + ai * wi;
        }
        //总和除以11求余
        int indexOf = sum % 11;
        //根据余数作为下表在校验码数组里取值
        stringBuffer.append(checkIndex[indexOf]);
        return stringBuffer;
    }
}
