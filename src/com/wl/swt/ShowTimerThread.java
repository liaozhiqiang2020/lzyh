package com.wl.swt;

public class ShowTimerThread implements Runnable {

	private boolean flag;
	
	private String seconds;
	
	private String content;
	
	public ShowTimerThread(boolean flag,String seconds,String content) {
		// TODO Auto-generated constructor stub
		
		this.flag=flag;
		
		this.content=content;
		this.seconds=seconds;
	}
	
	public ShowTimerThread(boolean flag) {
		// TODO Auto-generated constructor stub
		
		this.flag=flag;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		if(flag)//显示倒计时
		{
			ShowTimer.showTimer(seconds, content);
		}
		else //关闭倒计时
		{
			ShowTimer.hiddenTimer();
		}
	}



	public boolean isFlag() {
		return flag;
	}



	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getSeconds() {
		return seconds;
	}

	public void setSeconds(String seconds) {
		this.seconds = seconds;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
	
}
