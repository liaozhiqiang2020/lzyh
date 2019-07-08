package com.wl.spring.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import com.alibaba.fastjson.JSONObject;
import com.wl.logback.LogRunnable;
import com.wl.netty.http.server.MyNettyHttpServer;
import com.wl.params.CommonParams;
import com.wl.tools.IniFileOperator;
import com.wl.tools.mTimer;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

public class InitEnvironment {
	private static Logger logger = LoggerFactory
			.getLogger(MyNettyHttpServer.class);
	private String logbackConfigLocation;

	private String agentconfigpath;

	private String internalEdition;

	public void init() {
		initLogback();
		initAgentConfig();
		logger.info("�����ڲ��汾:" + internalEdition);
	}

	/**
	 * ��ʼ��logback�����ļ�
	 */
	public void initLogback() {

		//MDC.put("deviceId", "88888888");
		//MDC.put("TERMINAL_ID", "99990001");
		System.out
				.println("===========================================================================");

		InputStream input = ClassLoader.getSystemResourceAsStream(logbackConfigLocation);
		
///		this.getClass().getResource("/");//��ȡ��ǰ��Ŀ�ĸ�Ŀ¼
//		File file=null;
//		try
//		{
//		file= File.createTempFile("logback", ".xml");
//		OutputStream out = new FileOutputStream(file);
//		int read;
//		byte[] bytes = new byte[1024];
//		while ((read = input.read(bytes)) != -1) {
//			
//				out.write(bytes, 0, read);
//			
//		}
//			out.close();	
//		}catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		

		LoggerContext loggerContext = (LoggerContext) LoggerFactory
				.getILoggerFactory();
		JoranConfigurator joranConfigurator = new JoranConfigurator();
		joranConfigurator.setContext(loggerContext);
		loggerContext.reset();
		try {
			//joranConfigurator.doConfigure(file);
			joranConfigurator.doConfigure(input);
			//joranConfigurator.doConfigure(logbackConfigLocation);
		} catch (Exception e) {
			System.out.println("����ָ��logback.xml�ļ�ʧ��.");
			System.out
					.println(String.format(
							"Load logback config file error. Message: ",
							e.getMessage()));
		}
		System.out.println("����ָ��logback.xml�ļ��ɹ�.");
		StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
	}

	public void initAgentConfig() {

		String tmp = this.agentconfigpath;
		tmp = tmp.replaceAll("\\\\", "/");
		String path = tmp.substring(0, tmp.lastIndexOf("/"));

		File file = new File(path);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				System.out.println("����Ŀ¼ʧ��.");
				return;
			}
		}

		file = new File(this.agentconfigpath);
		if (file.exists()) {
			CommonParams.deviceNo = getDeviceNo(this.agentconfigpath);
			return;
		}
		JSONObject config = new JSONObject();
		
		

		JSONObject main = new JSONObject();
		main.put("version", "V1.0");// ��ǰ�汾
		main.put("versionold", "V0.1");// ��ʷ�汾
		main.put("modifydate", mTimer.getTime(2));// �汾��������
		main.put("agentname", "agent.jar");
		main.put("zzagentconfig", "D:\\runjar\\config\\ebAgent.ini");// �����ͻ��������ļ���ַ
		main.put("jarPath", "D:\\runjar\\");//����jar�ļ�����·��
		config.put("main", main);

		JSONObject agentUpdateFtp = new JSONObject();
		agentUpdateFtp.put("ftpip", "127.0.0.1");
		agentUpdateFtp.put("ftpport", "23");
		agentUpdateFtp.put("username", "lzyh");
		agentUpdateFtp.put("password", "lzyh1234");
		agentUpdateFtp.put("localpath", "D:\\runjar\\tmp\\");
		agentUpdateFtp.put("remotepath", "");
		config.put("agentftp", agentUpdateFtp);

		IniFileOperator iniFileOperator = new IniFileOperator(file);
		iniFileOperator.createIniFileByJSONObject(config);
		iniFileOperator.saveIniFile();
		CommonParams.deviceNo = getDeviceNo(this.agentconfigpath);

	}

	public String getZZAgentCOnfig(String configFile) {
		IniFileOperator fileOperator = new IniFileOperator(new File(configFile));
		String zzAgentConfig = fileOperator.getValue("main", "zzagentconfig");
		fileOperator = null;
		return zzAgentConfig;
	}

	public String getDeviceNo(String configFile) {
		String zzAgentConfig = getZZAgentCOnfig(configFile);
		if (zzAgentConfig == null || zzAgentConfig.equals(""))
			return "�����ļ�������.";
		IniFileOperator fileOperator = new IniFileOperator(new File(
				zzAgentConfig));
		String deviceNo = fileOperator.getValue("BankConfig", "TerminalNo");
		fileOperator = null;
		if (deviceNo == null || deviceNo.equals(""))
			return "�豸��δ����.";

		return deviceNo;

	}

	public String getLogbackConfigLocation() {
		return logbackConfigLocation;
	}

	public void setLogbackConfigLocation(String logbackConfigLocation) {
		this.logbackConfigLocation = logbackConfigLocation;
	}

	public String getAgentconfigpath() {
		return agentconfigpath;
	}

	public void setAgentconfigpath(String agentconfigpath) {
		this.agentconfigpath = agentconfigpath;
	}

	public String getInternalEdition() {
		return internalEdition;
	}

	public void setInternalEdition(String internalEdition) {
		this.internalEdition = internalEdition;
	}

}
