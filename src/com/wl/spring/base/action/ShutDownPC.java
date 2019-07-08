package com.wl.spring.base.action;

import com.alibaba.fastjson.JSONObject;
import com.wl.spring.base.BaseService;
import com.wl.swt.ShowTimerThread;
import com.wl.tools.CommonRegularExp;
import com.wl.tools.SystemOpr;

public class ShutDownPC extends BaseService {

	@Override
	public boolean doAction(JSONObject request) {
		// TODO Auto-generated method stub
		
	     String seconds=request.getString("seconds");//��ʱ�ػ�����
	     if(seconds==null||seconds.equals(""))seconds="60";
	     
	     if(!CommonRegularExp.isSignlessIntegral(seconds))seconds="60";//�ж��Ƿ���������
	     SystemOpr.shutdown(Integer.parseInt(seconds));
	     setRetInfo("�����ػ�");
	     
	     
	    
	     
	     
	     new Thread(new ShowTimerThread(true, seconds, "�����ر��豸,�뾡��������Ĳ���.")).start();
	     
	    
		return true;
	}

}
