package com.wl.ftp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import com.wl.tools.mTimer;





public class FtpClient {
	
	private String ftpip;
	private String ftpport;
	private String usernname;
	private String password;
	private String dir;
	private String locdirFr;
	private String locdirTo;
	private String errinfo;
	private FTPClient ftpClient;
	
	public FtpClient() {
		// TODO Auto-generated constructor stub
	}
	
	
	 public final String getErrinfo() {
		return errinfo;
	}


	public final void setErrinfo(String errinfo) {
		this.errinfo = errinfo;
	}


	public final String getFtpip() {
		return ftpip;
	}


	public final void setFtpip(String ftpip) {
		this.ftpip = ftpip;
	}


	public final String getFtpport() {
		return ftpport;
	}


	public final void setFtpport(String ftpport) {
		this.ftpport = ftpport;
	}


	public final String getUsernname() {
		return usernname;
	}


	public final void setUsernname(String usernname) {
		this.usernname = usernname;
	}


	public final String getPassword() {
		return password;
	}


	public final void setPassword(String password) {
		this.password = password;
	}


	public final String getDir() {
		return dir;
	}


	public final void setDir(String dir) {
		this.dir = dir;
	}


	


	public final FTPClient getFtpClient() {
		return ftpClient;
	}


	public final void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}


	public final String getLocdirFr() {
		return locdirFr;
	}


	public final void setLocdirFr(String locdirFr) {
		this.locdirFr = locdirFr;
	}


	public final String getLocdirTo() {
		return locdirTo;
	}


	public final void setLocdirTo(String locdirTo) {
		this.locdirTo = locdirTo;
	}


	/**
     * 连接到服务器
    */
   public  boolean connectServer() {
       if (ftpClient == null) {
           int reply;
           try {
        	   
                ftpClient=new FTPClient();
                ftpClient.setDefaultPort(Integer.parseInt(this.getFtpport()));
                ftpClient.configure(getFtpConfig());
                ftpClient.connect(this.getFtpip());
                ftpClient.login(usernname, password);
                ftpClient.setRemoteVerificationEnabled(false);
                System.out.print(ftpClient.getReplyString());
                reply = ftpClient.getReplyCode();
               
               if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                    ftpClient=null;
                    this.setErrinfo("登陆Ftp服务器:"+this.ftpip+" 端口:"+this.ftpport+",用户名"+this.usernname+",密码:"+this.password+"  失败.");
                  return false;
               }
               return true;
               
            } catch (Exception e) {
            	 ftpClient=null;
            	 this.setErrinfo("登陆Ftp服务器:"+this.ftpip+" 端口:"+this.ftpport+",用户名"+this.usernname+",密码:"+this.password+"  失败.");
                
               return false;
            }
        }
       return true;
    }
	
	
   /**
    * 设置FTP客服端的配置--一般可以不设置
    * @return
   */
  private  FTPClientConfig getFtpConfig(){
     //  FTPClientConfig ftpConfig=new FTPClientConfig(FTPClientConfig.SYST_UNIX);
	  FTPClientConfig ftpConfig=new FTPClientConfig(FTPClientConfig.SYST_NT);
     ftpConfig.setServerLanguageCode(FTP.DEFAULT_CONTROL_ENCODING);
      return ftpConfig;
   }
	
  /**
   * 设置传输文件的类型[文本文件或者二进制文件]
   * @param fileType--BINARY_FILE_TYPE、ASCII_FILE_TYPE 
  */
 public  void setFileType(int fileType){
     try{
          connectServer();
          ftpClient.setFileType(fileType);
      }catch(Exception e){
          e.printStackTrace();
          ftpClient=null;
      }
  }
 
  
 /*
  * 
  * 下载文件
  */
 public boolean downLoadFile(String filename,String destfilename)
 {
	 
	 if(!this.connectServer())
	 {
		
		 return false;
	 }
	 setFileType(FTP.BINARY_FILE_TYPE);//设置传输二进制文件
	 
	 String sourceFile=this.dir+filename;
	 String destFile=this.locdirTo+destfilename;
	 
	 System.out.println("远程文件:"+sourceFile);
	 System.out.println("文件保存路径:"+destFile);
	 
	 if(!loadFile(sourceFile, destFile))return false;
	 
		
	 File testFile=new File(destFile);
	 int size=(int) (testFile.length());
	 System.out.println("文件大小="+size+"B");
	 if(size<=10)//正常文件
	 {
		 this.setErrinfo("文件大小不正常,"+size+"B"+",文件"+sourceFile+"可能不存在.");
		 testFile.delete();
		 testFile=null;
		 return false;
	 }
	 
	 return true;
 }
 
 
 
 
 /**
  * 下载文件
  * @param remoteFileName --服务器上的文件名
  * @param localFileName--本地文件名
 */
