package com.wl.netty.http.server.handler;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;



import com.alibaba.fastjson.JSONObject;
import com.wl.spring.base.BaseService;
import com.wl.start.StartProject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderUtil;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;



import io.netty.util.CharsetUtil;

public class ParseRequestHandler extends ChannelHandlerAdapter {

	private static Logger logger=LoggerFactory.getLogger(ParseRequestHandler.class);
	
	private FullHttpRequest request;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {

		
		 InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress(); 
		 String clientIP = insocket.getAddress().getHostAddress(); 
		 //System.out.println(clientIP);
		MDC.put("IP", clientIP);
		
		if (msg instanceof FullHttpRequest)
			request = (FullHttpRequest) msg;
	
		
		String jsonStr = parseJosnRequest(request);
		
		logger.info("接收到消息内容" + jsonStr);
		
		ctx.flush();//很重要
		
		JSONObject json = JSONObject.parseObject(jsonStr);
		
		
		if(!json.containsKey("head")||!json.getJSONObject("head").containsKey("transcode"))throw new Exception("非法报文.");
		
		String transcode=json.getJSONObject("head").getString("transcode");//交易ID

		if(!StartProject.CONTEXT.containsBean(transcode))throw new Exception("当前交易未配置:"+transcode);
		
		
		BaseService service=(BaseService) StartProject.CONTEXT.getBean(transcode);
		
		JSONObject response=service.beforeAction(json);
		
		responseHttp(ctx, response);

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {

		System.out.println("channelReadComplete");
		try {
			ctx.flush();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// cause.printStackTrace();
		System.out.println("程序处理异常2:" + cause.getLocalizedMessage());
		JSONObject respHead = new JSONObject();
		respHead.put("retcode", "9999");
		respHead.put("retmsg", cause.toString());
		JSONObject resp=new JSONObject();
		resp.put("head", respHead);
		responseHttp(ctx,resp);
		//ctx.close();
	}

	private void responseHttp(ChannelHandlerContext ctx, JSONObject resp) {
		
		//System.out.println("http响应====================================================");
	
		
		
		String result=resp.toString();
		
		logger.info("返回数据:"+result);
		
		FullHttpResponse response=null;
		try {
			response = new DefaultFullHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
					Unpooled.wrappedBuffer(result.getBytes("utf-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 1.设置响应
//		FullHttpResponse resp = new DefaultFullHttpResponse(
//				HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
//				Unpooled.copiedBuffer(JSONObject.toJSONString(c),
//						CharsetUtil.UTF_8));

		/**
		 * 跨域设置
		 * 针对ajax,XDR之类http客户端
		 */
		response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN,"*");
		response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS,"GET,POST,OPTIONS,PUT,DELETE");
		response.headers().set(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE,"3600");
		response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS,"x-requested-with,content-type,token,Access-Control-Allow-Origin,Access-Control-Allow-Methods,Access-Control-Max-Age,authorization");

		response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH,
				response.content().readableBytes());
          
		 if (HttpHeaderUtil.isKeepAlive(request)) {
             response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
         }
		 
		 try{
		 
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
		ctx.close();
		 }catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace();
		}
		 
		 MDC.remove("IP");
	}


	private String parseJosnRequest(FullHttpRequest request) {
		ByteBuf jsonBuf = request.content();
		String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
		return jsonStr;
	}
}