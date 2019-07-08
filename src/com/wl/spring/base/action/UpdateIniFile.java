package com.wl.spring.base.action;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wl.spring.base.BaseService;
import com.wl.tools.IniFileOperator;

public class UpdateIniFile extends BaseService {
	
	private static Logger logger=LoggerFactory.getLogger(UpdateIniFile.class);
	
	
	
	@Override
	public boolean doAction(JSONObject request) {
		// TODO Auto-generated method stub
		
		logger.info(request.toString());
		
		if(request.containsKey("configfile"))
		{
			this.setConfigFile(request.getString("configfile"));
		}
		
		
		if(request==null||!request.containsKey("config"))
		{
			setRetInfo("请求报文不规范,无config字段");
			return false;
		}
		
		IniFileOperator ini=new IniFileOperator(new File(this.getConfigFile()));
		
		ini.modifyIniFileByJson(request.getJSONObject("config"));
		ini=null;
		
		return true;
	}

}
