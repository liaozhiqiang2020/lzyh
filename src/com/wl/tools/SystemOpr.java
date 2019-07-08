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
	 * �ػ�
	 * 
	 * @param timeout
	 */
	public static void shutdown(int timeout) {
		ProcessCmd proc = new ProcessCmd();
		proc.processCmd("shutdown -s -t " + timeout);
		logger.info("���ڹػ�" + proc.getResult());
	}

	public static void shutdown() {
		shutdown(30);
	}

	/**
	 * ȡ������
	 */
	public static void cancel() {
		ProcessCmd proc = new ProcessCmd();
		proc.processCmd("shutdown -a ");
		logger.info("����ȡ���ƻ�����" + proc.getResult());
	}

	/**
	 * ����
	 * 
	 * @param timeout
	 */
	public static void reboot(int timeout) {
		ProcessCmd proc = new ProcessCmd();
		proc.processCmd("shutdown -r -t " + timeout);
		logger.info("��������:" + proc.getResult());
	}

	public static void reboot() {
		reboot(30);
	}

	/**
	 * ��������
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
	 * ����exe����
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
	 * ͨ��nio���俽���ļ�(�ײ�������ȽϿ�)
	 * @param source
	 *            Դ�ļ�·��
	 * @param dest
	 *            Ŀ���ļ�·��
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
			System.out.println("�����ļ�ʧ��.");
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * ͨ��������ļ�
	 * 
	 * @param source
	 *            Դ�ļ�·��
	 * @param dest
	 *            Ŀ���ļ�·��
	 * @return
	 */
	public static boolean copyFileByCmd(String source, String dest) {

		String backup="cmd.exe /c copy "+"\""+source+"\""+"  "+"\""+dest+"\"";
		
		ProcessCmd proc = new ProcessCmd();
		return proc.processCmd("copy "+"\""+source+"\""+"  "+"\""+dest+"\"");
	}

}
