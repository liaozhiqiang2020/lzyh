package com.wl.spring.base.action;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wl.spring.base.BaseService;
import com.wl.tools.IniFileOperator;

public class GetIniFileToJSON extends BaseService {


	private static Logger logger=LoggerFactory.getLogger(GetIniFileToJSON.class);
	@Override
	public boolean doAction(JSONObject request) {
		// TODO Auto-generated method stub
		
		
		logger.info("获取配置文件");
		
		if(request.containsKey("configfile"))
		{
			this.setConfigFile(request.getString("configfile"));
		}
		
		
        IniFileOperator ini=new IniFileOperator(new File(this.getConfigFile()));
		JSONObject config=ini.fileToJSON();
		this.setRespBody("config", config);
		ini=null;
		return true;
	}



}
