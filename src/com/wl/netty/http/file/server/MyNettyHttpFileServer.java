package com.wl.netty.http.file.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ubique.inieditor.IniEditor;

import com.wl.data.check.SHA512Check;
import com.wl.netty.http.file.server.handler.HttpFileServerHandler;
import com.wl.netty.http.server.MyNettyHttpServer;

public class MyNettyHttpFileServer implements Runnable {

	private static Logger logger = LoggerFactory
			.getLogger(MyNettyHttpServer.class);

	private int port;
	private String host = "localhost";
	private String url="";
	
	private String configname="D:\\runjar\\config\\agentconfig.ini";
	private String section="fileTransfer";
	
	//文件服务器的根目录
    private static final String DEFAULT_URL="/files/";
    
    
    
    
    public MyNettyHttpFileServer(final int port,final String url) {
		// TODO Auto-generated constructor stub
    	
    	this.port=port;
    	this.url=url;
	}
    
    public void run(){
    	
    	final IniEditor editor=new IniEditor();
    	
    	try {
			editor.load(new File(configname));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			logger.error("启动文件服务器失败:加载配置文件"+configname+"失败.");
			return ;
		}
    	host=getLocalIp();
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workergGroup=new NioEventLoopGroup();
        try
        {
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup,workergGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            .childHandler(new ChannelInitializer<SocketChannel>()
            {
 
                @Override
                protected void initChannel(SocketChannel ch)
                    throws Exception
                {
                    //HTTP请求消息解码器
                    ch.pipeline().addLast("http-decoder",
                        new HttpRequestDecoder());
                    //将多个消息转换为单一的FullHttpRequest或者FullHttpResponse
                    ch.pipeline().addLast("http-aggregator",
                        new HttpObjectAggregator(65536));
                    //HTTP响应消息编码器
                    ch.pipeline().addLast("http-encoder",
                        new HttpResponseEncoder());
                    //支持异步发送大的码流，但不占用过多内存
                    ch.pipeline().addLast("http-chunked",
                        new ChunkedWriteHandler());
                    ch.pipeline().addLast(
                        new HttpFileServerHandler(url,section,editor));
                }
                
            });
            ChannelFuture f=b.bind(host,port).sync();
            System.out.println("HTTP文件服务器启动，网址是："+"http://"+host+":"+port+url);
            f.channel().closeFuture().sync();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally{
            bossGroup.shutdownGracefully();
            workergGroup.shutdownGracefully();
        }
    }
    
    
    
    
    public String getConfigname() {
		return configname;
	}

	public void setConfigname(String configname) {
		this.configname = configname;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public void startServer()
    {
		System.out.println("启动文件服务器");
		new Thread(this).run();
    }
    
    public static void main(String[] args) throws Exception
    {
//    	String path="C:\\ZZAgent\\";
//    	System.out.println("path="+path);
//       File file=new File(path); 
//       System.out.println(file.exists());
//       System.out.println(file.isHidden());
    	//System.out.println(new SHA512Check().check("03c37607f19c2275484713cfd4a3389eed1584ad2fb4f5b3ac41b670dcf6a257b1bda02e0d5e779599b28cdf0320210257fc017fa8ea9455d1401225d325db8a",new File("D:\\soft\\flash\\flashplayer_sa.exe")));
    	//System.out.println(new SHA512Check().check("03c37607f19c2275484713cfd4a3389eed1584ad2fb4f5b3ac41b670dcf6a257b1bda02e0d5e779599b28cdf0320210257fc017fa8ea9455d1401225d325db8a",new File("D:\\soft\\flash\\flashplayer_sa1.exe")));
    	
    	//cn_windows_7_ultimate_with_sp1_x86_dvd_u_677486.iso
    	//System.out.println(new SHA512Check().check("03c37607f19c2275484713cfd4a3389eed1584ad2fb4f5b3ac41b670dcf6a257b1bda02e0d5e779599b28cdf0320210257fc017fa8ea9455d1401225d325db8a",new File("G:\\win8\\windows_8.1_ultimate_x64_2019.iso")));
    	
        new MyNettyHttpFileServer(8888,DEFAULT_URL).run();
    }


    private String getLocalIp()
	{
		String localIp="";
		try
		{
		 InetAddress addr = InetAddress.getLocalHost();  
         String ip=addr.getHostAddress().toString(); //获取本机ip  
         String hostName=addr.getHostName().toString(); //获取本机计算机名称  
         System.out.println(ip);
         System.out.println(hostName);
         localIp=ip;
         //return ip;
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("获取本机IP失败:"+e.getMessage());
			//return "127.0.0.1";
			localIp="127.0.0.1";
		}
		
		List<String> list=getAllIP();
		for(int i=0,len=list.size();i<len;i++)
		{
			String tmpIp=list.get(i);
			
			if(tmpIp.indexOf("98.")>-1)
			{
				localIp=tmpIp;
			}
		}
	
		return localIp;
		
	}
	
	private List<String> getAllIP()
	{
		List<String> ips=new ArrayList<String>();
		
		Enumeration<NetworkInterface> nets = null;
		try {
			nets = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ips;
		}
				for (NetworkInterface netint : Collections.list(nets))
				{
				try {
					if (null != netint.getHardwareAddress()) {
					List<InterfaceAddress> list = netint.getInterfaceAddresses();
					for (InterfaceAddress interfaceAddress : list) {
					String localip=interfaceAddress.getAddress().toString();
					localip=localip.replace("/", "");
					//logger.info("本机IP:"+localip);
					ips.add(localip);
					}
					}
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				logger.info(ips.toString());
				return ips;
	}
	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	
}
