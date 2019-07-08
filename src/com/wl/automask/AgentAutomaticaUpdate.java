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
	private String updateVersion;//��Ҫ��������ǰ�汾
	
	private String ebagentconfig;//
	
	private HttpClient httpClient;
	

	private boolean checkEdtion()
	{
		System.out.println("���汾");
		iniOP=new IniFileOperator(new File(configname));
		iniOP.setSection(section);
		String version=iniOP.getValue("version");
		String versionOld=iniOP.getValue("versionOld");
		ftp.setFilename(iniOP.getValue("agentname"));
		if(version.equals(versionOld))
		{
			logger.info("��ǰ�Ѿ������°汾:"+version);
			return false;
		}
		else
		{
			logger.info("��Ҫ�������汾:"+version);
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
			logger.info("Agent����ʧ��,�����ļ�ʧ��:"+ftp.getErrormsg());
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
		 
		 
	    
	     new Thread(new ShowTimerThread(true, "60", "�ͻ����Ѹ���,��������,���������������")).start();
	     
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
			logger.error("��ȡ�����ļ�ʧ��:"+ebagentconfig);
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
			logger.info("�����豸Agent�汾��Ϣʧ��.");
		}
		
		logger.info("�����豸Agent�汾������Ϣ:"+httpClient.getError());
		
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
