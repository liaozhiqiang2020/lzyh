package com.wl.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpTool {

    private static enum STATUS{ONLINE,OFFLINE,ERROR};	
    
    
	private static Logger logger=LoggerFactory.getLogger(FtpTool.class);
	
	private String username;// 登录用户名
	private String password;// 登录密码
	private String remotepath;// 远程地址
	private String remotetmppath;//远程临时地址
	private String localpath;// 本地地址
	private String filename;// 文件名称
	private String ftpIp;//ftp地址
	private int ftpPort;// ftp端口
	private FTPClient ftpClient;
	private String errormsg;// 错误信息
	private STATUS status=STATUS.OFFLINE;//状态
	
	private int connectTimeout=3000;//3000毫秒
	
	private String section;
	
	private IniFileOperator iniOP;
	private String configname;

	public String getConfigname() {
		return configname;
	}

	public void setConfigname(String configname) {
		this.configname = configname;
	}
	
	public void initialParams()
	{
		iniOP=new IniFileOperator(new File(configname));
		iniOP.setSection(this.section);
		this.setFtpIp(iniOP.getValue("ftpip"));
		this.setFtpPort(Integer.valueOf(iniOP.getValue("ftpport")));
		this.setUsername(iniOP.getValue("username"));
		this.setPassword(iniOP.getValue("password"));
		this.setLocalpath(iniOP.getValue("localpath"));
		this.setRemotepath(iniOP.getValue("remotepath"));
		if(iniOP.containKey("agentname"))
		{
		this.setFilename(iniOP.getValue("agentname"));
		}else if(iniOP.containKey("filename"))
		{
			this.setFilename(iniOP.getValue("filename"));
		}
	}
	

	private boolean login() {
		if(iniOP==null)initialParams();
		boolean isSuccess=false;
		if(status==STATUS.ONLINE)return true;
		if (ftpClient == null) {
			int reply;
			try {

				ftpClient = new FTPClient();
				ftpClient.setDefaultPort(this.getFtpPort());
				ftpClient.configure(getFtpConfig());
				ftpClient.setConnectTimeout(connectTimeout);
				ftpClient.connect(this.getFtpIp());
				ftpClient.login(this.getUsername(), this.getPassword());
				
				
				
				System.out.print(ftpClient.getReplyString());
				reply = ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(reply)) {
					ftpClient.disconnect();
					this.setErrormsg("FTP server refused connection.");
					logger.info(this.errormsg);
				}
				
				ftpClient.enterLocalPassiveMode();//被动模式
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				status=STATUS.ONLINE;
				isSuccess = true;
			} catch (Exception e) {
				this.setErrormsg("登录ftp服务器【" + this.getFtpIp() + "】失败");
				logger.info("登录ftp服务器【" + this.getFtpIp() + "】失败");
			}
		}
		return isSuccess;
	}
	
	private boolean loginUnix() {
		String LOCAL_CHARSET = "GBK";
		if(iniOP==null)initialParams();
		boolean isSuccess=false;
		if(status==STATUS.ONLINE)return true;
		if (ftpClient == null) {
			int reply;
			try {

				ftpClient = new FTPClient();
				ftpClient.setDefaultPort(this.getFtpPort());
				ftpClient.configure(getFtpUnixConfig());
				ftpClient.setConnectTimeout(connectTimeout);
				ftpClient.connect(this.getFtpIp());
				if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
					if (ftpClient.login(getUsername(), getPassword())) {
					if (FTPReply.isPositiveCompletion(ftpClient.sendCommand(
					"OPTS UTF8", "ON"))) {// 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
					LOCAL_CHARSET = "UTF-8";
					}
					ftpClient.setControlEncoding(LOCAL_CHARSET);//中文编码处理
					ftpClient.enterLocalPassiveMode();// 设置被动模式
					ftpClient.setBufferSize(1024);
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// 设置传输的模式
					
					}
					else
					{
								logger.error("Connet ftpServer error! Please check user or password");
								return false;
					}
				}else
				{
					
				}
				
				
				System.out.print(ftpClient.getReplyString());
				reply = ftpClient.getReplyCode();
				if (!FTPReply.isPositiveCompletion(reply)) {
					ftpClient.disconnect();
					this.setErrormsg("FTP server refused connection.");
					logger.info(this.errormsg);
				}
			
				status=STATUS.ONLINE;
				isSuccess = true;
			} catch (Exception e) {
				this.setErrormsg("登录ftp服务器【" + this.getFtpIp() + "】失败");
				logger.info("登录ftp服务器【" + this.getFtpIp() + "】失败");
			}
		}
		return isSuccess;
	}

	/**
	 * 检查当前目录是不是存在
	 * @param dirs
	 * @return
	 */
	private boolean isExistDeretory(String dirs)
	{
		if(!login())return false;
		boolean isSuccess=false;
	    try {
			isSuccess=ftpClient.changeWorkingDirectory(dirs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			logger.info("切换工作目录失败:"+e.getMessage());
			this.setErrormsg("切换工作目录失败:"+dirs);
		}
	    //if(!isSuccess)return createDirs(dirs);
	    
	    return isSuccess;
		
	}
	/**
	 * 创建服务器目录
	 * @param dirs
	 * @return
	 */
	private boolean createRemoteDirs(String dirs)
	{
		
		String _dirs=dirs.replaceAll("\\\\", "/");
		boolean isSuccess=false;
		
		if(!login())return false;
		try
		{
			
			String[] dirsTmp=_dirs.split("/");
			
			for(int i=0;i<dirsTmp.length;i++)
			{
				
				if(dirsTmp[i]==null||dirsTmp[i].trim().equals(""))continue;
				if(ftpClient.changeWorkingDirectory(dirsTmp[i]))continue;
				isSuccess=ftpClient.makeDirectory(dirsTmp[i]);
				if(!isSuccess)break;
				ftpClient.cwd(dirsTmp[i]);//进入指定目录
			}
			
			isSuccess=true;
			
		}catch (Exception e) {
			// TODO: handle exception
			this.setErrormsg("创建目录"+dirs+"失败,"+e.getMessage());
			this.changeToRootDr();
			isSuccess=false;
		}
		
		return isSuccess;
	}
	
	/**
	 * 批量下载文件
	 * @return
	 */
	public boolean downLoadFiles()
	{
	
		File localDir=new File(this.getLocalpath());
		if(!localDir.exists())
		{
			boolean rs=localDir.mkdirs();
			logger.info("创建目录:"+this.getLocalpath()+"结果:"+rs);
		}
		//System.out.println("登录结果:"+loginUnix());
		
		loginUnix();
		
		FTPFile[] files=null;
		try {
		    
		     ftpClient.enterLocalPassiveMode();
			files = ftpClient.listFiles(this.remotepath);
			 //System.out.println(ftpClient.printWorkingDirectory());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			this.setErrormsg("枚举文件失败:"+e.getMessage());
			return false;
		}
		
		if(files.length==0)
		{
			this.setErrormsg("目录"+this.remotepath+"为空.");
			return false;
		}
		
		for(FTPFile file:files)
		{
			
			if(file.isDirectory()||file.getName().indexOf(".xml")<0)continue;
			
			String filename=file.getName();
			//System.out.println("name="+filename);
			
			
			if(!downloadFile(this.remotepath, filename))
			{
				return false;
			}
			
			
		}
		
	  return true;
		
	}
	
	/**
	 * 下载文件
	 * @param remoteDir 远程文件目录
	 * @param filename  远程文件名称
	 * @return
	 */
	public boolean downloadFile(String remoteDir,String filename)
	{
//		logger.error("下载文件目录："+remoteDir);
		if(!isExistDeretory(remoteDir))
		{
			this.setErrormsg("目录"+remoteDir+"不存在.");
			return false;
		}
		
		
		File fileTmp=new File(this.localpath);
		if(!fileTmp.exists())
		{
			
			if(!fileTmp.mkdirs())
			{
				this.setErrormsg("创建本地目录"+this.localpath+"失败.");
				return false;
			}
		}
		
		String localFile=this.localpath+"//"+filename;
		

        BufferedOutputStream buffOut=null;
		try {
			buffOut = new BufferedOutputStream(new FileOutputStream(localFile));
		
			
			if(!ftpClient.retrieveFile(new String(filename.getBytes(),"ISO-8859-1"), buffOut))
			{
				this.setErrormsg("下载文件失败.");
				changeToRootDr();
				return false;
			}

			buffOut.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			this.setErrormsg("下载文件失败:"+e.getMessage());
			changeToRootDr();
			return false;
		}catch (Exception e) {
			// TODO: handle exception
			
			
			changeToRootDr();
			return false;
		}
		
		changeToRootDr();
        return true;
	}
	
	
	
	/**
	 * 下载文件
	 * @param remoteDir 远程文件目录
	 * @param filename  远程文件名称
	 * @return
	 */
	public boolean downloadFile1(String remoteDir,String filename)
	{
		
		if(!loginUnix())
		{
			logger.error("登录ftp失败.");
			return false;
		}
		if(!isExistDeretory(remoteDir))
		{
			this.setErrormsg("目录"+remoteDir+"不存在.");
			return false;
		}
		
		
		File fileTmp=new File(this.localpath);
		if(!fileTmp.exists())
		{
			
			if(!fileTmp.mkdirs())
			{
				this.setErrormsg("创建本地目录"+this.localpath+"失败.");
				return false;
			}
		}
		
		String localFile=this.localpath+"//"+filename;
		

        BufferedOutputStream buffOut=null;
		try {
			buffOut = new BufferedOutputStream(new FileOutputStream(localFile));
		
			
			if(!ftpClient.retrieveFile(new String(filename.getBytes(),"ISO-8859-1"), buffOut))
			{
				this.setErrormsg("下载文件失败.");
				changeToRootDr();
				return false;
			}

			buffOut.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			this.setErrormsg("下载文件失败:"+e.getMessage());
			changeToRootDr();
			return false;
		}catch (Exception e) {
			// TODO: handle exception
			
			
			changeToRootDr();
			return false;
		}
		
		changeToRootDr();
        return true;
	}
	
	
	
	public boolean downLoadFile(String version)
	{
		boolean isSuccess= false;
		if(version!=null&&!version.equals(""))
		{
			isSuccess=downloadFile(this.remotepath+version+"/", this.filename);
		}
		else
		{
			isSuccess=downloadFile(this.remotepath, this.filename);
		}
		
		logout();
		return isSuccess;
	}
	
	
	
	
	/**
	 * 上传文件
	 * @param localpath 本地文件路径
	 * @param filename 本地文件名称
	 * @return
	 */
	public boolean uploadFile(String localpath,String filename)
	{
		localpath=localpath.replaceAll("\\\\", "/");
		return upload(localpath+filename);
		
	}
	
	public void loginByUnix()
	{
		loginUnix();
	}
	
	public boolean upload(String filepathname)
	{
		
        File fileTmp=new File(filepathname);
		
		if(!fileTmp.exists())
		{
			this.setErrormsg("上传文件不存在:"+fileTmp.getAbsolutePath());
			return false;
		}
		
		
		if(!isExistDeretory(this.getRemotetmppath()))
		{
			
			if(!createRemoteDirs(this.getRemotetmppath()))
			{
				this.setErrormsg("创建远程目录失败:"+this.getRemotetmppath());
				return false;
			}
			else
			{
				this.setErrormsg("创建远程目录成功.");
				
			}
		}
		
		BufferedInputStream bis=null;
		try {
			bis = new BufferedInputStream(new FileInputStream(fileTmp));
		
			ftpClient.storeFile(filename, bis);
		
		
			if(bis!=null)bis.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			changeToRootDr();
			this.setErrormsg("上传文件异常:"+e.getMessage());
		}
		changeToRootDr();
		return true;
	}
	
	/**
	 * 批量上传文件
	 * @param files
	 * @return
	 */
	public boolean uploadFile(File[] files)
	{
		if(!isExistDeretory(this.getRemotetmppath()))
		{
			
			if(!createRemoteDirs(this.getRemotetmppath()))
			{
				this.setErrormsg("创建远程目录失败:"+this.getRemotetmppath());
				return false;
			}
			else
			{
				this.setErrormsg("创建远程目录成功.");
				
			}
		}
		
		
		for(File tmp:files)
		{
			if(!uploadSingleFile(tmp))return false;
		}
		
		changeToRootDr();
		return true;
		
	}
	
	/**
	 * 批量上传
	 * @param files
	 * @return
	 */
	public boolean batchUpload(File[] files)
	{
		boolean isSuccess=uploadFile(files);
		
		logout();
		return isSuccess;
	}
	
	
	/**
	 * 上传单个文件
	 * @param file
	 * @return
	 */
	private boolean uploadSingleFile(File file)
	{
		BufferedInputStream bis=null;
		try {
			bis = new BufferedInputStream(new FileInputStream(file));
		
			ftpClient.storeFile(file.getName(), bis);
		
		
			if(bis!=null)bis.close();
			return true;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			changeToRootDr();
			this.setErrormsg("上传文件异常:"+e.getMessage());
			return false;
		}
	}
	
	
	
	public boolean uploadFile()
	{
		boolean isSuccess=this.uploadFile(this.localpath, this.filename);
		logout();
		return isSuccess;
	}
	
	
	/**
	 * 切换到根目录
	 * @return
	 */
	private boolean changeToRootDr()
	{
		
		try {
			boolean isSuccess= ftpClient.changeWorkingDirectory("/");
			//System.out.println("当前目录:"+ftpClient.printWorkingDirectory());
			return isSuccess;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println("切换至根目录失败");
			this.setErrormsg("切换至ftp根目录失败:"+e.getMessage());
			return false;
		}
	}
	
	public boolean logoutAuto()
	{
		return logout();
	}
	
	
	private boolean logout()
	{
		try {
			if(ftpClient.logout())
			{
				ftpClient.disconnect();
				this.status=STATUS.OFFLINE;
				ftpClient=null;
				iniOP=null;
				return true;
			}
			else
			{
				this.setErrormsg("登出ftp服务失败");
				this.status=STATUS.OFFLINE;
				ftpClient=null;
				iniOP=null;
				return false;
			}
				
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			this.setErrormsg("登出ftp服务器异常:"+e.getMessage());
			this.status=STATUS.ERROR;
			ftpClient=null;
			iniOP=null;
			return false;
		}
	}
	
	
	@Test
	public void test()
	{
//		this.setUsername("lzyh");
//		this.setPassword("lzyh1234");
//		this.setFtpIp("127.0.0.1");
//		this.setFtpPort(23);
		this.setLocalpath("D:\\work\\ftp\\local\\");
		this.setRemotepath("test2/test2");
		if(!this.downloadFile("test\\test\\", "js1.jar"))
		{
			logger.info(this.getErrormsg());
		}else
		{
			logger.info("下载文件成功.");
		}
		
		this.changeToRootDr();
		
		if(!this.uploadFile(this.getLocalpath(), "js1.jar"))
		{
			logger.info(this.getErrormsg());
		}else
		{
			logger.info("上传文件成功.");
		}
		
		
		
		this.logout();
		
		
	}
	
	public String getFtpIp() {
		return ftpIp;
	}

	public void setFtpIp(String ftpIp) {
		this.ftpIp = ftpIp;
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(int ftpPort) {
		this.ftpPort = ftpPort;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRemotepath() {
		return remotepath;
	}

	public void setRemotepath(String remotepath) {
		this.remotepath = remotepath;
	}

	public String getLocalpath() {
		return localpath;
	}

	public void setLocalpath(String localpath) {
		this.localpath = localpath;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}
	
	/**
     * 设置FTP客服端的配置--一般可以不设置
     * @return
    */
   private  FTPClientConfig getFtpConfig(){
        FTPClientConfig ftpConfig=new FTPClientConfig(FTPClientConfig.SYST_NT);//SYST_UNIX
        ftpConfig.setServerLanguageCode(FTP.DEFAULT_CONTROL_ENCODING);
       
       return ftpConfig;
    }
   
   
   private  FTPClientConfig getFtpUnixConfig(){
       FTPClientConfig ftpConfig=new FTPClientConfig(FTPClientConfig.SYST_UNIX);//SYST_UNIX
       ftpConfig.setServerLanguageCode(FTP.DEFAULT_CONTROL_ENCODING);
      
      return ftpConfig;
   }
   
   

	public IniFileOperator getIniOP() {
		return iniOP;
	}

	public void setIniOP(IniFileOperator iniOP) {
		this.iniOP = iniOP;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getRemotetmppath() {
		return remotetmppath;
	}

	public void setRemotetmppath(String remotetmppath) {
		this.remotetmppath = remotetmppath;
	}
   

}
