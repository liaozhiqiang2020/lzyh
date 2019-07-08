package com.wl.tools;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import ch.ubique.inieditor.IniEditor;

public class IniFileOperator {
	
	private static Logger logger=LoggerFactory.getLogger(IniFileOperator.class);
	
	private IniEditor iniOP; 
	
	private File fil;
	
	private String section;
	
	private String configFile;
	
	
	
	public IniFileOperator() {
		// TODO Auto-generated constructor stub
		
		iniOP=new IniEditor();
	
	}
	
	public IniFileOperator(String filefullPath)
	{
		fil=new File(filefullPath);
		iniOP=new IniEditor();
		try {
			iniOP.load(fil);
			logger.info("加载配置文件成功:"+filefullPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.info("加载配置文件失败:"+e.getMessage());
		}
	
		
	}
	
	
	public IniFileOperator(File file)
	{
		fil=file;
		iniOP=new IniEditor();
		try {
			iniOP.load(fil);
			//logger.info("加载配置文件:"+file.getName()+"成功.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.info("加载配置文件失败:"+e.getMessage());
		}
	}
	
	
	public boolean containKey(String key)
	{
		return iniOP.hasOption(this.section, key);
	}
	
	/**
	 * 根据Json文件创建Ini配置文件
	 * @param json
	 */
	public void createIniFileByJSONObject(JSONObject json)
	{
		Iterator<String> keys=json.keySet().iterator();
		 
		 while(keys.hasNext())
		 {
			 String key=keys.next();
			 if(json.get(key) instanceof JSONObject)
			 {
				 iniOP.addSection(key);
				 writeIniSection(key, json.getJSONObject(key));
				
				 continue;
			 }
		 }
	}
	
	/**
	 * 根据json文件修改ini配置文件
	 * @param json
	 */
	public void modifyIniFileByJson(JSONObject json)
	{
		Iterator<String> keys=json.keySet().iterator();
		 
		 while(keys.hasNext())
		 {
			 String key=keys.next();
			 if(json.get(key) instanceof JSONObject)
			 {
				 if(!iniOP.hasSection(key))iniOP.addSection(key);
				 writeIniSectionNoBlank(key, json.getJSONObject(key));
				
				 continue;
			 }
		 }
		 
		 saveIniFile();
		 clear();
	}
	
		 private void writeIniSection(String section,JSONObject json)
			{
				
				//iniEt.addComment(section,section);
				Iterator<String> keys=json.keySet().iterator();
				//iniOP.addBlankLine(section);
				 while(keys.hasNext())
				 {
					 String key=keys.next();
					 
					
						 iniOP.set(section, key, json.getString(key));
						 continue;
					
				 }
				
				
			}
		 private void writeIniSectionNoBlank(String section,JSONObject json)
			{
				
				
				Iterator<String> keys=json.keySet().iterator();
				 while(keys.hasNext())
				 {
					 String key=keys.next();
					 
					
						 iniOP.set(section, key, json.getString(key));
				 }
				
			}
		 
		 
		 private JSONObject readIniSection(String section)
		 {
			 JSONObject tmp=new JSONObject();
			 Iterator<String> it=iniOP.optionNames(section).iterator();
			 while(it.hasNext())
			 {
				 String key=it.next();
				 tmp.put(key, iniOP.get(section, key));
			 }
			 
			 return tmp;
		 }
		 
		 public JSONObject fileToJSON()
		 {
			 JSONObject config=new JSONObject();
			 
			 Iterator<String> sections=iniOP.sectionNames().iterator();
			 while(sections.hasNext())
			 {
				 String section=sections.next();
				 config.put(section, readIniSection(section));
			 }
			 
			 return config;
			 
		 }
		 
		 public boolean saveIniFile()
		 {
			 try {
				iniOP.save(this.fil);
				return true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.info("保存配置文件:"+fil.getAbsolutePath()+"失败.");
				return false;
			}
		 }
		 
		 public void clear()
		 {
			 iniOP=null;
		 }
		 
		 
		 /**
		  * 获取指定option的值
		  * @param section
		  * @param key
		  * @return
		  */
		 public String getValue(String section,String option)
		 {
			String defaultValue="";
			String value=iniOP.get(section, option);
			if(value==null)value=defaultValue;
			
			return value;
		 }
		 
		 public String getValue(String option)
		 {
			 return getValue(this.section, option);
		 }
		 
		 
		 
		 public void setValue(String section,String option,String value)
		 {
			 iniOP.set(section, option, value);
		 }
		 
		 public void setValue(String option,String value)
		 {
			 setValue(this.section,option,value);
		 }
		 

	public IniEditor getIniOP() {
		return iniOP;
	}

	public void setIniOP(IniEditor iniOP) {
		this.iniOP = iniOP;
	}

	public File getFil() {
		return fil;
	}

	public void setFil(File fil) {
		this.fil = fil;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getConfigFile() {
		return configFile;
	}

	public void setConfigFile(String configFile) {
		this.configFile = configFile;
	}
	
	
	

}
