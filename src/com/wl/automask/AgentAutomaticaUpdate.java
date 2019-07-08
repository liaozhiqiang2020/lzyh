package com.wl.automask;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ubique.inieditor.IniEditor;

import com.alibaba.fastjson.JSONObject;
import com.wl.http.client.HttpClient;
import com.wl.swt.ShowTimer;
import com.wl.swt.ShowTimerThread;
import com.wl.tools.FtpTool;
import com.wl.tools.IniFileOperator;
import com.wl.tools.SystemOpr;
import com.wl.tools.mTimer;

public class AgentAutomaticaUpdate {

	private static Logger logger=LoggerFactory.getLogger(AgentAutomaticaUpdate.class);
	
	private FtpTool ftp;
	private String configname;
	private String section;
	private IniFileOperator iniOP;
	private String updateVersion;//需要更新至当前版本
	
	private String ebagentconfig;//
	
	private HttpClient httpClient;
	

	private boolean checkEdtion()
	{
		System.out.println("检查版本");
		iniOP=new IniFileOperator(new File(configname));
		iniOP.setSection(section);
		String version=iniOP.getValue("version");
		String versionOld=iniOP.getValue("versionOld");
		ftp.setFilename(iniOP.getValue("agentname"));
		if(version.equals(versionOld))
		{
			logger.info("当前已经是最新版本:"+version);
			return false;
		}
		else
		{
			logger.info("需要更新至版本:"+version);
			updateVersion=version;
			return true;
		}
		
	}
	
	public void doUpdate()
	{
		if(!checkEdtion())return;
		
		//ftp.setRemotepath(ftp.getRemotepath()+updateVersion+"\\");
		
		String filename=iniOP.getValue("agentname");
		String src=ftp.getLocalpath()+"\\"+filename;
		
		if(!ftp.downLoadFile(updateVersion))
		{
			logger.info("Agent更新失败,下载文件失败:"+ftp.getErrormsg());
			return;
		}
		
		
		String dest=iniOP.getValue("jarPath")+"\\"+filename;
		
		
		iniOP.setValue("versionold", updateVersion);
		iniOP.setValue("modifydate", mTimer.getTime(2));
		iniOP.setValue("modifytime", mTimer.getTime(5));
		iniOP.saveIniFile();
		iniOP=null;
		
		 SystemOpr.reboot(60);
	     
		// SystemOpr.copyFile(src, dest);
		
		 updateDeviceAgentVertion(updateVersion);
		 
		 
	    
	     new Thread(new ShowTimerThread(true, "60", "客户端已更新,即将重启,请您尽快结束操作")).start();
	     
	     SystemOpr.copyFile(src, dest);
	}
	
	
	public void updateDeviceAgentVertion(String version)
	{
		
		IniEditor editor=new IniEditor();
		
		try {
			editor.load(new File(ebagentconfig));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("获取配置文件失败:"+ebagentconfig);
		}
		
		JSONObject header=new JSONObject();
		header.put("dateTime", mTimer.getTime(4));
		header.put("transcode", "DEVRV");
		
		JSONObject body=new JSONObject();
		body.put("deviceno", editor.get("BankConfig", "TerminalNo"));
		body.put("version", version);
		
		JSONObject json=new JSONObject();
		json.put("body", body);
		json.put("head", header);
		
		editor=null;
		
		
		
		boolean isSuccess=httpClient.sendMgr(json);
		
		
		if(!isSuccess)
		{
			logger.info("更新设备Agent版本信息失败.");
		}
		
		logger.info("更新设备Agent版本返回信息:"+httpClient.getError());
		
		return;
	}
	
	
	public FtpTool getFtp() {
		return ftp;
	}
	public void setFtp(FtpTool ftp) {
		this.ftp = ftp;
	}
	public String getConfigname() {
		return configname;
	}
	public void setConfigname(String configname) {
		this.configname = configname;
	}
	public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}

	public String getUpdateVersion() {
		return updateVersion;
	}

	public void setUpdateVersion(String updateVersion) {
		this.updateVersion = updateVersion;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public String getEbagentconfig() {
		return ebagentconfig;
	}

	public void setEbagentconfig(String ebagentconfig) {
		this.ebagentconfig = ebagentconfig;
	}


	
	
	
	
	
}
