/**
 * 
 */
package com.hcs.android.common.util;

import androidx.annotation.Nullable;

import com.hcs.android.common.util.log.KLog;

import org.droid.java.beans.PropertyDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class PropertyUtil 
{
	public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String propertyName) {
		StringBuffer sb = new StringBuffer();//构建一个可变字符串用来构建方法名称
		Method setMethod = null;
		Method getMethod = null;
		PropertyDescriptor pd = null;
		try {
			Field f = clazz.getDeclaredField(propertyName);//根据字段名来获取字段
			if (f!= null) {
				//构建方法的后缀
				String methodEnd = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
				sb.append("set").append(methodEnd);//构建set方法
				setMethod = clazz.getDeclaredMethod(sb.toString(), new Class[]{ f.getType() });
				sb.delete(0, sb.length());//清空整个可变字符串
				if (f.getType() == boolean.class || f.getType() == Boolean.class)
				{
					sb.append("is").append(methodEnd);//构建get方法
				}
				else
				{
					sb.append("get").append(methodEnd);//构建get方法
				}
				//构建get 方法
				getMethod = clazz.getDeclaredMethod(sb.toString(), new Class[]{ });
				//构建一个属性描述器 把对应属性 propertyName 的 get 和 set 方法保存到属性描述器中
				pd = new PropertyDescriptor(propertyName, getMethod, setMethod);
			}
		} catch (Exception ex) {
			//ex.printStackTrace();
		}

		return pd;
	}
	/**
	 * 将字符串按照普通的变量类型进行转换
	 */
	public static Object convertCommonValue(Class<?> clazz,Object orgValue)
	{
		try {
			//字符串不需要转换
			if(clazz.getName().contains("String")) return orgValue;
			//其它类型基本都有ValueOf这个方法
			if(clazz.getName().contains("Integer")) return Integer.valueOf(orgValue.toString());
			if(clazz.getName().contains("Float")) return Float.valueOf(orgValue.toString());
		}catch (Exception e){
			KLog.e(e);
		}
		return orgValue;
	}

	/**
	 * 为对象的属性赋值
	 * @param clazz 对象类型
	 * @param obj 对象
	 * @param propertyName 属性名称
	 * @param value 属性值
	 */
	public static void setProperty(Class<?> clazz, Object obj, String propertyName, Object value){
		try {
            Field f = clazz.getDeclaredField(propertyName);//根据字段名来获取字段
			if(f == null){
				return;
			}
			setProperty(clazz,obj,value,f);
		}catch (Exception e){
			KLog.e(e);
		}
	}

	/**
	 * 为对象的属性赋值
	 * @param clazz 对象类型
	 * @param obj 对象
	 * @param value 属性值
	 * @param f 属性域
	 */
	public static void setProperty(Class<?> clazz, Object obj, Object value,Field f){
		try {
			String propertyName = f.getName();
			String methodName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
			Method method = clazz.getDeclaredMethod(methodName, f.getType());
			if(method == null){
				return;
			}
			method.invoke(obj, value);
		}catch (Exception e){
			KLog.e(e);
		}
	}
	/**
	 * 是否有该属性
	 * @param clazz 类型
	 * @param propertyName 属性
	 * @return 是否有属性
	 */
	public static boolean hasProperty(Class<?> clazz,String propertyName){
		//获取 clazz 类型中的 propertyName 的属性描述器
		PropertyDescriptor pd = getPropertyDescriptor(clazz, propertyName);
		if(pd ==  null){
			return false;
		}
		return true;
	}
	/**
	 * 获取属性数据的增强版
	 * @param clazz 基础类型
	 * @param obj 源数据
	 * @param propertyName 属性名称
	 * @return 属性值
	 */
	@Nullable
	public static Object getPropertyEx(Class<?> clazz, Object obj, String propertyName){
		if(hasProperty(clazz,propertyName)) {
			//如果有此属性，则直接返回
			return getProperty(clazz, obj, propertyName);
		}else{
			//否则还要判断其父类是否有此属性
			if(clazz.getSuperclass() != null && Object.class != clazz.getSuperclass()){
				//这是个递归，挺危险的
				return getPropertyEx(clazz.getSuperclass(),obj,propertyName);
			}
		}
		//否则就是真取不到数据了
		return null;
	}
	@Nullable
	public static Object getProperty(Class<?> clazz, Object obj, String propertyName){
		//Class<?> clazz = obj.getClass();//获取对象的类型
		PropertyDescriptor pd = getPropertyDescriptor(clazz, propertyName);//获取 clazz 类型中的 propertyName 的属性描述器
		if(pd ==  null){
			return null;
		}
		Object value = null ;
		try {
			Method getMethod = pd.getReadMethod();//从属性描述器中获取 get 方法
			if(getMethod == null){
				return null;
			}
			value = getMethod.invoke(obj);//调用方法获取方法的返回值
		} catch (Exception e) {			
			//KLog.e(e);
		}
	   return value;//返回值
	}

}
