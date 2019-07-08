package com.wl.tools;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wl.spring.base.action.CreateVoucher;

public class ReaderVoucherXML {
	private static Logger logger=LoggerFactory.getLogger(CreateVoucher.class);
	private Map<String, String> fields;
	
	public ReaderVoucherXML() {
		// TODO Auto-generated constructor stub
	}
	
	public boolean readerVoucher(File file)
	{
		
		if(!file.exists())return false;
		
		fields=new  LinkedHashMap<String, String>();
		
		SAXReader reader=new SAXReader();
		
		Document doc=null;
		try {
			doc = reader.read(file);
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			logger.error("解析凭证xml文件失败:"+e1.getMessage());
		}
		Element root = doc.getRootElement();
		
		for(Element e : (List<Element>)root.elements())
		{
			String name=e.getName();
			String value=e.getTextTrim();
			fields.put(name, value);
		}
		root=null;
		doc=null;
		reader=null;
		
		return true;
	}
	
	public Map<String, String> getFields()
	{
		return fields;
	}

}
