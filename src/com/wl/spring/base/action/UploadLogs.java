package com.wl.spring.base.action;

import java.io.File;
import java.io.FilenameFilter;

import com.alibaba.fastjson.JSONObject;
import com.wl.params.CommonParams;
import com.wl.spring.base.BaseService;
import com.wl.tools.FtpTool;
import com.wl.tools.mTimer;

public class UploadLogs extends BaseService{
	
	private String section;
	
	private FtpTool ftp;
	
	@Override
	public boolean doAction(JSONObject request) {
		// TODO Auto-generated method stub
		String logDate=request.getString("logdate");//ѡ���ϴ���־������
		if(logDate==null||logDate.trim().equals(""))logDate=mTimer.getTime(2);
		
		
		final String logCreateDate=logDate;
		
		if(request.containsKey("section"))
		{
			section=request.getString("section");//��ͬ����־�в�ͬ�����ã�Ĭ����agentlog
		}
		
		ftp.setSection(section);
		ftp.initialParams();
		
		String localDirectory=ftp.getLocalpath();
		
		File directory=new File(localDirectory);
		
		if(!directory.exists())
		{
			setRetInfo("�ļ�:"+directory.getAbsolutePath()+"������.");
			return false;
		}
		
		if(directory.isFile())//������ļ�����Ŀ¼,ֱ���ϴ��ļ�
		{
			ftp.setRemotetmppath(ftp.getRemotepath()+CommonParams.deviceNo+"\\"+logDate+"\\");
			if(!ftp.upload(localDirectory))
			{
				this.setRetInfo(ftp.getErrormsg());
				return false;
			}
			else
			{
				this.setRetInfo("0000","�ϴ���־�ɹ�");
				return true;
			}
		}
		
		File[] uploadFiles=directory.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				// TODO Auto-generated method stub
				
				//System.out.println(logCreateDate);
				
			   File filTmp=new File(dir.getAbsolutePath()+"\\"+name);
			   
			   if(filTmp.isDirectory())return false;
			   
			   if(!filTmp.getName().endsWith(".log")&&!filTmp.getName().endsWith(".txt"))return false;
			   
			  long time= filTmp.lastModified();
			   
			  String modifiedDate=mTimer.getDateByMSEL(time);
			  
			  if(!modifiedDate.equals(logCreateDate))return false;
				return true;
			}
			
		});

		
		ftp.setRemotetmppath(ftp.getRemotepath()+CommonParams.deviceNo+"\\"+logCreateDate+"\\");//����Զ�̹���Ŀ¼
		
		if(!ftp.batchUpload(uploadFiles))
		{
			setRetInfo(ftp.getErrormsg());
			
			return false;
		}
		
	   
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
