package com.wl.tools;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemOpr {

	private static Logger logger = LoggerFactory.getLogger(SystemOpr.class);

	/**
	 * 关机
	 * 
	 * @param timeout
	 */
	public static void shutdown(int timeout) {
		ProcessCmd proc = new ProcessCmd();
		proc.processCmd("shutdown -s -t " + timeout);
		logger.info("正在关机" + proc.getResult());
	}

	public static void shutdown() {
		shutdown(30);
	}

	/**
	 * 取消命令
	 */
	public static void cancel() {
		ProcessCmd proc = new ProcessCmd();
		proc.processCmd("shutdown -a ");
		logger.info("正在取消计划任务" + proc.getResult());
	}

	/**
	 * 重启
	 * 
	 * @param timeout
	 */
	public static void reboot(int timeout) {
		ProcessCmd proc = new ProcessCmd();
		proc.processCmd("shutdown -r -t " + timeout);
		logger.info("正在重启:" + proc.getResult());
	}

	public static void reboot() {
		reboot(30);
	}

	/**
	 * 结束进程
	 * 
	 * @param processname
	 */
	public static boolean killProcess(String processname) {
		ProcessCmd proc = new ProcessCmd();
		boolean isSuccess = proc.killProcess(processname);
		logger.info(proc.getResult());
		return isSuccess;
	}

	/**
	 * 运行exe程序
	 * 
	 * @param exepathname
	 * @return
	 */
	public static boolean runProcess(String exepathname) {
		ProcessCmd proc = new ProcessCmd();
		boolean isSuccess = proc.processExe(exepathname);

		logger.info(proc.getResult());

		return isSuccess;

	}

	/**
	 * 通过nio传输拷贝文件(底层操作，比较快)
	 * @param source
	 *            源文件路径
	 * @param dest
	 *            目标文件路径
	 * @return
	 */
	public static boolean copyFile(String source, String dest) {
		try {
			FileChannel sc = new FileInputStream(source).getChannel();
			FileChannel tc = new FileOutputStream(dest).getChannel();
			long count = sc.size();
			while (count > 0) {
				long transferred = sc.transferTo(sc.position(), count, tc);
				count -= transferred;
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("拷贝文件失败.");
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 通过命令拷贝文件
	 * 
	 * @param source
	 *            源文件路径
	 * @param dest
	 *            目标文件路径
	 * @return
	 */
	public static boolean copyFileByCmd(String source, String dest) {

		String backup="cmd.exe /c copy "+"\""+source+"\""+"  "+"\""+dest+"\"";
		
		ProcessCmd proc = new ProcessCmd();
		return proc.processCmd("copy "+"\""+source+"\""+"  "+"\""+dest+"\"");
	}

}
