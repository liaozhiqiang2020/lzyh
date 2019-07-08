package com.wl.utils.image;

import java.io.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.junit.Test;

public class ShotsnapScreen {
	private String errormsg="";
	private String filename;
	private String folder;
	public  boolean shotsnap(String filename,String folder)
	{
		boolean isSuccess=false;
		try
		{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        Robot robot = new Robot();
        BufferedImage image = robot.createScreenCapture(screenRectangle);
        // ��ͼ�����·�� 
        File screenFile = new File(folder);    
        
        //�ж��ļ��Ƿ���ڣ������ھʹ����ļ�
        if(!screenFile.exists()&& !screenFile .isDirectory()) {
            screenFile.mkdirs();
        }
        
        File f = new File(folder, filename);        
        ImageIO.write(image, "png", f);
        isSuccess=true;
		}catch (AWTException e) {
			// TODO: handle exception
			errormsg=e.getMessage();
		}catch (IOException e) {
			// TODO: handle exception
			errormsg=e.getMessage();
		}
		
		return isSuccess;
	}
	public String getErrormsg() {
		return errormsg;
	}
	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}
	
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	
	public boolean captureScreen()
	{
		return shotsnap(this.filename, this.folder);
	}
	
	@Test
	public void test()
	{
		shotsnap("tmp.jpg","D:\\img\\");
	}
	
	

}
