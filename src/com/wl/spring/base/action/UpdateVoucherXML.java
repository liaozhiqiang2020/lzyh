package com.wl.spring.base.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl.netty.http.server.MyNettyHttpServer;
import com.wl.tools.FtpTool;

public class UpdateVoucherXML implements Runnable {

	private static Logger logger = LoggerFactory
			.getLogger(MyNettyHttpServer.class);
	
	private FtpTool ftp;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	   
		if(!ftp.downLoadFiles())
		{
			logger.error(ftp.getErrormsg());
		}
		else
		{
			logger.info("下载更新凭证配置文件成功");
		}
		
		return;

	}
	
	public void update()
	{
		new Thread(this).start();
	}

	public FtpTool getFtp() {
		return ftp;
	}

	public void setFtp(FtpTool ftp) {
		this.ftp = ftp;
	}
	
	

}
