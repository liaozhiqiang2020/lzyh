package com.wl.http.client;

import java.util.HashMap;
import java.util.Map;

import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.wl.netty.http.server.MyNettyHttpServer;

public class HttpClient {

	private static Logger logger = LoggerFactory
			.getLogger(MyNettyHttpServer.class);

	private String url;// 请求地址
	private String error;// 错误信息
	private String errorcode;// 错误代码
	private JSONObject reqData;// 请求数据
	private JSONObject resData;// 返回数据

	public HttpClient() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 发后管交易
	 * @return
	 */
	public boolean sendMgr(JSONObject req) {
		this.reqData=req;
		
		try
		{
		boolean isSuccess = true;

		Map<String, Object> parms = new HashMap<String, Object>();

		parms.put("data", reqData);
		Header header = Header.create();
		header.set("Content-Type", "application/json");

		Response resp = Http.post3(url, reqData, header, 30 * 1000);

		String content = resp.getContent();
		content = content.replaceAll("\\\\", "");

		content = content.replaceAll("^\\\"", "");
		content = content.replaceAll("\\\"$", "");
		
        //{"respBody":{"count":"","Message":"获取流水号成功"},"respHead":{"TransCode":"SN001","ResCode":"0000","ResMsg":"获取流水号成功"}}

		// {"dev":{"startDate":"20171010","funcAreaCode":"SCHOOL","stopDate":"","manager":"XXX","wbEndDate":"20201010","wbStartDate":"20171010","typeCode":"CJ600D","branchTree":"0000","devTypeNm":"智能柜","deviceIp":"98.10.65.912","pad3":"","deviceNo":"5550001","pad2":"","pad1":"123","address":"兰州","deviceType":"ITM","managerPhone":"130XXXXXXXXXXX","useFlag":true,"trlNo":"234343","branchNo":"0000","branchNm":"兰州银行总行"},"Message":"查询成功."}
        
		JSONObject jsonObject = JSONObject.parseObject(content);
		
		String retCode=jsonObject.getJSONObject("respHead").getString("ResCode");
		
		if(!"0000".equals(retCode))
		{
			error=jsonObject.getJSONObject("respHead").getString("ResMsg");
			return false;
		}
		
		//JSONObject body1 = jsonObject.getJSONObject("respBody");
		//System.out.println(body1.getString("Message"));
		
		resData=jsonObject.getJSONObject("respBody");

		return isSuccess;
		}catch (Exception e) {
			// TODO: handle exception
			error=e.getMessage();
			return false;
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getErrorcode() {
		return errorcode;
	}

	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}

	public JSONObject getReqData() {
		return reqData;
	}

	public void setReqData(JSONObject reqData) {
		this.reqData = reqData;
	}

	public JSONObject getResData() {
		return resData;
	}

	public void setResData(JSONObject resData) {
		this.resData = resData;
	}

}
