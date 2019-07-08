package com.wl.itext.pdf;

import java.io.File;
import java.util.Iterator;
import java.util.Map;


import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.wl.tools.ReaderVoucherXML;
import com.wl.tools.mTimer;

public class VoucherPdfCreater extends PdfCreater {

	private PdfPTable table;

	public VoucherPdfCreater(JSONObject data) {
		super(data);
		// TODO Auto-generated constructor stub

		table = new PdfPTable(4);
		table.setWidthPercentage(100);
	}

	@Override
	public boolean createPdfDocument() {
		// TODO Auto-generated method stub

		if(!createHeader())return false;
		if(!createBody(new File(getJsonData().getString("voucherFile"))))return false;
		if(!createStatement())return false;
		if(!createImg())return false;
		if(!createTail())return false;
		
		
		try {
			getDoc().add(table);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}

	public boolean createHeader() {

		table.getDefaultCell().setBorder(Rectangle.BOX);
		table.getDefaultCell().setBorderWidthBottom(5f);
		table.getDefaultCell().setBorderWidth(1f);
		table.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
		table.getDefaultCell().setUseBorderPadding(true);
		table.getDefaultCell().setPadding(5f);
		PdfPCell cell;
		cell = new PdfPCell();
		cell.setColspan(4);
        
		Paragraph headers = new Paragraph();
	        
		Chunk title = new Chunk();
	
		title.setFont(getFont());
		title.append(""
				+ getJsonData().getString("TradeName"));

		
		headers.add(title);

		headers.add(Chunk.NEWLINE);
		headers.add(Chunk.NEWLINE);


		
//		headers.add(new Chunk("  申请日期: " + mTimer.getTime(0)
//				+ "                     ",getFont()));
//		headers.add(Chunk.NEWLINE);
//		headers.add(Chunk.NEWLINE);
//
//		
		headers.setAlignment(1);
		
		cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中
		cell.addElement(headers);
		
		
		headers = new Paragraph();
		
		headers.add(new Chunk("  申请日期: " + mTimer.getTime(0)
				+ "                     ",getFont()));
		
		headers.add(Chunk.NEWLINE);
		headers.add(Chunk.NEWLINE);
		
		cell.addElement(headers);
		
		
		
		

		table.addCell(cell);

		// 增加黑色分割线
		cell = new PdfPCell();
		cell.setColspan(4);
		cell.setBorder(1);
		cell.setBackgroundColor(BaseColor.GRAY);
		cell.addElement(new Chunk(" "));
		table.addCell(cell);

		return true;

	}


	public boolean createBody(File voucherTypeConfig) {
		
		
		
		
		ReaderVoucherXML readerVoucherXML=new ReaderVoucherXML();
		
		if(!readerVoucherXML.readerVoucher(voucherTypeConfig))
		{
			return false;
		}
		
		Map<String, String> fields=readerVoucherXML.getFields();
		int len=fields.size();
		
		Iterator<String> keys=fields.keySet().iterator();
		
		for(int i=0;i<len;i++)
		{
			String key=keys.next();
			table.addCell(new Phrase(fields.get(key), getFont()));
			table.addCell(new Phrase(getJsonData().getString(key), getFont()));
		}
		
		if(len%2!=0)
		{
			table.addCell(new Phrase("", getFont()));
			table.addCell(new Phrase("", getFont()));
		}
		
		
         
		
		
		
		

		PdfPCell cell = new PdfPCell();
		cell.setColspan(4);
		cell.setBorder(1);
		cell.setBackgroundColor(BaseColor.GRAY);
		cell.addElement(new Chunk(" "));
		table.addCell(cell);

		return true;
	}

	public boolean createStatement() {
		try
		{
		PdfPCell cell = new PdfPCell();
		cell.setColspan(4);

		String remask = "";//getJsonData().getString("remark")
		if(getJsonData().containsKey("remark"))
		{
			remask=getJsonData().getString("remark");
		}

		Paragraph paragraph = new Paragraph(new Phrase(remask, getFont()));
		paragraph.add(Chunk.NEWLINE);

		Chunk sign = new Chunk("                          客户签名:");

		paragraph.add(sign);

		Image img = Image.getInstance(iniEditor.get("voucher", "Signature"));
		img.setAlignment(Image.TEXTWRAP);
		img.setBorder(Image.BOX);
		img.scaleToFit(100, 100);// 大小
		cell.addElement(paragraph);
		cell.addElement(img);
		table.addCell(cell);

		cell = new PdfPCell();
		cell.setColspan(4);
		cell.setBorder(1);
		cell.setBackgroundColor(BaseColor.GRAY);
		cell.addElement(new Chunk(" "));
		table.addCell(cell);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public boolean createImg() {
		try
		{
		PdfPCell cell = new PdfPCell();
		cell.setColspan(2);
		Image img = Image.getInstance(iniEditor.get("voucher", "FrontImg"));
		img.setAlignment(Image.TEXTWRAP | Image.LEFT);
		img.setBorder(Image.BOX);
		img.scaleToFit(200, 200);// 大小
		cell.addElement(img);
		table.addCell(cell);

		cell = new PdfPCell();
		cell.setColspan(2);
		img = Image.getInstance(iniEditor.get("voucher", "BackImg"));
		img.setAlignment(Image.TEXTWRAP | Image.LEFT);
		img.setBorder(Image.BOX);
		img.scaleToFit(200, 200);// 大小
		cell.addElement(img);

		table.addCell(cell);

		cell = new PdfPCell();
		cell.setColspan(1);
		img = Image.getInstance(iniEditor.get("voucher", "IDImage"));
		img.setAlignment(Image.TEXTWRAP | Image.LEFT);
		img.setBorder(Image.BOX);
		img.scaleToFit(100, 100);// 大小
		cell.addElement(img);
		table.addCell(cell);

		cell = new PdfPCell();
		cell.setColspan(1);
		img = Image.getInstance(iniEditor.get("voucher", "CheckImg"));
		img.setAlignment(Image.TEXTWRAP | Image.LEFT);
		img.setBorder(Image.BOX);
		img.scaleToFit(100, 100);// 大小
		cell.addElement(img);
		table.addCell(cell);

		cell = new PdfPCell();
		cell.setColspan(2);
		img = Image.getInstance(iniEditor.get("voucher", "PhotoImg"));
		img.setAlignment(Image.TEXTWRAP | Image.LEFT);
		img.setBorder(Image.BOX);
		img.scaleToFit(150, 150);// 大小
		cell.addElement(img);
		table.addCell(cell);
		}catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		return true;
	}

	public boolean createTail() {
		PdfPCell cell = new PdfPCell();
		cell.setColspan(4);

		String foot = "            设备柜员号: "
				+ getJsonData().getString("DVTeller") + "           设备号: "
				+ getJsonData().getString("DeviceNO") + "            机构名: "
				+ getJsonData().getString("BranchName");

		cell.addElement(new Chunk(foot, getFont()));
		table.addCell(cell);

		return true;
	}

}
