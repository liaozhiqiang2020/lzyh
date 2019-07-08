package com.wl.spring.base.action;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ubique.inieditor.IniEditor;

import com.alibaba.fastjson.JSONObject;
import com.wl.itext.pdf.PdfCreater;
import com.wl.itext.pdf.QuickBorrowVoucher;
import com.wl.itext.pdf.VoucherPdfCreater;
import com.wl.spring.base.BaseService;
import com.wl.tools.FtpTool;

public class CreateVoucher extends BaseService{

	private static Logger logger=LoggerFactory.getLogger(CreateVoucher.class);
	
	private FtpTool ftp;
	
	private String ftpSection;
	
	@Override
	public boolean doAction(JSONObject request) {
		// TODO Auto-generated method stub
		
		String tradeType=request.getString("TradeCode");
		
		IniEditor iniEditor=new IniEditor();
		try
		{
		iniEditor.load(new File(this.getConfigFile()));
		}
		catch (Exception e) {
			// TODO: handle exception
			this.setRetInfo("9901", "���������ļ�ʧ��:"+this.getConfigFile()+",Error:"+e.getMessage());
			return false;
		}
		
		
		PdfCreater creater=null;
		
		String voucherDir=iniEditor.get("main", "voucherDir");
		
		if(voucherDir==null||voucherDir.equals(""))
		{
			iniEditor=null;
			this.setRetInfo("9901", "ƾ֤Ŀ¼������,����������.");
			return false;
		}
		
		
		File tmp=new File(voucherDir+tradeType+".xml");
		
		if(!tmp.exists())
		{
			iniEditor=null;
			this.setRetInfo("9901", "ƾ֤�ļ�"+tmp.getAbsolutePath()+"������,����������");
			return false;
		}
		
		
		request.put("voucherFile", voucherDir+tradeType+".xml");//ƾ֤�����ļ�
		
		
		/*
		if("YJDLuanApply".equals(tradeType))//һ��������
		{
			creater=new QuickBorrowVoucher(request);
			
			creater.setIniEditor(iniEditor);
			creater.initPdfDoc();
			if(!creater.createPdfDocument())
			{
				creater.finishPdfDoc();
				this.setRetInfo("9901", "����ƾ֤ʧ��");
				return false;
			}
			else
			{
				
				creater.finishPdfDoc();
				
				if(!creater.PDFToImage())
				{
					this.setRetInfo("9901", "pdfתjpgʧ��:"+creater.getError());
					return false;
				}
				ftp.setSection(ftpSection);
				
				ftp.initialParams();
				
				creater.setFtp(ftp);
				
				if(!creater.Upload_DownLoadFtp())
				{
					this.setRetInfo("9901", creater.getError());
					return false;
				}
				
				
				this.setRespBody("filename", creater.getFileName());
			}
			
			
			
		}
		else
		{
			this.setRetInfo("9901", "ƾ֤���Ͳ�����");
			return false;
		}
		*/
		
		creater=new VoucherPdfCreater(request);
		
		creater.setIniEditor(iniEditor);
		creater.initPdfDoc();
		if(!creater.createPdfDocument())
		{
			creater.finishPdfDoc();
			this.setRetInfo("9901", "����ƾ֤ʧ��");
			return false;
		}
		else
		{
			
			creater.finishPdfDoc();
			
			if(!creater.PDFToImage())
			{
				this.setRetInfo("9901", "pdfתjpgʧ��:"+creater.getError());
				return false;
			}
			ftp.setSection(ftpSection);
			
			ftp.initialParams();
			
			creater.setFtp(ftp);
			
			if(!creater.Upload_DownLoadFtp())
			{
				this.setRetInfo("9901", creater.getError());
				return false;
			}
			
			
			this.setRespBody("filename", creater.getFileName());
		}
		
		
		
		
		
		
		return true;
	}

	public FtpTool getFtp() {
		return ftp;
	}

	public void setFtp(FtpTool ftp) {
		this.ftp = ftp;
	}

	public String getFtpSection() {
		return ftpSection;
	}

	public void setFtpSection(String ftpSection) {
		this.ftpSection = ftpSection;
	}
	
	
}
