package com.wl.spring.base.action;

import java.io.File;

import com.alibaba.fastjson.JSONObject;
import com.wl.spring.base.BaseService;
import com.wl.swt.ShowTimerThread;
import com.wl.tools.FtpTool;
import com.wl.tools.IniFileOperator;
import com.wl.tools.SystemOpr;

public class UpGradeDriver extends BaseService {
	
    private String section;
	
	private FtpTool ftp;

	@Override
	public boolean doAction(JSONObject request) {
		// TODO Auto-generated method stub
		
		ftp.setSection(section);
		ftp.initialParams();
		
	     String version="";
		if(request.containsKey("version"))
		{
			version=request.getString("version");
		}
		
		if(request.containsKey("filename"))
		{
			ftp.setFilename(request.getString("filename"));
		}
		
	    if(!ftp.downLoadFile(version))//�����ļ�
	    {
	    	setRetInfo(ftp.getErrormsg());
	    	return false;
	    }
		
	    IniFileOperator iniOP=new IniFileOperator(new File(ftp.getConfigname()));
	    
	    iniOP.setSection("main");
	    String explorer=iniOP.getValue("explorer");//��ȡ�������������
	    iniOP.clear();
	    
	    ShowTimerThread showTips= new ShowTimerThread(true, "60", "���������豸,�뾡��������Ĳ���.");
	    showTips.run();
	    SystemOpr.reboot(10);
	    
	    SystemOpr.killProcess(explorer);//�ر����������
	    
	    SystemOpr.runProcess(ftp.getLocalpath()+ftp.getFilename());
	    
		return true;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public FtpTool getFtp() {
		return ftp;
	}

	public void setFtp(FtpTool ftp) {
		this.ftp = ftp;
	}

}
