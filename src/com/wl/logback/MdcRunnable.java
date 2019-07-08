package com.wl.logback;

import java.util.Map;

import org.slf4j.MDC;

public abstract class MdcRunnable implements Runnable {

    /**
     * Ϊ���̳߳��е��߳��ڸ��õ�ʱ��Ҳ�ܻ�ø��̵߳�MDC�е���Ϣ��
     * ���̵߳�һ�γ�ʼ����ʱ��û�£���Ϊͨ��ThreadLocal
     * �Ѿ����Ի��MDC�е�������
     */
    private final Map mdcContext = MDC.getCopyOfContextMap();
    //ExecutorService.execute(new Runnable())��ʱ����Runnable�����ʱ������������õ�һ��Map��������������ʱ��context�Ǹ��̵߳ġ�
    //Ȼ����ִ��run������ʱ�򣬷ŵ�MDC��ȥ�������̵߳�context map��ȥ

    @Override
    public final void run() {
        // �߳����õ�ʱ�򣬰Ѹ��߳��е�context map���ݴ��뵱ǰ�̵߳�context map�У�
        // ��Ϊ�߳��Ѿ���ʼ�����ˣ��������ʼ��ʱ����ͨ���������߳�ThreadLocal�����߳�
        // ��ThreadLocal����������̼߳�context map�Ĵ��ݡ�
        // ����ִ�е����run������ʱ���Ѿ��������߳����ˣ�����Ҫ�ڳ�ʼ����ʱ����
        // MDC.getCopyOfContextMap()����ø��߳�contest map����ʱ���ڸ��߳�����
        if (mdcContext != null) {
            MDC.setContextMap(mdcContext);//������һ����ʵ����MDC.clear����Ϊ��һ���Ὣ���߳��е�context map����Ϊ���̵߳�context map
//            //Դ���룺
//            public void setContextMap(Map<String, String> contextMap) {
//                lastOperation.set(WRITE_OPERATION);
//                //��ԭ����
//                Map<String, String> newMap = Collections.synchronizedMap(new HashMap<String, String>());
//                newMap.putAll(contextMap);
//
//                // the newMap replaces the old one for serialisation's sake
//                copyOnThreadLocal.set(newMap);
//            }
        }
        try {
            runWithMdc();
        } finally {
//            MDC.clear();//���������new Thread�����������߳̿��Բ��üӣ���Ϊ֮���̻߳�������
            //�����ThreadPool�̳߳صĻ����߳��ǿ������õģ����֮ǰ���̵߳�MDC����û��������Ļ���
            // �ٴ����̳߳��л�ȡ������̣߳���ȡ��֮ǰ������(������)���ᵼ��һЩ����Ԥ�ڵĴ���
            // ���Ե�ǰ�߳̽�����һ��Ҫ�����
        }
    }

    protected abstract void runWithMdc();
}