public boolean loadFile(String remoteFileName,String localFileName){
   
	    boolean successFlag=false;
	    	
    //下载文件
     BufferedOutputStream buffOut=null;
    try{
         buffOut=new BufferedOutputStream(new FileOutputStream(localFileName));
      
         ftpClient.retrieveFile(remoteFileName, buffOut);
         
         
         
         successFlag=true;
     }catch(Exception e){
         e.printStackTrace();
     }finally{
        try{
            if(buffOut!=null)
                 buffOut.close();
         }catch(Exception e){
             e.printStackTrace();
         }finally
         {
        	 closeConnect();
         }
     }
     
     if(!successFlag)this.setErrinfo("下载文件失败,请确认"+remoteFileName+"文件存在.");
     
     return successFlag;
     
     
 }
 
/**
 * 上传文件
 * @param localfile
 * @param filename
 * @return
 */
public boolean uploadFile(String localfile,String filename)
{
	 
	 if(!this.connectServer())
	 {
		
		 return false;
	 }
	 setFileType(FTP.BINARY_FILE_TYPE);//设置传输二进制文件
	 try {
		 System.out.println("上传目录:"+this.dir);
		ftpClient.changeWorkingDirectory(this.dir);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		this.setErrinfo(this.dir+"目录不存在");
		closeConnect();
		return false;
	}
	try
	{
	localfile=this.locdirFr+localfile;	
	System.out.println("enterLocalPassiveMode");
	ftpClient.enterLocalPassiveMode();
	ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
	
	//FTPClient.enterLocalPassiveMode();
	//这个方法的意思就是每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据。
	//为什么要这样做呢，因为ftp server可能每次开启不同的端口来传输数据，
	//但是在linux上或者其他服务器上面，由于安全限制，可能某些端口没有开启，所以就出现阻塞。
	//主动 FTP ：   命令连接：客户端 >1024 端口 -> 服务器 21 端口   数据连接：客户端 >1024 端口 <- 服务器 20 端口   
    //被动 FTP ：   命令连接：客户端 >1024 端口 -> 服务器 21 端口   数据连接：客户端 >1024 端口 -> 服务器 >1024 端口 
	 //FTP协议有两种工作方式：PORT方式和PASV方式，中文意思为主动式和被动式。     
	// PORT（主动）方式的连接过程是：客户端向服务器的FTP端口（默认是21）发送连接请求，服务器接受连接，建立一条命令链路。当需要传送数据时， 客户端在命令链路上用PORT命令告诉服务器：“我打开了***X端口，你过来连接我”。于是服务器从20端口向客户端的***X端口发送连接请求，建立 一条数据链路来传送数据。       

	 //PASV（被动）方式的连接过程是：客户端向服务器的FTP端口（默认是21）发送连接请求，服务器接受连接，建立一条命令链路。当需要传送数据时， 服务器在命令链路上用PASV命令告诉客户端：“我打开了***X端口，你过来连接我”。于是客户端向服务器的***X端口发送连接请求，建立一条数据链 路来传送数据。
	System.out.println("本地文件路径:"+localfile);
	
	InputStream inputStream=new FileInputStream(localfile);
	System.out.println("准备上传文件");
	boolean success=ftpClient.storeFile(filename, inputStream);
	
	inputStream.close();
	
	
	System.out.println("上传文件结果："+success);
	return success;
	
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
		this.setErrinfo("请查询本地文件:"+localfile+"是否存在,或者用户："+this.usernname+"是否有上传文件权限");
		System.out.println(e.getMessage());
		System.out.println(this.getErrinfo());
		
		return false;
	}finally
	{
		System.out.println("上传文件结束，准备关闭");
		closeConnect();
	}
}


