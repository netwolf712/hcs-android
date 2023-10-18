package com.hcs.android.common.util;

import com.hcs.android.common.util.log.KLog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class StringUtil {

    /**
     * 检查字符串是否为空
     * <p>Checks if a String is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank("bob")     = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is
     *  not empty and not null and not whitespace
     * @since 2.0
     */
    public static boolean isNotBlank(String str) {
    	boolean isBlank = true;
    	if (str == null || "".equals(str.trim())) {
    		isBlank = false;
		}
        return isBlank;
    }
    
	public static boolean isEmpty(String str) { 
		boolean empty = false;
		if(str == null || str.trim().length() <=0){
			empty = true;
		}
		return empty;
	}
	
	public static <T> boolean isEmpty(List<T> objList) { 
		boolean empty = false;
		if(objList == null || objList.isEmpty()){
			empty = true;
		}
		return empty;
	}
	
	public static boolean isEmpty(String[] strList) { 
		boolean empty = false;
		if(strList == null || strList.length == 0){
			empty = true;
		}
		return empty;
	}
	
	/**
	 * 将字符串按照分隔符分开
	 */
	public static List<Object> CutStringWithChar(String str,char c)
	{
		if(StringUtil.isEmpty(str)) return null;
		List<Object> strList = new ArrayList<Object>();
		int pos = 0;
		String strSubPre = "";
		String strSubEnd = str;
		while((pos = strSubEnd.indexOf(c)) != -1)
		{
			if(pos == 0)
			{
				strSubEnd = strSubEnd.substring(pos + 1);
				continue ;
			}
			strSubPre = strSubEnd.substring(0,pos);
			strList.add(strSubPre);
			strSubEnd = strSubEnd.substring(pos + 1);
		}
		//将末尾的也加入list
		if(strSubEnd.length() > 0)
			strList.add(strSubEnd);
		
		return strList;
	}

	/**
	 * 将字符串转换成int类型的数组
	 * 主要为集合查询用
	 * @param str 符合id,id要求的字符串
	 * @return 数组
	 */
	public static Integer[] ConvertStringToIds(String str){
		if(isEmpty(str)){
			return null;
		}
		List<Object> objectList = CutStringWithChar(str,',');
		if(isEmpty(objectList)){
			return null;
		}
		List<Integer> dataList = new ArrayList<>();
		for(Object obj : objectList) {
			try {
				Integer data = Integer.parseInt(String.valueOf(obj));
				dataList.add(data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dataList.toArray(new Integer[0]);
	}
	
	/**
	* 创建指定数量的随机字符串
	* @param numberFlag 是否是数字
	* @param length
	* @return
	*/
	public static String createRandom(boolean numberFlag, int length){
	 String retStr = "";
	 String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
	 int len = strTable.length();
	 boolean bDone = true;
	 do {
	  retStr = "";
	  int count = 0;
	  for (int i = 0; i < length; i++) {
	  double dblR = Math.random() * len;
	  int intR = (int) Math.floor(dblR);
	  char c = strTable.charAt(intR);
	  if (('0' <= c) && (c <= '9')) {
	   count++;
	  }
	  retStr += strTable.charAt(intR);
	  }
	  if (count >= 2) {
	  bDone = false;
	  }
	 } while (bDone);
	 return retStr;
	}
	
	/**
	* 创建指定数量的随机字符串,给js生成随机版本号用的
	* @param numberFlag 是否是数字
	* @param length
	* @return
	*/
	public static String createRandom_4_js(boolean numberFlag, int length){
		return "2";
	}
	
	//正则匹配查找字符串是否存在
	public static boolean findStr(String Src, String regex) {
		
		if (isEmpty(Src) || isEmpty(regex)) return false;
		
		Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(Src);
        
        return m.find();
	}
	
	public static String getValidString(String val, String defaultVal) {
		if (isEmpty(val)) return defaultVal;
		
		return val;
	}
	
	public static String getValidString(Float val, String defaultVal) {
		if (val == null) return defaultVal;
		
		return String.valueOf(val);
	}
	
	public static String getValidString(Integer val, String defaultVal) {
		if (val == null) return defaultVal;
		
		return String.valueOf(val);
	}

	public static String FloatSavePointNumberCnt(float fValue)
	{
		DecimalFormat fnum = new DecimalFormat("##0.00"); 
		String dd=fnum.format(fValue); 
		return dd;
	}
	
	public static long Lines(String thisstrings)  
	{  
		long count = 1;  
		int position = 0;  
		while((position = thisstrings.indexOf('\n',position)) != -1)  
		{  
			count++;  
			position++; //Skip this occurance!  
		}  
		return count;  
		 
	} 
	public static String parseToNeatString(String str) {
			String dest = "";
			if (str!=null) {
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(str);
				dest = m.replaceAll("");
			}
			return dest.replaceAll("\\\\u000a", "");
		}
	
    /** 
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有： 
     * 13+任意数 
     * 15+除4的任意数 
     * 18+除1和4的任意数 
     * 17+除9的任意数 
     * 147 
     */  
    public static boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
    	if (isEmpty(str)) return false;
        String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(147)|(19[8-9])|(166))\\d{8}$";  
        Pattern p = Pattern.compile(regExp);  
        Matcher m = p.matcher(str);  
        return m.matches();  
    }

	/**
	 * 判断ip格式
	 * @param ip
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static boolean isIp(String ip) throws PatternSyntaxException {
		if (isEmpty(ip)) return false;
		String regExp = "^(25[0-5]|2[0-4][0-9]|1\\d\\d|[1-9]\\d|[1-9])(\\.(25[0-5]|2[0-4][0-9]|1\\d\\d|[1-9]\\d|[1-9]|0)){3}$|^(0\\.){3}0$";
		Pattern p = Pattern.compile(regExp);
		Matcher m = p.matcher(ip);
		return m.matches();
	}

	 // 过滤特殊字符 
    public static String StringFilter(String str,String regEx) throws PatternSyntaxException { 
		 // 只允许字母和数字 
	     //String regEx ="[^a-zA-Z0-9]"; 
		 // 清除掉所有特殊字符 
		 //String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"; 
		 Pattern p = Pattern.compile(regEx); 
		 Matcher m = p.matcher(str);
		 return m.replaceAll("").trim();
	 } 
    
    /** 
     * 使用正则表达式获取指定内容
     * @param msg 
     * @param MatchPattern
     * @return  
     */  
    public static List<String> extractMessageByRegular(String msg,String MatchPattern){  
    	
    	if(StringUtil.isEmpty(MatchPattern)){
    		//MatchPattern = "(\\[[^\\]]*\\])";
    		return null;
    	}
        List<String> list=new ArrayList<String>();  
        Pattern p = Pattern.compile(MatchPattern);  
        Matcher m = p.matcher(msg);  
        while(m.find()){  
            list.add(m.group().substring(0, m.group().length()));  
        }  
        return list;  
    }

	/**
	 * 比较两个字符串是否完全一致
	 * @param leftStr
	 * @param rightStr
	 * @return
	 */
	public static boolean compare(String leftStr,String rightStr)
	{
		if(isEmpty(leftStr) && !isEmpty(rightStr)) return false;
		if(!isEmpty(leftStr) && isEmpty(rightStr)) return false;
		if(isEmpty(leftStr) && isEmpty(rightStr)) return true;
		if(!leftStr.equals(rightStr)) return false;
		return true;
	}

	/**
	 * 将字符串转换成首字母大写
	 * @param str
	 * @return
	 */
	public static String upperCaseFirst(String str)
	{
		try{
			str = str.substring(0,1).toUpperCase() + str.substring(1);
		}catch (Exception e){
			KLog.e(e);
		}
		return str;
	}

	/**
	 * 将字符串转换成首字母小写
	 * @param str
	 * @return
	 */
	public static String lowerCaseFirst(String str)
	{
		try{
			str = str.substring(0,1).toLowerCase() + str.substring(1);
		}catch (Exception e){
			KLog.e(e);
		}
		return str;
	}

	/**
	 * 将linux命名规则的字符串转换成驼峰命名
	 * @param str
	 * @return
	 */
	public static String humpNames(String str)
	{
		try{
			String destStr = "";
			//按照_切分成多个名词
			List<Object> nameList = CutStringWithChar(str,'_');
			for(Object obj : Objects.requireNonNull(nameList)){
				destStr += upperCaseFirst(obj.toString());
			}
			return destStr;
		}catch (Exception e){
			KLog.e(e);
		}

		return str;
	}

	/**
	 * 判断字符串是否为数字或小数
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		if(str.indexOf(".")>0){//判断是否有小数点
			if(str.indexOf(".") == str.lastIndexOf(".")
					&& str.split("\\.").length == 2){ //判断是否只有一个小数点
				return pattern.matcher(str.replace(".","")).matches();
			}else {
				return false;
			}
		}else {
			return pattern.matcher(str).matches();
		}
	}

	/**
	 * 判断两字符串是否完全相同
	 * 同时考虑字符串为null的情况
	 * @return true相同，false不同
	 */
	public static boolean equalsIgnoreCase(String src,String dst){
		if(isEmpty(src) && isEmpty(dst)){
			return true;
		}
		if(isEmpty(src) && !isEmpty(dst)){
			return false;
		}
		return src.equalsIgnoreCase(dst);
	}

	public static boolean equals(String src,String dst){
		if(isEmpty(src) && isEmpty(dst)){
			return true;
		}
		if(isEmpty(src) && !isEmpty(dst)){
			return false;
		}
		return src.equals(dst);
	}

	/**
	 * 修剪字符串
	 * @param Src 源字符串
	 * @param c 需要修剪的内容
	 * @param pos 修剪的位置
	 * @return 修剪后的结果
	 */
	public static String trimString(String Src, String c, PositionEnum pos) {
		if (StringUtil.isEmpty(Src)){ return Src;}
		String regex = "";

		switch (pos) {
			case Left:
			{
				regex = "^"+String.valueOf(c)+"{1,}";
				Src = Src.replaceAll(regex, "");
			}
			break;
			case LeftRight:
			{
				regex = "^"+String.valueOf(c)+"{1,}";
				Src = Src.replaceAll(regex, "");
				regex = String.valueOf(c)+"{1,}$";
				Src = Src.replaceAll(regex, "");
			}
			break;
			case Right:
			{
				regex = String.valueOf(c)+"{1,}$";
				Src = Src.replaceAll(regex, "");
			}
			break;
			case All:
			default:
			{
				Src = Src.replaceAll(String.valueOf(c), "");
			}
			break;
		}
		return Src;
	}
	public enum PositionEnum {

		Left(0),
		Right(1),
		LeftRight(2),
		All(3);

		private int Code;

		private PositionEnum(int code) {
			this.Code = code;
		}

		public int getCode() {
			return Code;
		}

		public static PositionEnum getEnum(int code)
		{
			int Ordinal = code;
			if (Ordinal < 0 || Ordinal >= PositionEnum.values().length) return null;
			return PositionEnum.values()[Ordinal];
		}
	}
}
