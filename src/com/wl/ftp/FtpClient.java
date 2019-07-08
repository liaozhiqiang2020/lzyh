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
     * ���ӵ�������
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
                    this.setErrinfo("��½Ftp������:"+this.ftpip+" �˿�:"+this.ftpport+",�û���"+this.usernname+",����:"+this.password+"  ʧ��.");
                  return false;
               }
               return true;
               
            } catch (Exception e) {
            	 ftpClient=null;
            	 this.setErrinfo("��½Ftp������:"+this.ftpip+" �˿�:"+this.ftpport+",�û���"+this.usernname+",����:"+this.password+"  ʧ��.");
                
               return false;
            }
        }
       return true;
    }
	
	
   /**
    * ����FTP�ͷ��˵�����--һ����Բ�����
    * @return
   */
  private  FTPClientConfig getFtpConfig(){
     //  FTPClientConfig ftpConfig=new FTPClientConfig(FTPClientConfig.SYST_UNIX);
	  FTPClientConfig ftpConfig=new FTPClientConfig(FTPClientConfig.SYST_NT);
     ftpConfig.setServerLanguageCode(FTP.DEFAULT_CONTROL_ENCODING);
      return ftpConfig;
   }
	
  /**
   * ���ô����ļ�������[�ı��ļ����߶������ļ�]
   * @param fileType--BINARY_FILE_TYPE��ASCII_FILE_TYPE 
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
  * �����ļ�
  */
 public boolean downLoadFile(String filename,String destfilename)
 {
	 
	 if(!this.connectServer())
	 {
		
		 return false;
	 }
	 setFileType(FTP.BINARY_FILE_TYPE);//���ô���������ļ�
	 
	 String sourceFile=this.dir+filename;
	 String destFile=this.locdirTo+destfilename;
	 
	 System.out.println("Զ���ļ�:"+sourceFile);
	 System.out.println("�ļ�����·��:"+destFile);
	 
	 if(!loadFile(sourceFile, destFile))return false;
	 
		
	 File testFile=new File(destFile);
	 int size=(int) (testFile.length());
	 System.out.println("�ļ���С="+size+"B");
	 if(size<=10)//�����ļ�
	 {
		 this.setErrinfo("�ļ���С������,"+size+"B"+",�ļ�"+sourceFile+"���ܲ�����.");
		 testFile.delete();
		 testFile=null;
		 return false;
	 }
	 
	 return true;
 }
 
 
 
 
 /**
  * �����ļ�
  * @param remoteFileName --�������ϵ��ļ���
  * @param localFileName--�����ļ���
 */
public boolean loadFile(String remoteFileName,String localFileName){
   
	    boolean successFlag=false;
	    	
    //�����ļ�
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
     
     if(!successFlag)this.setErrinfo("�����ļ�ʧ��,��ȷ��"+remoteFileName+"�ļ�����.");
     
     return successFlag;
     
     
 }
 
