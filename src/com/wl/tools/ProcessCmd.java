package com.wl.tools;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl.netty.http.server.MyNettyHttpServer;

public class ProcessCmd {
	
	private static Logger logger = LoggerFactory
			.getLogger(MyNettyHttpServer.class);
	
	
	private String cmd;
	private String result;
	
	private String monitoredProc;//����صĽ���
	
	private String Pid;
	
	private List<String> pids;
	
	public ProcessCmd() {
		// TODO Auto-generated constructor stub
		pids=new ArrayList<String>();
	}
	
	/**
	 * ������ͨ����������
	 * @return
	 */
	public boolean processCmd()
	{
		boolean isSuccess=false;
		
		try
		{
			String cmd_="cmd.exe /c  "+cmd;
		Process process=Runtime.getRuntime().exec(cmd_);
		
		 System.out.println("ִ������:"+cmd_);
		StringBuilder sb=new StringBuilder();
		/*ByteArrayOutputStream baos=new ByteArrayOutputStream();
		InputStream os=process.getInputStream();
		byte b[]=new byte[1024];
		while(os.read(b)>0)
		{
			baos.write(b);
			String s=baos.toString();
			sb.append(s);
		}
		*/
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
         String line = null;
         while ((line = bufferedReader.readLine()) != null) {
            sb.append(line).append("\n");
         }
		
		
		this.result=sb.toString().toUpperCase();
		
		//System.out.println("result="+result);
		
		isSuccess=true;
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			this.result=e.getMessage();
		}

		return isSuccess;
	}
	public boolean processCmd(String cmd)
	{
		this.cmd=cmd;
		return this.processCmd();
	}
	
	
	/**
	 * ִ��exe����
	 * @return
	 */
	public boolean processExe()
	{
		boolean isSuccess=false;
		
		try
		{
			String cmd_="cmd.exe /k    "+this.cmd;
			System.out.println("ִ������:"+cmd);
		Process process=Runtime.getRuntime().exec(cmd_);
		
		isSuccess=true;
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getMessage());
			this.result=e.getMessage();
		}

