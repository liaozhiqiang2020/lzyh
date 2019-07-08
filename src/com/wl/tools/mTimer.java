/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wl.tools;

import java.util.*;
import java.text.*;


public class mTimer {

    

    public static String getTime(int mode) {

        Date now = new Date(System.currentTimeMillis());

        switch (mode) {
            case 0: {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                return df.format(now);
            }
            case 1: {
                DateFormat df = new SimpleDateFormat("yyMMdd");
                return df.format(now);
            }
            case 2: {
                DateFormat df = new SimpleDateFormat("yyyyMMdd");
                return df.format(now);
            }
            case 3: {
                DateFormat df = new SimpleDateFormat("yyyy.MM.dd");
                return df.format(now);
            }
            case 4: {
                DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                return df.format(now);
            }
            case 5: {
                DateFormat df = new SimpleDateFormat("HH:mm:ss");
                return df.format(now);
            }
            case 6: {
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
                return df.format(now);
            }
            case 7: {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                return df.format(now);
            }
            case 8: {
                DateFormat df = new SimpleDateFormat("HHmmss");
                return df.format(now);
            }
            case 9:
            {
            	DateFormat df=new SimpleDateFormat("HHmmssSSS");
            	return df.format(now);
            }

            default: {
                DateFormat df = new SimpleDateFormat("yyyyMMdd");
                return df.format(now);
            }
        }
    }

    public static String getCurrentDate(int num) // �Ե���Ϊ׼����þ��뵱������num������ڣ���ֵ��ʾ��ǰ����ֵ��ʾ����
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance();   //��ʱ��ӡ����ȡ����ϵͳ��ǰʱ��
        calendar.add(Calendar.DATE, num);
        Date theDate = calendar.getTime();
        String s = df.format(theDate);
        return s;
    }

    public static String getDateByMSEL(long miliseconds)
    {
    	 SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
         Date theDate =new Date(miliseconds);
         String s = df.format(theDate);
         
         return s;
    }
    
    public static void main(String args[]) {
        System.out.println(getTime(9));
    }
}
