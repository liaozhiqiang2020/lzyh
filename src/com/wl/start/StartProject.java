package com.wl.start;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.wl.netty.http.server.MyNettyHttpServer;
import com.wl.spring.base.BaseService;

public class StartProject {

	public static ApplicationContext CONTEXT;

	@Test
	public void test() {
		ApplicationContext ac = new ClassPathXmlApplicationContext(
				"applicationContext.xml");

		MyNettyHttpServer httpServer = (MyNettyHttpServer) ac
				.getBean("locHttpServer");

		// new Thread(httpServer).start();
		// httpServer.run();
		Thread thread = new Thread(httpServer);

		thread.run();

	}

	public static void main(String[] args) {
		// /System.out.println("dsfdfsff");
		CONTEXT = new ClassPathXmlApplicationContext("applicationContext.xml");

		System.out.println("Æô¶¯...");
//		MyNettyHttpServer httpServer = (MyNettyHttpServer) CONTEXT
//				.getBean("locHttpServer");
//
//		// new Thread(httpServer).start();
//		// httpServer.run();
//		Thread thread = new Thread(httpServer);
//
//		thread.run();

	}
}
