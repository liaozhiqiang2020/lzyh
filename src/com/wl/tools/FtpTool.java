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
	
	private String username;// ��¼�û���
	private String password;// ��¼����
	private String remotepath;// Զ�̵�ַ
	private String remotetmppath;//Զ����ʱ��ַ
	private String localpath;// ���ص�ַ
	private String filename;// �ļ�����
	private String ftpIp;//ftp��ַ
	private int ftpPort;// ftp�˿�
	private FTPClient ftpClient;
	private String errormsg;// ������Ϣ
	private STATUS status=STATUS.OFFLINE;//״̬
	
	private int connectTimeout=3000;//3000����
	
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
				
				ftpClient.enterLocalPassiveMode();//����ģʽ
				ftpClient.setBufferSize(1024);
				ftpClient.setControlEncoding("UTF-8");
				
				ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
				status=STATUS.ONLINE;
				isSuccess = true;
			} catch (Exception e) {
				this.setErrormsg("��¼ftp��������" + this.getFtpIp() + "��ʧ��");
				logger.info("��¼ftp��������" + this.getFtpIp() + "��ʧ��");
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
					"OPTS UTF8", "ON"))) {// ������������UTF-8��֧�֣����������֧�־���UTF-8���룬�����ʹ�ñ��ر��루GBK��.
					LOCAL_CHARSET = "UTF-8";
					}
					ftpClient.setControlEncoding(LOCAL_CHARSET);//���ı��봦��
					ftpClient.enterLocalPassiveMode();// ���ñ���ģʽ
					ftpClient.setBufferSize(1024);
					ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// ���ô����ģʽ
					
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
				this.setErrormsg("��¼ftp��������" + this.getFtpIp() + "��ʧ��");
				logger.info("��¼ftp��������" + this.getFtpIp() + "��ʧ��");
			}
		}
		return isSuccess;
	}

	/**
	 * ��鵱ǰĿ¼�ǲ��Ǵ���
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
			logger.info("�л�����Ŀ¼ʧ��:"+e.getMessage());
			this.setErrormsg("�л�����Ŀ¼ʧ��:"+dirs);
		}
	    //if(!isSuccess)return createDirs(dirs);
	    
	    return isSuccess;
		
	}
	/**
	 * ����������Ŀ¼
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
				ftpClient.cwd(dirsTmp[i]);//����ָ��Ŀ¼
			}
			
			isSuccess=true;
			
		}catch (Exception e) {
			// TODO: handle exception
			this.setErrormsg("����Ŀ¼"+dirs+"ʧ��,"+e.getMessage());
			this.changeToRootDr();
			isSuccess=false;
		}
		
		return isSuccess;
	}
	
	/**
	 * ���������ļ�
	 * @return
	 */
	public boolean downLoadFiles()
	{
	
		File localDir=new File(this.getLocalpath());
		if(!localDir.exists())
		{
			boolean rs=localDir.mkdirs();
			logger.info("����Ŀ¼:"+this.getLocalpath()+"���:"+rs);
		}
		//System.out.println("��¼���:"+loginUnix());
		
		loginUnix();
		
		FTPFile[] files=null;
		try {
		    
		     ftpClient.enterLocalPassiveMode();
			files = ftpClient.listFiles(this.remotepath);
			 //System.out.println(ftpClient.printWorkingDirectory());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			this.setErrormsg("ö���ļ�ʧ��:"+e.getMessage());
			return false;
		}
		
		if(files.length==0)
		{
			this.setErrormsg("Ŀ¼"+this.remotepath+"Ϊ��.");
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
	 * �����ļ�
	 * @param remoteDir Զ���ļ�Ŀ¼
	 * @param filename  Զ���ļ�����
	 * @return
	 */
	public boolean downloadFile(String remoteDir,String filename)
	{
//		logger.error("�����ļ�Ŀ¼��"+remoteDir);
		if(!isExistDeretory(remoteDir))
		{
			this.setErrormsg("Ŀ¼"+remoteDir+"������.");
			return false;
		}
		
		
		File fileTmp=new File(this.localpath);
		if(!fileTmp.exists())
		{
			
			if(!fileTmp.mkdirs())
			{
				this.setErrormsg("��������Ŀ¼"+this.localpath+"ʧ��.");
				return false;
			}
		}
		
		String localFile=this.localpath+"//"+filename;
		

        BufferedOutputStream buffOut=null;
		try {
			buffOut = new BufferedOutputStream(new FileOutputStream(localFile));
		
			
			if(!ftpClient.retrieveFile(new String(filename.getBytes(),"ISO-8859-1"), buffOut))
			{
				this.setErrormsg("�����ļ�ʧ��.");
				changeToRootDr();
				return false;
			}

			buffOut.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			this.setErrormsg("�����ļ�ʧ��:"+e.getMessage());
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
	 * �����ļ�
	 * @param remoteDir Զ���ļ�Ŀ¼
	 * @param filename  Զ���ļ�����
	 * @return
	 */
	public boolean downloadFile1(String remoteDir,String filename)
	{
		
		if(!loginUnix())
		{
			logger.error("��¼ftpʧ��.");
			return false;
		}
		if(!isExistDeretory(remoteDir))
		{
			this.setErrormsg("Ŀ¼"+remoteDir+"������.");
			return false;
		}
		
		
		File fileTmp=new File(this.localpath);
		if(!fileTmp.exists())
		{
			
			if(!fileTmp.mkdirs())
			{
				this.setErrormsg("��������Ŀ¼"+this.localpath+"ʧ��.");
				return false;
			}
		}
		
		String localFile=this.localpath+"//"+filename;
		

        BufferedOutputStream buffOut=null;
		try {
			buffOut = new BufferedOutputStream(new FileOutputStream(localFile));
		
			
			if(!ftpClient.retrieveFile(new String(filename.getBytes(),"ISO-8859-1"), buffOut))
			{
				this.setErrormsg("�����ļ�ʧ��.");
				changeToRootDr();
				return false;
			}

			buffOut.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
			this.setErrormsg("�����ļ�ʧ��:"+e.getMessage());
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
	 * �ϴ��ļ�
	 * @param localpath �����ļ�·��
	 * @param filename �����ļ�����
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
			this.setErrormsg("�ϴ��ļ�������:"+fileTmp.getAbsolutePath());
			return false;
		}
		
		
		if(!isExistDeretory(this.getRemotetmppath()))
		{
			
			if(!createRemoteDirs(this.getRemotetmppath()))
			{
				this.setErrormsg("����Զ��Ŀ¼ʧ��:"+this.getRemotetmppath());
				return false;
			}
			else
			{
				this.setErrormsg("����Զ��Ŀ¼�ɹ�.");
				
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
			this.setErrormsg("�ϴ��ļ��쳣:"+e.getMessage());
		}
		changeToRootDr();
		return true;
	}
	
	/**
	 * �����ϴ��ļ�
	 * @param files
	 * @return
	 */
	public boolean uploadFile(File[] files)
	{
		if(!isExistDeretory(this.getRemotetmppath()))
		{
			
			if(!createRemoteDirs(this.getRemotetmppath()))
			{
				this.setErrormsg("����Զ��Ŀ¼ʧ��:"+this.getRemotetmppath());
				return false;
			}
			else
			{
				this.setErrormsg("����Զ��Ŀ¼�ɹ�.");
				
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
	 * �����ϴ�
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
	 * �ϴ������ļ�
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
			this.setErrormsg("�ϴ��ļ��쳣:"+e.getMessage());
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
	 * �л�����Ŀ¼
	 * @return
	 */
	private boolean changeToRootDr()
	{
		
		try {
			boolean isSuccess= ftpClient.changeWorkingDirectory("/");
			//System.out.println("��ǰĿ¼:"+ftpClient.printWorkingDirectory());
			return isSuccess;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//System.out.println("�л�����Ŀ¼ʧ��");
			this.setErrormsg("�л���ftp��Ŀ¼ʧ��:"+e.getMessage());
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
				this.setErrormsg("�ǳ�ftp����ʧ��");
				this.status=STATUS.OFFLINE;
				ftpClient=null;
				iniOP=null;
				return false;
			}
				
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			this.setErrormsg("�ǳ�ftp�������쳣:"+e.getMessage());
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
			logger.info("�����ļ��ɹ�.");
		}
		
		this.changeToRootDr();
		
		if(!this.uploadFile(this.getLocalpath(), "js1.jar"))
		{
			logger.info(this.getErrormsg());
		}else
		{
			logger.info("�ϴ��ļ��ɹ�.");
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
     * ����FTP�ͷ��˵�����--һ����Բ�����
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
