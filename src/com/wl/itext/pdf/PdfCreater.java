package com.wl.itext.pdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ubique.inieditor.IniEditor;

import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.wl.data.check.SHA512Check;
import com.wl.ftp.FtpClient;
import com.wl.spring.base.action.UpdateIniFile;
import com.wl.tools.FtpTool;
import com.wl.tools.mTimer;

public abstract class PdfCreater {

	private static Logger logger=LoggerFactory.getLogger(PdfCreater.class);
	
	
	private Document doc;
	private Font font;

	private PdfWriter pdfWriter;

	private JSONObject jsonData;

	private String tradeType;// ��������

	private String tradeName;// ��������
	
	private String fullFilePathName;//�����ļ�ȫ·����
	
	private String error;//������Ϣ
	
	
	private FtpTool ftp;//
	
	private String SHA512CheckValue;//�ļ�У��
	
	/**
	 * ������׺��
	 */
	private String fileName;//�ļ���
	
	public IniEditor iniEditor;

	public PdfCreater(JSONObject data) {
		// TODO Auto-generated constructor stub
		jsonData = data;
	}

	
	
	
	public IniEditor getIniEditor() {
		return iniEditor;
	}




	public void setIniEditor(IniEditor iniEditor) {
		this.iniEditor = iniEditor;
	}




