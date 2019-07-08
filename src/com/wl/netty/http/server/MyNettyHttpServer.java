package com.wl.netty.http.server;

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

import com.wl.netty.http.file.server.MyNettyHttpFileServer;
import com.wl.netty.http.server.handler.ParseRequestHandler;

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

public class MyNettyHttpServer implements Runnable{

	private static Logger logger=LoggerFactory.getLogger(MyNettyHttpServer.class);
	
	private int httpPort;
	private String uri="localhost";
	
	private MyNettyHttpFileServer httpFileServer;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		uri=getLocalIp();
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try
		{
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				 .handler(new LoggingHandler(LogLevel.INFO))
				 .childHandler(new ChannelInitializer<SocketChannel>() {
					 @Override
					protected void initChannel(SocketChannel ch)
							throws Exception {
						// TODO Auto-generated method stub
//						 ch.pipeline().addLast(new HttpServerCodec());
//						 ch.pipeline().addLast(new HttpObjectAggregator(2048));
//						 ch.pipeline().addLast(new ParseRequestHandler());
//						 ch.pipeline().addLast(new OozieRequestHandler());
						 
						 
						 ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());//http������Ϣ������
					     ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));//�������Ϣת���ɵ�һ��FullHttpRequest����FullHttpRequest
						 ch.pipeline().addLast("http-encoder",new HttpResponseEncoder());//http��Ӧ������
						 ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());//֧���첽���ʹ������(�������ļ�����),����ռ�ù�����ڴ�
						 ch.pipeline().addLast(new ParseRequestHandler());//�Զ����handler�������
						 
						 
					}
				});
         
		logger.info("��������Http����...");
		//ChannelFuture f = b.bind(uri,httpPort).sync();
		ChannelFuture f = b.bind(httpPort).sync();
		System.out.println(ParseRequestHandler.class.getName() + "started and listen on " + f.channel().localAddress());
		f.channel().closeFuture().sync();
	}catch (Exception e) {
		// TODO: handle exception
		logger.info("����http����ʧ��:"+e.getMessage());
	}finally {
		workerGroup.shutdownGracefully();
		bossGroup.shutdownGracefully();
	}
	}

	
	public void startServer()
	{
		System.out.println("����http������1");
		new Thread(this).start();
		System.out.println("����http�ļ�������1");
		new Thread(httpFileServer).start();
	}
	
	public int getHttpPort() {
		return httpPort;
	}

	public void setHttpPort(int httpPort) {
		this.httpPort = httpPort;
	}
	
	
	private String getLocalIp()
	{
		String localIp="";
		try
		{
		 InetAddress addr = InetAddress.getLocalHost();  
         String ip=addr.getHostAddress().toString(); //��ȡ����ip  
         String hostName=addr.getHostName().toString(); //��ȡ�������������  
         System.out.println(ip);
         System.out.println(hostName);
         localIp=ip;
         //return ip;
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("��ȡ����IPʧ��:"+e.getMessage());
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
	
		logger.info("��IP:"+localIp);
		localIp="127.0.0.1";
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
					//logger.info("����IP:"+localip);
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
	
	


	public MyNettyHttpFileServer getHttpFileServer() {
		return httpFileServer;
	}


	public void setHttpFileServer(MyNettyHttpFileServer httpFileServer) {
		this.httpFileServer = httpFileServer;
	}

}
