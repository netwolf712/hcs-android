package com.hcs.android.common.util;

import com.hcs.android.common.util.log.KLog;

import java.io.BufferedReader;
import java.lang.reflect.Field;

/**
 * 对象工具类
 */
public class ObjectUtil {
    /**
     * 将数据流转换为对象
     * @param beanClass 对象类型
     * @param bufferedReader 数据流
     * @param <T>
     * @return 按照bean对象实例化
     */
    public static <T> T convertBufferToObject(Class<T> beanClass,BufferedReader bufferedReader) throws Exception{
        String orgData = "";
        try {
            String readStr;
            while ((readStr = bufferedReader.readLine()) != null) {
                orgData += readStr;
            }
        }catch (Exception e){
            KLog.e(e);
        }
        return JsonUtils.toObject(orgData,beanClass);
    }

    /**
     * 将对象的属性数据赋值给另一个（包括继承的父类，深度循环拷贝）
     * @param srcBean 源对象
     * @param srcClass 源对象类型
     * @param dstBean 目的对象
     * @param dstClass 目的对象类型
     */
    public static void CopyObjToAnotherEx(Object srcBean,Class<?> srcClass,Object dstBean,Class<?> dstClass){
        Field[] fields = dstClass.getDeclaredFields();
        if(fields != null && fields.length > 0){
            for(Field field: fields){
                Object objValue = PropertyUtil.getPropertyEx(srcClass, srcBean, field.getName());
                PropertyUtil.setProperty(dstClass,dstBean,objValue,field);
            }
        }

        //如果有父类，则将父类的属性也赋值
        if(dstClass.getSuperclass() != null && Object.class != dstClass.getSuperclass()){
            //此处是递归，需小心
            CopyObjToAnotherEx(srcBean,srcClass,dstBean,dstClass.getSuperclass());
        }
    }
    /**
     * 将对象的属性数据赋值给另一个（包括继承的父类）
     * @param beanClass 目的对象
     * @param bean 源数据
     * @param orgClass 源对象
     * @param <T> 对象类型
     * @return 赋值结果
     */
    public static <T> T CopyObjToAnother(Class<T> beanClass,Object bean,Class<?> orgClass)
    {
        try{
            Class<?> c = Class.forName(beanClass.getName());
            T targetObj = (T)c.newInstance();
            CopyObjToAnotherEx(bean,orgClass,targetObj,beanClass);
            return targetObj;
        }catch(Exception e){
            return null;
        }
    }
}