		return isSuccess;
	} 
	
	public boolean processExe(String process)
	{
		this.monitoredProc=process;
		this.cmd=process;
		return processExe();
	}
	
	/**
	 * ��ؽ���
	 * @return
	 */
	public boolean monitorProcess()
	{
		boolean isExist=false;
		this.cmd="tasklist";
		processCmd();
	    if(this.result.indexOf(this.monitoredProc.toUpperCase())>-1)
	    {
	    	isExist=true;
	    }
		
		
		return isExist;
	}
	
	/**
	 * ���ָ�������Ƿ����
	 * @return
	 */
	public boolean checkProcessExist()
	{
		boolean isExist=false;
		this.cmd="tasklist -fi \" imagename eq "+this.monitoredProc+"\"";
		processCmd();
		
	    if(this.result.indexOf(this.monitoredProc)>-1)
	    {
	    	System.out.println("���� "+this.monitoredProc+"����.");
	    	
	    	System.out.println(this.result);
	    	String[] rs=this.result.split("\n");
	    	System.out.println("length="+rs.length);
	    	
	    	for(int i=0;i<rs.length;i++)
	    	{
	    		if(rs[i].indexOf(this.monitoredProc)<0)continue;
	    		
	    		String[] tmps=rs[i].split("\\s+");
	    		
	    		pids.add(tmps[1]);
	    	}
	    	
	    	System.out.println(pids.toString());
	    	
	    	isExist=true;
	    }
	    else
	    {
	    	System.out.println("���� "+this.monitoredProc+"������.");
	    }
		return isExist;
	}
	
	
	public boolean checkProcessExist(String processname)
	{
		this.monitoredProc=processname;
		
		return checkProcessExist();
	}
	
	public boolean monitorProcess(String monitoredProc)
	{
		this.monitoredProc=monitoredProc;
		return monitorProcess();
	}
	
	/**
	 * ��������
	 * @return
	 */
	public boolean killProcess()
	{
		
		if(!monitorProcess())
		{
			this.result="����"+this.monitoredProc+"δ����.";
			return true;
		}
		
		
	    this.cmd=" C:\\windows\\system32\\taskkill /F /im "+this.monitoredProc;
	    
	    processCmd();
	    
	    if(monitorProcess())
	    {
	    	return false;
	    }
		
	    return true;
		
	}
	public boolean killProcess(String monitoredProc)
	{
		this.monitoredProc=monitoredProc;
		return killProcess();
	}
	
	/**
	 * ��PID��������
	 * @param Pid
	 * @return
	 */
	public boolean killProcessByPid()
	{
		
		if(!monitorProcess())
		{
			this.result="����"+this.monitoredProc+"δ����.";
			return true;
		}
		
		
	    this.cmd=" C:\\windows\\system32\\taskkill /PID "+this.Pid+" /F /T ";
	    
	   
	    processCmd();
	    
	    if(monitorProcess())
	    {
	    	return false;
	    }
		
	    return true;
		
	}
	
	/**
	 * �����̱�Ž�������
	 * @param Pid
	 * @return
	 */
	public boolean killProcessByPid(String pid)
	{
	     this.Pid=pid;
	     return killProcessByPid();
	}
	
	
	public boolean killProcessByPids()
	{
		boolean isSuccess=true;
		for(int i=0;i<pids.size();i++)
		{
			if(!killProcessByPid(pids.get(i)))
			{
				isSuccess=false;
				break;
			}
		}
		
		return isSuccess;
	}
	
	/**
	 * ͬ������ʱ��
	 * @return
	 */
	public boolean sysNT(String dataStr_,String timeStr_)
	{
		 try {
	            String osName = System.getProperty("os.name");
	            // Window ϵͳ
	            if (osName.matches("^(?i)Windows.*$")) {
	                String cmd;
	                // ��ʽ��yyyy-MM-dd
	                cmd = " cmd /c date " + dataStr_;
	                Runtime.getRuntime().exec(cmd);
	                // ��ʽ HH:mm:ss
	                cmd = " cmd /c time " + timeStr_;
	                Runtime.getRuntime().exec(cmd);
	                logger.info("windows ʱ���޸�");
	                return true;
	            } else if (osName.matches("^(?i)Linux.*$")) {
	                // Linux ϵͳ ��ʽ��yyyy-MM-dd HH:mm:ss   date -s "2017-11-11 11:11:11"
	                FileWriter excutefw = new FileWriter("/usr/updateSysTime.sh");
	                BufferedWriter excutebw=new BufferedWriter(excutefw);
	                excutebw.write("date -s \"" + dataStr_ +" "+ timeStr_ +"\"\r\n");
	                excutebw.close();
	                excutefw.close();
	                String cmd_date ="sh /usr/updateSysTime.sh";
	                Runtime.getRuntime().exec(cmd_date);
	                logger.info("cmd :" + cmd_date + " date :" + dataStr_ +" time :" + timeStr_);
	                logger.info("linux ʱ���޸�");
	                return true;
	            } else {
                    logger.error("����ϵͳ�޷�ʶ��");
                    return false;
	            }
	        } catch (IOException e) {
	            e.getMessage();
	            logger.error(e.getMessage());
	           return false;
	        }
	}
	
	
	public boolean processBatFile(String fileFullpath)
	{
		try
		{
		String cmd="cmd /c start "+fileFullpath;
		System.out.println("cmd="+cmd);
		Runtime.getRuntime().exec(cmd);
		return true;
		}catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		
	}
	
	
	@Test
	public void test()
	{
		String processpath="D:\\program files\\��������.9.61.412.1420\\";
		String process="taskmgr.exe";
		
		
	    this.processCmd("net start tomcat6");
	    System.out.println("result:"+this.result);
		process=process.toUpperCase();
//		if(monitorProcess("lenovodrvsrv"))
//		{
//			System.out.println("����"+this.monitoredProc+"����.");
//		}
		
//		processExe("\""+processpath+process+"\"");
		
//		if(killProcess(process))
//		{
//			System.out.println("�رս���:"+this.monitoredProc+"�ɹ�.");
//		}
//		else
//		{
//			System.out.println("�رս���:"+this.monitoredProc+"ʧ��.");
//		}
		//this.monitoredProc=process;
		
		this.checkProcessExist(process);
		
		this.killProcessByPids();
		
//		if(killProcessByPid("15116"))
//		{
//			System.out.println("�رս���:"+this.monitoredProc+"�ɹ�.");
//		}
//		else
//		{
//			System.out.println("�رս���:"+this.monitoredProc+"ʧ��.");
//		}
		
		
		//System.out.println(this.result);
	}
	
	
	
	
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}

	public String getMonitoredProc() {
		return monitoredProc;
	}

	public void setMonitoredProc(String monitoredProc) {
		this.monitoredProc = monitoredProc.toUpperCase();
	}

	public String getPid() {
		return Pid;
	}

	public void setPid(String pid) {
		Pid = pid;
	}
	
	
	
	

}
