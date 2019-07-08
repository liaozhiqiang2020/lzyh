package com.wl.spring.base.action;

import com.alibaba.fastjson.JSONObject;
import com.wl.spring.base.BaseService;
import com.wl.swt.ShowTimer;
import com.wl.swt.ShowTimerThread;
import com.wl.tools.CommonRegularExp;
import com.wl.tools.SystemOpr;

public class RebootPC extends BaseService {

	@Override
	public boolean doAction(JSONObject request) {
		// TODO Auto-generated method stub
		 String seconds=request.getString("seconds");//定时关机秒数
	     if(seconds==null||seconds.equals(""))seconds="60";
	     
	     if(!CommonRegularExp.isSignlessIntegral(seconds))seconds="60";//判断是否是正整数
	    
	     SystemOpr.reboot(Integer.parseInt(seconds));
	     setRetInfo("即将重启");
	    // ShowTimer.showTimer(seconds, "即将重启设备,请尽快结束您的操作.");
	     new Thread(new ShowTimerThread(true, seconds, "即将重启设备,请尽快结束您的操作.")).start();
	     
		
		return true;
	}

}
