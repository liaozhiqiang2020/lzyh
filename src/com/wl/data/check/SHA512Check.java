package com.wl.data.check;

import java.io.File;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

public class SHA512Check {
	private String checkValue;
	
	public SHA512Check() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean check(String checkValue,File file)
	{
		if(!file.exists())
		{
			System.out.println("校验文件不存在.");
			return false;
		}
		//System.out.println(new Date().toLocaleString());
		String hex="";
		try
		{
		hex=new DigestUtils(MessageDigestAlgorithms.SHA_512).digestAsHex(file);
		
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		//System.out.println(new Date().toLocaleString());
		return checkValue.equalsIgnoreCase(hex);
	}
	public boolean getCheckValue(File file)
	{
		try
		{
		this.checkValue=new DigestUtils(MessageDigestAlgorithms.SHA_512).digestAsHex(file);
		return true;
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
	}

	public String getCheckValue() {
		return checkValue;
	}

	public void setCheckValue(String checkValue) {
		this.checkValue = checkValue;
	}

}
