package com.wl.spring.base.action;

import com.alibaba.fastjson.JSONObject;
import com.wl.spring.base.BaseService;
import com.wl.swt.ShowTimerThread;
import com.wl.tools.SystemOpr;

public class CancleCMD extends BaseService {

	@Override
	public boolean doAction(JSONObject request) {
		// TODO Auto-generated method stub
		     SystemOpr.cancel();
		    // ShowTimer.hiddenTimer();
		     setRetInfo("√¸¡Ó“—»°œ˚");
		     new Thread(new ShowTimerThread(false)).start();
		     
		return true;
	}

}