/**
 * 上传照片文件
 * @param localfile
 * @param filename
 * @return
 */
public boolean uploadImageFile(String localfile,String filename)
{
	 
	 if(!this.connectServer())
	 {
		
		 return false;
	 }
	 setFileType(FTP.BINARY_FILE_TYPE);//设置传输二进制文件
	 try {
		 System.out.println("上传目录:"+this.dir+mTimer.getTime(2));
		
		if(!ftpClient.changeWorkingDirectory(this.dir+mTimer.getTime(2)))
		{
			ftpClient.mkd(this.dir+mTimer.getTime(2));
		}
		
		ftpClient.changeWorkingDirectory(this.dir+mTimer.getTime(2));
		
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		this.setErrinfo(this.dir+"目录不存在");
		closeConnect();
		return false;
	}
	try
	{
	
	System.out.println("enterLocalPassiveMode");
	ftpClient.enterLocalPassiveMode();
	ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
	
	//FTPClient.enterLocalPassiveMode();
	//这个方法的意思就是每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据。
	//为什么要这样做呢，因为ftp server可能每次开启不同的端口来传输数据，
	//但是在linux上或者其他服务器上面，由于安全限制，可能某些端口没有开启，所以就出现阻塞。
	//主动 FTP ：   命令连接：客户端 >1024 端口 -> 服务器 21 端口   数据连接：客户端 >1024 端口 <- 服务器 20 端口   
    //被动 FTP ：   命令连接：客户端 >1024 端口 -> 服务器 21 端口   数据连接：客户端 >1024 端口 -> 服务器 >1024 端口 
	 //FTP协议有两种工作方式：PORT方式和PASV方式，中文意思为主动式和被动式。     
	// PORT（主动）方式的连接过程是：客户端向服务器的FTP端口（默认是21）发送连接请求，服务器接受连接，建立一条命令链路。当需要传送数据时， 客户端在命令链路上用PORT命令告诉服务器：“我打开了***X端口，你过来连接我”。于是服务器从20端口向客户端的***X端口发送连接请求，建立 一条数据链路来传送数据。       

	 //PASV（被动）方式的连接过程是：客户端向服务器的FTP端口（默认是21）发送连接请求，服务器接受连接，建立一条命令链路。当需要传送数据时， 服务器在命令链路上用PASV命令告诉客户端：“我打开了***X端口，你过来连接我”。于是客户端向服务器的***X端口发送连接请求，建立一条数据链 路来传送数据。
	System.out.println("本地文件路径:"+localfile);
	
	InputStream inputStream=new FileInputStream(localfile);
	System.out.println("准备上传文件");
	boolean success=ftpClient.storeFile(filename, inputStream);
	
	inputStream.close();
	
	
	System.out.println("上传文件结果："+success);
	
	closeConnect();
	
	return success;
	
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
		this.setErrinfo("请查询本地文件:"+localfile+"是否存在,或者用户："+this.usernname+"是否有上传文件权限");
		System.out.println(e.getMessage());
		System.out.println(this.getErrinfo());
		closeConnect();
		return false;
	}finally
	{
		System.out.println("上传文件结束，准备关闭");
		closeConnect();
	}
}



/**
 * 关闭连接
*/
public  void closeConnect(){
	System.out.println("关闭连接");
   try{
	   
       if(ftpClient!=null){
            ftpClient.logout();
            ftpClient.disconnect();
            ftpClient=null;
        }
    }catch(Exception e){
        e.printStackTrace();
        ftpClient=null;
       
    }
}

}