	/**
	 * ��ʼ���ĵ�
	 */
	public boolean initPdfDoc() {
		
		
		fullFilePathName=iniEditor.get("voucher", "savePath")+mTimer.getTime(2)+"\\";
		
		logger.error("�ļ�ȫ·������"+fullFilePathName);
		
		File tmp=new File(fullFilePathName);
		
		if(!tmp.exists())
		{
			if(!tmp.mkdirs())
			{
				logger.error("����Ŀ¼ʧ��:"+fullFilePathName);
				error="����Ŀ¼ʧ��:"+fullFilePathName;
				return false;
			}
		}
		
		fileName=jsonData.getString("IDCode")+mTimer.getTime(4);
		
		
		fullFilePathName+=jsonData.getString("IDCode")+mTimer.getTime(4)+".pdf";
		
		doc = new Document();
		// ��,��,��,��
		doc.setMargins(0, 0, 0, 0);

		try {
			PdfWriter pdfWriter = PdfWriter.getInstance(doc, new FileOutputStream(
					fullFilePathName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			return false;
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			return false;
		}

		doc.open();
		
		return true;
	}

	public Font getFont() {
		BaseFont baseFont = null;
		try {
			baseFont = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
					BaseFont.NOT_EMBEDDED);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Font font = new Font(baseFont);
		return font;
	}
	
	
	public void finishPdfDoc()
	{
		if(doc!=null)doc.close();
		if(pdfWriter!=null)pdfWriter.close();
	}

	public String getTradeType() {
		return tradeType;
	}

	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public abstract boolean createPdfDocument();

	public Document getDoc() {
		return doc;
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public String getFullFilePathName() {
		return fullFilePathName;
	}

	public void setFullFilePathName(String fullFilePathName) {
		this.fullFilePathName = fullFilePathName;
	}

	public JSONObject getJsonData() {
		return jsonData;
	}

	public void setJsonData(JSONObject jsonData) {
		this.jsonData = jsonData;
	}

	
	
	public String getError() {
		return error;
	}




	public void setError(String error) {
		this.error = error;
	}




		//PDFתJPG
		public  boolean PDFToImage()
		{
			try
			{
			 File file = new File(fullFilePathName);
			
			       PDDocument doc = PDDocument.load(file);
			       PDFRenderer renderer = new PDFRenderer(doc);
			       int pageCount = doc.getNumberOfPages();
			       
			       if(pageCount>0)
			       {
			    	   BufferedImage image = renderer.renderImage(0, 1.5f);
			    	   
			           ImageIO.write(image,"jpg",new File(fullFilePathName.replace(".pdf", ".jpg")));
			       }
			       doc.close();
			       
			       return true;
			}catch (Exception e) {
				// TODO: handle exception
				error=e.getMessage();
				return false;
			}
			    
		}
		
		//�ϴ������ļ�������У���ļ�
		public  boolean Upload_DownLoadFtp()
		{
			
			String FtpFileName=fullFilePathName.replace(".pdf", ".jpg");
			
			SHA512Check check=new SHA512Check();
			
			if(!check.getCheckValue(new File(FtpFileName)))
			{
				error="�ϴ�ǰ�ļ�У��ʧ��!";
				return false;
			}
			
			SHA512CheckValue=check.getCheckValue();
			
			ftp.setRemotetmppath(iniEditor.get("voucherFtp", "remotepath")+mTimer.getTime(2)+"/");
			ftp.setRemotepath(iniEditor.get("voucherFtp", "remotepath")+mTimer.getTime(2)+"/");
			
		    ftp.setFilename(fileName+".jpg");
		    ftp.loginByUnix();
		    
		     if(!ftp.upload(FtpFileName))
		     {
		    	 error="�ϴ��ļ�ʧ��:"+ftp.getErrormsg();
			     return false;
		     }
		     
		     
		     File filTmp=new File(FtpFileName);
		     
		     if(filTmp.exists())
		     {
		    	 System.out.println(FtpFileName+" �ļ�����.");
		     }
		     else
		     {
		    	 System.out.println(FtpFileName+" �ļ�������.");
		     }
		     
		     
		    // System.out.println("ɾ���ļ����:"+filTmp.delete());
		     
		     try {
		    	 logger.error("�ϴ��ȴ���.....");
				Thread.sleep(2000);
				logger.error("�ϴ��ɹ�.....");
			} catch (Exception e) {
				// TODO: handle exception
			}
		     
		     
		     ftp.setLocalpath(iniEditor.get("voucherFtp", "localpath")+mTimer.getTime(2));
		     
//		     logger.error("2222222�����ļ�Ŀ¼:"+ftp.getRemotepath()+",�ļ�����"+this.getFileName()+".jpg");
		     if(!ftp.downloadFile(ftp.getRemotepath(), this.getFileName()+".jpg"))
		     {
		    	 error="�����ļ�ʧ��:"+ftp.getErrormsg();
		    	 return false;
		     }
		     
		     
		     ftp.logoutAuto();
		     
		     
		     if(!check.check(SHA512CheckValue, new File(FtpFileName)))
		     {
		    	 error="���ص��ļ���Ԫʼ�ļ�У�鲻ƥ��";
		    	 return false;
		     }
		     
		     
		     return true;
		     
		     
		}
		
		//����HTTP����
		
		public  void AddVoucherHttp(String voucherID)
		{
			
			JSONObject body=new JSONObject();
			body.put("str", "");
			body.put("IdCode", "320405198806102511");
			
			JSONObject head=new JSONObject();
			
			head.put("dateTime", "0727165144");
			head.put("transcode", "VH002");
			head.put("deviceno", "55550002");
			head.put("brc", "10187");
			head.put("teller", "4076");
			head.put("channel", "71");
			head.put("slhdm", "004478210");
			head.put("seqNo", "001799");
			head.put("devip", "98.10.65.196");
			
			JSONObject bw=new JSONObject();
			bw.put("body", body);
			bw.put("head", head);
			
			String url="";
			url=iniEditor.get("Http", "Url");
	         Map<String, Object> parms = new HashMap<String, Object>();
			
		    parms.put("data", bw);
			
			
		    System.out.println(bw.toJSONString());
		    
		  
		    Header header=Header.create();
		    header.set("Content-Type","application/json");
		    
		  Response resp=  Http.post3(url, bw.toJSONString(), header, 30*1000);
		   
		  String content=resp.getContent();
		  content=content.replaceAll("\\\\", "");
		  
		  content=content.replaceAll("^\\\"", "");
		  content=content.replaceAll("\\\"$", "");
		  System.out.println(content);
		  
		  
		  JSONObject jsonObject=JSONObject.parseObject(content);
		  JSONObject body1=jsonObject.getJSONObject("respBody");
		  System.out.println(body1.getString("Message"));
	      
			
		}




		public String getFileName() {
			return fileName;
		}




		public void setFileName(String fileName) {
			this.fileName = fileName;
		}




		public FtpTool getFtp() {
			return ftp;
		}




		public void setFtp(FtpTool ftp) {
			this.ftp = ftp;
		}
	
		
		
		
}