/**
 * �ϴ��ļ�
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
	 setFileType(FTP.BINARY_FILE_TYPE);//���ô���������ļ�
	 try {
		 System.out.println("�ϴ�Ŀ¼:"+this.dir);
		ftpClient.changeWorkingDirectory(this.dir);
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		this.setErrinfo(this.dir+"Ŀ¼������");
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
	//�����������˼����ÿ����������֮ǰ��ftp client����ftp server��ͨһ���˿����������ݡ�
	//ΪʲôҪ�������أ���Ϊftp server����ÿ�ο�����ͬ�Ķ˿����������ݣ�
	//������linux�ϻ����������������棬���ڰ�ȫ���ƣ�����ĳЩ�˿�û�п��������Ծͳ���������
	//���� FTP ��   �������ӣ��ͻ��� >1024 �˿� -> ������ 21 �˿�   �������ӣ��ͻ��� >1024 �˿� <- ������ 20 �˿�   
    //���� FTP ��   �������ӣ��ͻ��� >1024 �˿� -> ������ 21 �˿�   �������ӣ��ͻ��� >1024 �˿� -> ������ >1024 �˿� 
	 //FTPЭ�������ֹ�����ʽ��PORT��ʽ��PASV��ʽ��������˼Ϊ����ʽ�ͱ���ʽ��     
	// PORT����������ʽ�����ӹ����ǣ��ͻ������������FTP�˿ڣ�Ĭ����21�������������󣬷������������ӣ�����һ��������·������Ҫ��������ʱ�� �ͻ�����������·����PORT������߷����������Ҵ���***X�˿ڣ�����������ҡ������Ƿ�������20�˿���ͻ��˵�***X�˿ڷ����������󣬽��� һ��������·���������ݡ�       

	 //PASV����������ʽ�����ӹ����ǣ��ͻ������������FTP�˿ڣ�Ĭ����21�������������󣬷������������ӣ�����һ��������·������Ҫ��������ʱ�� ��������������·����PASV������߿ͻ��ˣ����Ҵ���***X�˿ڣ�����������ҡ������ǿͻ������������***X�˿ڷ����������󣬽���һ�������� ·���������ݡ�
	System.out.println("�����ļ�·��:"+localfile);
	
	InputStream inputStream=new FileInputStream(localfile);
	System.out.println("׼���ϴ��ļ�");
	boolean success=ftpClient.storeFile(filename, inputStream);
	
	inputStream.close();
	
	
	System.out.println("�ϴ��ļ������"+success);
	return success;
	
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
		this.setErrinfo("���ѯ�����ļ�:"+localfile+"�Ƿ����,�����û���"+this.usernname+"�Ƿ����ϴ��ļ�Ȩ��");
		System.out.println(e.getMessage());
		System.out.println(this.getErrinfo());
		
		return false;
	}finally
	{
		System.out.println("�ϴ��ļ�������׼���ر�");
		closeConnect();
	}
}


/**
 * �ϴ���Ƭ�ļ�
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
	 setFileType(FTP.BINARY_FILE_TYPE);//���ô���������ļ�
	 try {
		 System.out.println("�ϴ�Ŀ¼:"+this.dir+mTimer.getTime(2));
		
		if(!ftpClient.changeWorkingDirectory(this.dir+mTimer.getTime(2)))
		{
			ftpClient.mkd(this.dir+mTimer.getTime(2));
		}
		
		ftpClient.changeWorkingDirectory(this.dir+mTimer.getTime(2));
		
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
		this.setErrinfo(this.dir+"Ŀ¼������");
		closeConnect();
		return false;
	}
	try
	{
	
	System.out.println("enterLocalPassiveMode");
	ftpClient.enterLocalPassiveMode();
	ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
	
	//FTPClient.enterLocalPassiveMode();
	//�����������˼����ÿ����������֮ǰ��ftp client����ftp server��ͨһ���˿����������ݡ�
	//ΪʲôҪ�������أ���Ϊftp server����ÿ�ο�����ͬ�Ķ˿����������ݣ�
	//������linux�ϻ����������������棬���ڰ�ȫ���ƣ�����ĳЩ�˿�û�п��������Ծͳ���������
	//���� FTP ��   �������ӣ��ͻ��� >1024 �˿� -> ������ 21 �˿�   �������ӣ��ͻ��� >1024 �˿� <- ������ 20 �˿�   
    //���� FTP ��   �������ӣ��ͻ��� >1024 �˿� -> ������ 21 �˿�   �������ӣ��ͻ��� >1024 �˿� -> ������ >1024 �˿� 
	 //FTPЭ�������ֹ�����ʽ��PORT��ʽ��PASV��ʽ��������˼Ϊ����ʽ�ͱ���ʽ��     
	// PORT����������ʽ�����ӹ����ǣ��ͻ������������FTP�˿ڣ�Ĭ����21�������������󣬷������������ӣ�����һ��������·������Ҫ��������ʱ�� �ͻ�����������·����PORT������߷����������Ҵ���***X�˿ڣ�����������ҡ������Ƿ�������20�˿���ͻ��˵�***X�˿ڷ����������󣬽��� һ��������·���������ݡ�       

	 //PASV����������ʽ�����ӹ����ǣ��ͻ������������FTP�˿ڣ�Ĭ����21�������������󣬷������������ӣ�����һ��������·������Ҫ��������ʱ�� ��������������·����PASV������߿ͻ��ˣ����Ҵ���***X�˿ڣ�����������ҡ������ǿͻ������������***X�˿ڷ����������󣬽���һ�������� ·���������ݡ�
	System.out.println("�����ļ�·��:"+localfile);
	
	InputStream inputStream=new FileInputStream(localfile);
	System.out.println("׼���ϴ��ļ�");
	boolean success=ftpClient.storeFile(filename, inputStream);
	
	inputStream.close();
	
	
	System.out.println("�ϴ��ļ������"+success);
	
	closeConnect();
	
	return success;
	
	}catch (Exception e) {
		// TODO: handle exception
		e.printStackTrace();
		this.setErrinfo("���ѯ�����ļ�:"+localfile+"�Ƿ����,�����û���"+this.usernname+"�Ƿ����ϴ��ļ�Ȩ��");
		System.out.println(e.getMessage());
		System.out.println(this.getErrinfo());
		closeConnect();
		return false;
	}finally
	{
		System.out.println("�ϴ��ļ�������׼���ر�");
		closeConnect();
	}
}



/**
 * �ر�����
*/
public  void closeConnect(){
	System.out.println("�ر�����");
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
