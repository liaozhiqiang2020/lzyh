package com.wl.netty.http.file.server.handler;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderUtil.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import ch.ubique.inieditor.IniEditor;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
	
	
	//private static final String[] enableDirectory={"XFS","ZZAgent"};
	
	
	
	private  String baseDir;
	private final String url;
	
	private final String section;
	private final IniEditor iniEditor;
	
	private String currentDir="";
	
    public HttpFileServerHandler(String url,String section,IniEditor iniEditor){
        this.url=url;
        this.section=section;
        this.iniEditor=iniEditor;
    }
    
    //��Ϣ���뷽��
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request)
        throws Exception
    {
    	baseDir="";
    	
        //��HTTP������Ϣ�Ľ����������ж�
        if (!request.decoderResult().isSuccess())
        {
            //�������ʧ��ֱ�ӹ���400���󷵻�
            sendError(ctx,HttpResponseStatus.BAD_REQUEST);
            return;
        }
        //�������GET����ͷ���405����
        if (request.method()!=HttpMethod.GET)
        {
            sendError(ctx,HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        String uri=request.uri();
        

        
        
        final String path=sanitizeUri(uri);
        
        //System.out.println("uri="+uri);
        //System.out.println("path="+path);
        //��������·�����Ϸ��ͷ���403����
        if(path==null){
            sendError(ctx,HttpResponseStatus.FORBIDDEN);
            return;
        }
        
        if("\\".equals(path))//��ʼĿ¼
        {
        	sendListing(ctx);
        	return;
        }
        
        //ʹ��URI·������file����������ļ������ڻ��������ļ��ͷ���404
        File file=new File(path);

        
        if(!file.exists())
        {
        	sendError(ctx,HttpResponseStatus.NOT_FOUND);
          return;
        }
        //�����Ŀ¼�ͷ���Ŀ¼�����Ӹ��ͻ���
        if(file.isDirectory()){
            if(uri.endsWith("/")){
                sendListing(ctx,file);
            }else {
                sendRedirect(ctx,uri+"/");
            }
            return;
        }
        //�ж��ļ��Ϸ���
        if(!file.isFile()){
            sendError(ctx,HttpResponseStatus.FORBIDDEN);
            return;
        }
        RandomAccessFile randomAccessFile=null;
        try
        {
            //��ֻ���ķ�ʽ���ļ��������ʧ�ܷ���404����
            randomAccessFile=new RandomAccessFile(file, "r");
        }
        catch (FileNotFoundException e)
        {
            sendError(ctx,HttpResponseStatus.NOT_FOUND);
            return;
        }
        //��ȡ�ļ��ĳ��ȹ���ɹ���HTTPӦ����Ϣ
        long fileLength=randomAccessFile.length();
        HttpResponse response=new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        setContentLength(response,fileLength);
        setContentTypeHeader(response,file);
        //�ж��Ƿ���keepAlive������Ǿ�����Ӧͷ������CONNECTIONΪkeepAlive
        if(isKeepAlive(request)){
            response.headers().set(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
        }
        ctx.write(response);
        ChannelFuture sendFileFuture;
        //ͨ��Netty��ChunkedFile����ֱ�ӽ��ļ�д�뵽���ͻ�������
        sendFileFuture=ctx.write(new ChunkedFile(randomAccessFile,0,fileLength,8192),ctx.newProgressivePromise());
        //ΪsendFileFuture��Ӽ����������������ɴ�ӡ������ɵ���־
        sendFileFuture.addListener(new ChannelProgressiveFutureListener()
        {
            
            @Override
            public void operationComplete(ChannelProgressiveFuture future)
                throws Exception
            {
                System.out.println("Transfer complete.");
            }
            
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total)
                throws Exception
            {
                if(total<0){
                    System.err.println("Transfer progress: "+progress);
                }else {
                    //System.err.println("Transfer progress: "+progress+"/"+total);
                }
            }
        });
        //���ʹ��chunked���룬�����Ҫ����һ����������Ŀ���Ϣ�壬��LastHttpContent.EMPTY_LAST_CONTENT���͵��������У�
        //����ʾ���е���Ϣ���Ѿ�������ɣ�ͬʱ����flush���������ͻ������е���Ϣˢ�µ�SocketChannel�з���
        ChannelFuture lastContentFuture=ctx.
            writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        //����Ƿ�keepAlive�ģ����һ����Ϣ������ɺ󣬷����Ҫ�����Ͽ�����
        if(!isKeepAlive(request)){
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }
 
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
        cause.printStackTrace();
        if(ctx.channel().isActive()){
            sendError(ctx,HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private  final Pattern INSECURE_URI=Pattern.compile(".*[<>&\"].*");
    
    private String sanitizeUri(String uri){
        try
        {
            //ʹ��UTF-8��URL���н���
            uri=URLDecoder.decode(uri,"UTF-8");
        }
        catch (Exception e)
        {
            try
            {
                //����ʧ�ܾ�ʹ��ISO-8859-1���н���
                uri=URLDecoder.decode(uri,"ISO-8859-1");
            }
            catch (Exception e2)
            {
                //��Ȼʧ�ܾͷ��ش���
                throw new Error();
            }
        }
        //����ɹ����uri���кϷ����жϣ����������Ȩ�޵�Ŀ¼
        if(!uri.startsWith(url)){
            return null;
        }
        if(!uri.startsWith("/")){
            return null;
        }
        
        //��Ӳ������ļ�·���ָ����滻Ϊ���ز���ϵͳ���ļ�·���ָ���
        uri=uri.replace('/', File.separatorChar);
        if(uri.contains(File.separator+".")||uri.contains('.'+File.separator)||
            uri.startsWith(".")||uri.endsWith(".")||INSECURE_URI.matcher(uri).matches()){
            return null;
        }
        //ʹ�õ�ǰ���г������ڵĹ���Ŀ¼+URI�������·��
       // return System.getProperty("user.dir")+File.separator+uri;
       
         currentDir=uri.substring(7);
         if(currentDir.indexOf("/")>0)
         {
        	 currentDir=currentDir.substring(0,currentDir.indexOf("/"));
        	
         }
         
         if(currentDir.indexOf("\\")>01)
         {
        	 currentDir=currentDir.substring(0,currentDir.indexOf("\\"));
         }
         
         if(iniEditor.hasOption(section, currentDir))
         {
        	 baseDir=iniEditor.get(section, currentDir);
         }
         
         
        
        return baseDir+uri.substring(6);//ȥ��Ĭ�ϵ�files
        
    }
    private  final Pattern ALLOWED_FILE_NAME=Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
    //����Ŀ¼�����ӵ��ͻ��������
    private  void sendListing(ChannelHandlerContext ctx,File dir){
        //�����ɹ���http��Ӧ��Ϣ
        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        //������Ϣͷ��������html�ļ�����Ҫ����Ϊtext/plain���ͻ��˻ᵱ���ı�����
        response.headers().set(CONTENT_TYPE,"text/html;charset=UTF-8");
        //���췵�ص�htmlҳ������
        StringBuilder buf=new StringBuilder();
        String dirPath=dir.getPath();
        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append(dirPath);
        buf.append("Ŀ¼��");
        buf.append("</title></head><body>\r\n");
        buf.append("<h3>");
        buf.append(dirPath).append("Ŀ¼��");
        buf.append("</h3>\r\n");
        buf.append("<ul>");
        buf.append("<li>��һ����<a href=\"../\">..</a></li>\r\n");
        for(File f:dir.listFiles()){
            if(f.isHidden()||!f.canRead()){
                continue;
            }
            String name=f.getName();
//            if(!ALLOWED_FILE_NAME.matcher(name).matches()){
//                continue;
//            }
            
            
            String fileType="�ļ�";
            if(f.isDirectory())fileType="Ŀ¼";
            
            buf.append("<li>").append(fileType).append("��<a href=\"");
            buf.append(name);
            buf.append("\">");
            buf.append(name);
            buf.append("</a></li>\r\n");
        }
        buf.append("</ul></body></html>\r\n");
        //������Ϣ�������
        ByteBuf buffer=Unpooled.copiedBuffer(buf,CharsetUtil.UTF_8);
        //��������������д����Ӧ���󣬲��ͷŻ�����
        response.content().writeBytes(buffer);
        buffer.release();
        //����Ӧ��Ϣ���͵���������ˢ�µ�SocketChannel��
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    
    //���ͳ�ʼĿ¼���ͻ���
    private  void sendListing(ChannelHandlerContext ctx){
    	
    	String[] enableDirectory=iniEditor.get(section, "fileDir").split(",");
    	
        //�����ɹ���http��Ӧ��Ϣ
        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        //������Ϣͷ��������html�ļ�����Ҫ����Ϊtext/plain���ͻ��˻ᵱ���ı�����
        response.headers().set(CONTENT_TYPE,"text/html;charset=UTF-8");
        //���췵�ص�htmlҳ������
        StringBuilder buf=new StringBuilder();
       
        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append("�ļ�");
        buf.append("Ŀ¼��");
        buf.append("</title></head><body>\r\n");
        buf.append("<h3>");
        buf.append("�ļ�").append("Ŀ¼��");
        buf.append("</h3>\r\n");
        buf.append("<ul>");
        //buf.append("<li>��һ����<a href=\"../\">..</a></li>\r\n");
        for(String name:enableDirectory){
           
            
            String fileType="Ŀ¼";
           
            buf.append("<li>").append(fileType).append("��<a href=\"");
            buf.append(name);
            buf.append("\">");
            buf.append(name);
            buf.append("</a></li>\r\n");
        }
        buf.append("</ul></body></html>\r\n");
        //������Ϣ�������
        ByteBuf buffer=Unpooled.copiedBuffer(buf,CharsetUtil.UTF_8);
        //��������������д����Ӧ���󣬲��ͷŻ�����
        response.content().writeBytes(buffer);
        buffer.release();
        //����Ӧ��Ϣ���͵���������ˢ�µ�SocketChannel��
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    private static void sendRedirect(ChannelHandlerContext ctx,String newUri){
        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        response.headers().set(LOCATION,newUri);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    private static void sendError(ChannelHandlerContext ctx,HttpResponseStatus status){
        FullHttpResponse response=new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,status,
            Unpooled.copiedBuffer("Failure: "+status.toString()+"\r\n",CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE,"text/html;charset=UTF-8");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
    
    private static void setContentTypeHeader(HttpResponse response,File file){
        MimetypesFileTypeMap mimetypesTypeMap=new MimetypesFileTypeMap();
        response.headers().set(CONTENT_TYPE,mimetypesTypeMap.getContentType(file.getPath()));
    }

	public String getSection() {
		return section;
	}

	
    
    

}
