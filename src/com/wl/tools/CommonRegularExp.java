package com.wl.tools;

import org.junit.Test;

public class CommonRegularExp {
	
	/**
	 * �ж��Ƿ��Ǵ�����
	 * @param s
	 * @return
	 */
	public static boolean isDigital(String s)
	{
		return s.matches("^[0-9]*$");
	}
	
	/**
	 * �ж��Ƿ���nλ������
	 * @param s
	 * @param n
	 * @return
	 */
	public static boolean isNDigital(String s,int n)
	{
		return s.matches("^\\d{"+n+"}$");
	}
	
	/**
	 * �Ƿ���m-nλ������
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
	 * ��λС������ʵ��
	 * @param s
	 * @return
	 */
	public static boolean isTwoDecimal(String s)
	{
		return s.matches("^[0-9]+(.[0-9]{2})?$");
	}
	
	/**
	 * �Ƿ���������
	 * @param s
	 * @return
	 */
	public static boolean isSignlessIntegral(String s)
	{
		return s.matches("^\\+?[1-9][0-9]*$");
	}
	
	
	/**
	 * �Ƿ��Ǹ�����
	 * @param s
	 * @return
	 */
	public static boolean isNegtiveIntegral(String s)
	{
		return s.matches("^\\-[1-9][0-9]*$");
	}
	
	/**
	 * �Ƿ�Ӣ����ĸ
	 * @param s
	 * @return
	 */
	public static boolean isWords(String s)
	{
		return s.matches("^[A-Za-z]+$");
	}
	
	/**
	 * �Ƿ񴿴�д��ĸ
	 * @param s
	 * @return
	 */
	public static boolean isUpperWords(String s)
	{
		return s.matches("^[A-Z]*$");
		
	}
	
	/**
	 * �Ƿ�Сд��ĸ
	 * @param s
	 * @return
	 */
	public static boolean isLowWords(String  s)
	{
		return s.matches("^[a-z]*$");
	}
	
	/**
	 * �Ƿ����ֺ���ĸ
	 * @param s
	 * @return
	 */
	public static boolean isDigitalAndWords(String s)
	{
		return s.matches("^[A-Za-z0-9]+$");
	}
	
	/**
	 * �Ƿ���������ĸ���»���
	 * @param s
	 * @return
	 */
	public static boolean isCommonWords(String s)
	{
		return s.matches("^\\w+$");
	}
	
	/**
	 * ��֤�Ƿ�����������
	 * ����ĸ��ͷ��������6~18֮�䣬ֻ�ܰ����ַ������ֺ��»���
	 * @param s
	 * @return
	 */
	public static boolean isNormalPassword(String s)
	{
		return s.matches("^[a-zA-Z]\\w{5,17}$");
	}
	
	/**
	 * �Ƿ��Ǵ�����
	 * @param s
	 * @return
	 */
	public static boolean isChineseWords(String s)
	{
		return s.matches("^[\u4e00-\u9fa5]{0,}$");
	}
	
	/**
	 * �Ƿ�����ȷ�ĵ绰����
	 * @param s
	 * @return
	 */
	public static boolean checkPhoneNo(String s)
	{
		return s.matches("^(\\d{3,4}[-]?)?\\d{7,8}$");
	}
	
	/**
	 * ��֤Email
	 * @param s
	 * @return
	 */
	public static boolean checkEmail(String s)
	{
		return s.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
	}
	
	/**
	 * ��֤�Ƿ�����ȷ��URL
	 * @param s
	 * @return
	 */
	public static boolean checkHttpUrl(String s)
	{
		return s.matches("^[a-zA-z]+://(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*(\\?\\S*)?$");
	}
	
	/**
	 * ��֤�Ƿ�����ȷλ�������֤����
	 * @param s
	 * @return
	 */
	public static boolean checkIdCardNo(String s)
	{
		return s.matches("^\\d{15}|\\d{18}|\\d{17}X$");
	}
	
	/**
	 * ��֤������
	 * @param s
	 * @param split �ָ���
	 * @return
	 */
	public static boolean checkDate(String s,String split)
	{
		return s.matches("^(d{2}|d{4})"+split+"((0([1-9]{1}))|(1[1|2]))"+split+"(([0-2]([1-9]{1}))|(3[0|1]))$");
	}
	
	/**
	 * ��֤IP��ַ
	 * @param s
	 * @return
	 */
	public static boolean checkIp(String s)
	{
		return s.matches("^(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5]).(d{1,2}|1dd|2[0-4]d|25[0-5])$");
	}
	
	/**
	 * ƥ��˫�ֽ��ַ�(������������)
	 * @param s
	 * @return
	 */
	public static boolean checkDoubleByte(String s)
	{
		return s.matches("^\\x00-\\xff$");
	}
	
	/**
	 * ƥ�����
	 * @param s
	 * @return
	 */
	public static boolean checkBlankLine(String s)
	{
		return s.matches("\\n[\\s| ]*\\r");
	}
	
	/**
	 * ƥ��html��ǩ
	 * @param s
	 * @return
	 */
	public static boolean checkHtmlTag(String s)
	{
		return s.matches("<(.*)>.*<\\/\1>|<(.*) \\/>");
	}
	
	/**
	 * ��֤ͷβ�ո�
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
