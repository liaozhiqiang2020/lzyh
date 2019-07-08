package com.wl.logback;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogRunnable extends MdcRunnable {

	 private volatile boolean flag = true;

	    private static final Logger log = LoggerFactory.getLogger(LogRunnable.class);

	    private String code;

	    // 阻塞队列
	    private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();

	    public LogRunnable(String code) {
	        this.code = code;
	    }

	    @Override
	    protected void runWithMdc() {
	        String str;
	        System.out.println("code=" + code + ",启动,logFileName=" + MDC.get("logFileName"));
	        while (flag || queue.size() > 0) {
	            try {
	                str = queue.poll(5, TimeUnit.SECONDS);
	                if (str == null || "null".equals(str) || "".equals(str)) {
	                    continue;
	                }
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	                continue;
	            }
	            log.info(str);
	            MDC.put("logFileName",code);
	        }
	        System.out.println("code=" + code + ",结束,logFileName=" + MDC.get("logFileName"));
	    }

	    public void close() {
	        this.flag = false;
	    }

	    public void setQueue(String str) {
	        queue.add(str);
	    }

	    public String getCode() {
	        return code;
	    }

}
