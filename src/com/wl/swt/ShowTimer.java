package com.wl.swt;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;

public class ShowTimer extends Frame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Label l1=new Label();
	Label l2=new Label();
	Panel p=null;
	
	private static ShowTimer showTimer;
	
	
	private boolean isVisible;
	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public ShowTimer(String timer,String tips) {
		// TODO Auto-generated constructor stub
		setTitle("温馨提示.");
		
		if(timer==null||timer.indexOf("该字段不存在")>-1)
		{
			timer="30";
		}
		
		
		p=new Panel(new FlowLayout());
		
		l1.setFont(new Font("Dialog", 0, 36));
		l1.setForeground(new Color(51, 255, 0));
		
		l1.setText(tips+"\r\n");
		
		//l1.setLocation(new Point(50, 50));
		l1.setBounds(50, 50, 650, 100);
		
		p.add(l1);
		
		
		
		l2.setFont(new Font("Dialog", 0, 48));
		l2.setForeground(new Color(255, 0, 0));
		
		l2.setText(timer);
		
		
		//l2.setLocation(new Point(300, 150));
		l2.setBounds(150, 150, 100, 100);
		p.add(l2);
		
		
		
		add(p);
		
		isVisible=true;
		
	}
	
	public void showTimeUp()
	{
		
		int time=Integer.parseInt(l2.getText());
		
		while(time>0)
		{
			time--;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			l2.setText(String.valueOf(time));
			
		}
		
		isVisible=false;
		this.hide();
		
	}

	public static void showTimer(String seconds,String tips)
	{
		if(showTimer==null)
		{
		Dimension   screensize   =   Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int)screensize.getWidth();
		int height = (int)screensize.getHeight();
		showTimer=new ShowTimer(seconds,tips);
		showTimer.resize(700, 200);
		showTimer.setLocation((width-700)/2,(height-200)/2);
		showTimer.disable();
		showTimer.setAlwaysOnTop(true);
		}
		showTimer.show();
        
		showTimer.showTimeUp();
		
	}
	
	public static void showTimerFull(String seconds,String tips)
	{
		
		ShowTimer showTimer1=new ShowTimer(seconds,tips);
		showTimer1.l2.hide();
		//“dialog”代表字体，1代表样式(1是粗体，0是平常的）15是字号
		showTimer1.l1.setFont(new Font("微软雅黑", 1, 60));
		
		Dimension   screensize   =   Toolkit.getDefaultToolkit().getScreenSize();
		int width = (int)screensize.getWidth();
		int height = (int)screensize.getHeight();
		
		
		
		
		showTimer1.resize(width, height);
		showTimer1.setLocation(0,0);
		showTimer1.disable();
		showTimer1.setAlwaysOnTop(true);
		showTimer1.l1.setAlignment((int) Component.LEFT_ALIGNMENT);
		showTimer1.show();
		
		
		
		
		
		
        
		showTimer1.showTimeUp();
	}
	
	public static void hiddenTimer()
	{
		///System.out.println("showTimer==null?"+(showTimer==null));
		if(showTimer!=null)
		{
			showTimer.hide();
			showTimer=null;
		}
	}
	
	

	public static void main(String[] args) {
		showTimer("5","设备正在重启，请尽快终止您的交易");
	}
}
