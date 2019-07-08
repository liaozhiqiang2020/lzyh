package com.wl.tools;

import org.junit.Test;

public class CommonRegularExp {
	
	/**
	 * 判断是否是纯数字
	 * @param s
	 * @return
	 */
	public static boolean isDigital(String s)
	{
		return s.matches("^[0-9]*$");
	}
	
	/**
	 * 判断是否是n位纯数字
	 * @param s
	 * @param n
	 * @return
	 */
	public static boolean isNDigital(String s,int n)
	{
		return s.matches("^\\d{"+n+"}$");
	}
	
	/**
	 * 是否是m-n位纯数字
	 * @param s
	 * @param m
	 * @param n
	 * @return
	 */
	public static boolean isMNDigital(String s,int m,int n)
	{
		return s.matches("^\\d{"+m+","+"n}$");
	}
	
	/**
	 * 两位小数的正实数
	 * @param s
	 * @return
	 */
	public static boolean isTwoDecimal(String s)
	{
		return s.matches("^[0-9]+(.[0-9]{2})?$");
	}
	
	/**
	 * 是否是正整数
	 * @param s
	 * @return
	 */
	public static boolean isSignlessIntegral(String s)
	{
		return s.matches("^\\+?[1-9][0-9]*$");
	}
	
	
	/**
	 * 是否是负整数
	 * @param s
	 * @return
	 */
	public static boolean isNegtiveIntegral(String s)
	{
		return s.matches("^\\-[1-9][0-9]*$");
	}
	
	/**
	 * 是否英文字母
	 * @param s
	 * @return
	 */
	public static boolean isWords(String s)
	{
		return s.matches("^[A-Za-z]+$");
	}
	
	/**
	 * 是否纯大写字母
	 * @param s
	 * @return
	 */
	public static boolean isUpperWords(String s)
	{
		return s.matches("^[A-Z]*$");
		
	}
	
	/**
	 * 是否纯小写字母
	 * @param s
	 * @return
	 */
	public static boolean isLowWords(String  s)
	{
		return s.matches("^[a-z]*$");
	}
	
	/**
	 * 是否数字和字母
	 * @param s
	 * @return
	 */
	public static boolean isDigitalAndWords(String s)
	{
		return s.matches("^[A-Za-z0-9]+$");
	}
	
	/**
	 * 是否是数字字母和下划线
	 * @param s
	 * @return
	 */
	public static boolean isCommonWords(String s)
	{
		return s.matches("^\\w+$");
	}
	
	/**
	 * 验证是否是正常密码
	 * 以字母开头，长度在6~18之间，只能包含字符、数字和下划线
	 * @param s
	 * @return
	 */
	public static boolean isNormalPassword(String s)
	{
		return s.matches("^[a-zA-Z]\\w{5,17}$");
	}
	
	/**
	 * 是否是纯汉字
	 * @param s
	 * @return
	 */
	public static boolean isChineseWords(String s)
	{
		return s.matches("^[\u4e00-\u9fa5]{0,}$");
	}
	
	/**
	 * 是否是正确的电话号码
	 * @param s
	 * @return
	 */
	public static boolean checkPhoneNo(String s)
	{
		return s.matches("^(\\d{3,4}[-]?)?\\d{7,8}$");
	}
	
	/**
	 * 验证Email
	 * @param s
	 * @return
	 */
	public static boolean checkEmail(String s)
	{
		return s.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
	}
	
	/**
	 * 验证是否是正确的URL
	 * @param s
	 * @return
	 */
	public static boolean checkHttpUrl(String s)
	{
		return s.matches("^[a-zA-z]+://(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*(\\?\\S*)?$");
	}
	
	/**
	 * 验证是否是正确位数的身份证号码
	 * @param s
	 * @return
	 */
	public static boolean checkIdCardNo(String s)
	{
		return s.matches("^\\d{15}|\\d{18}|\\d{17}X$");
	}
	
	/**
	 * 验证年月日
	 * @param s
	 * @param split 分隔符
	 * @return
	 */
	public static boolean checkDate(String s,String split)
	{
		return s.matches("^(d{2}|d{4})"+split+"((0([1-9]{1}))|(1[1|2]))"+split+"(([0-2]([1-9]{1}))|(3[0|1]))$");
	}
	
	/**
	 * 验证IP地址
	 * @param s
	 * @return
	 */
	public static boolean checkIp(String s)
	{
		return s.matches("^(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5])$");
	}
	
	/**
	 * 匹配双字节字符(包括汉字在内)
	 * @param s
	 * @return
	 */
	public static boolean checkDoubleByte(String s)
	{
		return s.matches("^\\x00-\\xff$");
	}
	
	/**
	 * 匹配空行
	 * @param s
	 * @return
	 */
	public static boolean checkBlankLine(String s)
	{
		return s.matches("\\n[\\s| ]*\\r");
	}
	
	/**
	 * 匹配html标签
	 * @param s
	 * @return
	 */
	public static boolean checkHtmlTag(String s)
	{
		return s.matches("<(.*)>.*<\\/\1>|<(.*) \\/>");
	}
	
	/**
	 * 验证头尾空格
	 * @param s
	 * @return
	 */
	public static boolean checkHTBlank(String s)
	{
		return s.matches("(^\\s*)|(\\s*$)");
	}
	
	
	@Test
	public  void  test()
	{
		//System.out.println(isNDigital("2838239932", 10));
		System.out.println(checkPhoneNo("13815098989"));
		System.out.println(checkPhoneNo("025-93924828"));
		System.out.println(checkPhoneNo("0519-93299434"));
		System.out.println(checkPhoneNo("051983824288"));
		System.out.println(checkPhoneNo("0519-9930000"));
		System.out.println(checkPhoneNo("768099999"));
		System.out.println(checkPhoneNo("87328844"));
		System.out.println(checkEmail("wanglin@gugouang.com.cn"));
		System.out.println(checkHttpUrl("https://www.cnblogs.com/coder-wzr/p/7838527.html"));
		
		System.out.println(checkIdCardNo("32048119850912991X"));
	}
	

}
