package com.wl.spring.base;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wl.params.CommonParams;
import com.wl.tools.IniFileOperator;


public abstract class BaseService {
	
	public final static Logger logger=LoggerFactory.getLogger(BaseService.class);
	
	private String errcode;
	
	private String errmsg;
	
	private JSONObject response;
	
	private String configFile;
	
	private String transcode;
	
	private String desc;
	
	private String defaultconfig;
	
	public BaseService() {
		// TODO Auto-generated constructor stub
		response=new JSONObject();
		setRespHead("retcode","0000");
		setRespHead("retmsg", "交易成功");
		
	}
	
	public JSONObject beforeAction(JSONObject request)
	{
		if(request==null||!request.containsKey("body"))
		{
			setErrmsg("非法请求报文.");
			return response;
		}
		setRespHead("transcode", request.getJSONObject("head").getString("transcode"));
		setRespHead("desc", this.getDesc());//交易描述
		setDeviceNo();
		
		return afterAction(request);
	}
	
	
	public abstract boolean doAction(JSONObject request);
	
	public JSONObject afterAction(JSONObject request)
	{
		JSONObject body=request.getJSONObject("body");
		doAction(body);
		return response;
	}
	
	public void setRespHead(String key,Object value)
	{
		if(!response.containsKey("head"))response.put("head", new JSONObject());
		response.getJSONObject("head").put(key, value);
	}
	
	public void setRespBody(String key,Object value)
	{
		if(!response.containsKey("body"))response.put("body", new JSONObject());
		response.getJSONObject("body").put(key, value);
	}
	
	public void setRetInfo(String retcode,String retmsg)
	{
		setRespHead("retcode",retcode);
		setRespHead("retmsg",retmsg);
	}
	
	public void setRetInfo(String retmsg)
	{
		setRetInfo("9999", retmsg);//交易失败
	}
	public void  setRetInfo()
	{
		setRetInfo("0000", "交易成功");
	}
	
	public String getRespHead(String key)
	{
		return response.getJSONObject("head").getString(key);
	}

	public String getErrcode() {
		return errcode;
	}

	public void setErrcode(String errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	
	public void setDeviceNo()
	{
		setRespHead("deviceNo",CommonParams.deviceNo);
	}

	public String getTranscode() {
		return transcode;
	}

	public void setTranscode(String transcode) {
		this.transcode = transcode;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDefaultconfig() {
		return defaultconfig;
	}

	public void setDefaultconfig(String defaultconfig) {
		this.defaultconfig = defaultconfig;
	}
	
	

}
