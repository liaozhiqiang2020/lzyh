package com.wl.spring.base.action;

import com.alibaba.fastjson.JSONObject;
import com.wl.spring.base.BaseService;
import com.wl.tools.FtpTool;
import com.wl.tools.mTimer;
import com.wl.utils.image.ShotsnapScreen;

public class ScreenCaptrue extends BaseService {

	private FtpTool ftp;
	
	@Override
	public boolean doAction(JSONObject request) {
		// TODO Auto-generated method stub
//		
//		if(request.containsKey("section"))
//			{
//			ftp.setSection(request.getString("section"));
//			
//			}
		
		
		ftp.initialParams();
		ShotsnapScreen shotsnapScreen=new ShotsnapScreen();
		
		String filename=mTimer.getTime(4)+".png";
		shotsnapScreen.setFolder(ftp.getLocalpath()+mTimer.getTime(2)+"\\");
		shotsnapScreen.setFilename(filename);
		if(!shotsnapScreen.captureScreen())
		{
			setRetInfo(shotsnapScreen.getErrormsg());
			return false;
		}
		
		//ftp.setRemotepath(ftp.getRemotepath()+getRespHead("deviceNo")+"\\"+mTimer.getTime(2)+"\\");
		ftp.setRemotetmppath(ftp.getRemotepath()+getRespHead("deviceNo")+"\\"+mTimer.getTime(2)+"\\");
		
		
		
		ftp.setLocalpath(shotsnapScreen.getFolder());
		ftp.setFilename(filename);
		
		//System.out.println(ftp.getRemotepath());
		if(!ftp.uploadFile())
		{
			setRetInfo(ftp.getErrormsg());
			return false;
		}
		
		setRespBody("filename", filename);
		
		return true;
	}

	public FtpTool getFtp() {
		return ftp;
	}

	public void setFtp(FtpTool ftp) {
		this.ftp = ftp;
	}
	
	

}
