package com.wl.spring.base.action;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.intercept.RunAsManager;

import ch.ubique.inieditor.IniEditor;

import com.alibaba.fastjson.JSONObject;
import com.wl.http.client.HttpClient;
import com.wl.netty.http.server.MyNettyHttpServer;
import com.wl.spring.base.BaseService;
import com.wl.swt.ShowTimer;
import com.wl.tools.IniFileOperator;
import com.wl.tools.ProcessCmd;
import com.wl.tools.mTimer;

public class DeviceLogn implements Runnable {
	
	private HttpClient httpClient;
    private String ebagentconfig;
    private IniEditor editor;
    
    private IniFileOperator mainConfig;
	
	private static Logger logger = LoggerFactory
			.getLogger(MyNettyHttpServer.class);
	
	public void logons()
	{
		
		new Thread(this).start();
		
	}
	
	private boolean logon(JSONObject reqData)
	{
		int count=3;
		
		
		String httpReqUrl=mainConfig.getValue("main", "httpReqUrl");
		logger.info("后端服务器请求URL:"+httpReqUrl);
		httpClient.setUrl(httpReqUrl);
		
		
		while(count-->0)
		{
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(httpClient.sendMgr(reqData))
			{
				return true;
			}
			
			logger.error("设备签到失败:"+httpClient.getError());
			
			
			
		}
		
		return false;
	}
	
	

	private String getLocalIp() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String ip = addr.getHostAddress().toString(); // 获取本机ip
			String hostName = addr.getHostName().toString(); // 获取本机计算机名称
			System.out.println(ip);
			System.out.println(hostName);
			
			return ip;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("获取本机IP失败:" + e.getMessage());
			return "127.0.0.1";
		}
	}
	
	
	private boolean checkIP(List<String> ips,String ip)
	{
		boolean isExist=false;
		
		for(int i=0,len=ips.size();i<len;i++)
		{
			if(ips.get(i).equals(ip))
			{
				isExist=true;
				break;
			}
		}
		return isExist;
	}
	
	
	private List<String> getAllIP()
	{
		List<String> ips=new ArrayList<String>();
		
		Enumeration<NetworkInterface> nets = null;
		try {
			nets = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ips;
		}
				for (NetworkInterface netint : Collections.list(nets))
				{
				try {
					if (null != netint.getHardwareAddress()) {
					List<InterfaceAddress> list = netint.getInterfaceAddresses();
					for (InterfaceAddress interfaceAddress : list) {
					String localip=interfaceAddress.getAddress().toString();
					localip=localip.replace("/", "");
					//logger.info("本机IP:"+localip);
					ips.add(localip);
					}
					}
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				logger.info(ips.toString());
				return ips;
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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		logger.info("设备签到.");
		editor=new IniEditor();
		try {
			editor.load(new File(ebagentconfig));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.error("加载配置文件失败:"+ebagentconfig+","+e.getMessage());
		}
		JSONObject header=new JSONObject();
		header.put("dateTime", mTimer.getTime(4));
		header.put("transcode", "DEV002");
		
		JSONObject body=new JSONObject();
		body.put("deviceno", editor.get("BankConfig", "TerminalNo"));
		
		JSONObject json=new JSONObject();
		json.put("body", body);
		json.put("head", header);
		
		boolean isSuccess=logon(json);
		
		logger.info("返回信息:"+httpClient.getError());
		if(!isSuccess)//签到交易失败
		{
			ShowTimer.showTimerFull("3600", httpClient.getError());
			return;
		}
		
		
		
		JSONObject respData=httpClient.getResData();
		
		String dtStr=respData.getString("DateTime");
				logger.info("系统返回日期时间:"+dtStr);
		String[] dt=dtStr.split("\\ ");
		if(dt.length==2)
		{
			ProcessCmd cmd=new ProcessCmd();
			if(!cmd.sysNT(dt[0], dt[1]))
			{
				logger.info("日期时间同步失败.");
			}
			else
			{
				logger.info("日期时间同步成功.");
			}
//			String filepath="D:\\runjar\\chDT.bat";
//			try {
//				BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filepath))));
//				bw.write("date "+dt[0]+"\r\n");
//				bw.write("time "+dt[1]+"\r\n");
//			    bw.close();
//			    cmd.processBatFile("D:\\runjar\\chDT.bat");
//			    
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				//e.printStackTrace();
//				logger.error("生成批处理文件失败:"+e.getMessage());
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				//e.printStackTrace();
//				logger.error("生成批处理文件失败1:"+e.getMessage());
//			}
//			
			
			cmd=null;
		}
		else
		{
			logger.info("日期同步数据有误:"+dtStr);
		}
		
		
				
		
		String rtIp=respData.getJSONObject("dev").getString("deviceIp");//
		logger.info("返回IP:"+rtIp);
		String realIp=getLocalIp();
		
//		if(!realIp.equals(rtIp))
//		{
//			ShowTimer.showTimerFull("3600", "设备IP地址不正确,请联系工作人员重新配置");
//			return;
//		}
//		
		if(!checkIP(getAllIP(), rtIp))
		{
			ShowTimer.showTimerFull("3600", "设备IP地址不正确,请联系工作人员重新配置");
			return;
		}
		
		
		return;
		
	}

	public IniFileOperator getMainConfig() {
		return mainConfig;
	}

	public void setMainConfig(IniFileOperator mainConfig) {
		this.mainConfig = mainConfig;
	}
	
	

}
