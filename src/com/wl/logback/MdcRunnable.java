package com.wl.logback;

import java.util.Map;

import org.slf4j.MDC;

public abstract class MdcRunnable implements Runnable {

    /**
     * 为了线程池中的线程在复用的时候也能获得父线程的MDC中的信息，
     * 子线程第一次初始化的时候没事，因为通过ThreadLocal
     * 已经可以获得MDC中的内容了
     */
    private final Map mdcContext = MDC.getCopyOfContextMap();
    //ExecutorService.execute(new Runnable())的时候，在Runnable构造的时候，用这个方法得到一个Map，保存起来，这时的context是父线程的。
    //然后在执行run方法的时候，放到MDC中去——子线程的context map中去

    @Override
    public final void run() {
        // 线程重用的时候，把父线程中的context map内容带入当前线程的context map中，
        // 因为线程已经初始化过了，不会像初始化时那样通过拷贝父线程ThreadLocal到子线程
        // 的ThreadLocal操作来完成线程间context map的传递。
        // 真正执行到这个run方法的时候，已经到了子线程中了，所以要在初始化的时候用
        // MDC.getCopyOfContextMap()来获得父线程contest map，那时候还在父线程域中
        if (mdcContext != null) {
            MDC.setContextMap(mdcContext);//有了这一步其实不用MDC.clear，因为这一步会将子线程中的context map重置为父线程的context map
//            //源代码：
//            public void setContextMap(Map<String, String> contextMap) {
//                lastOperation.set(WRITE_OPERATION);
//                //将原来的
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
//            MDC.clear();//如果都是用new Thread方法建立的线程可以不用加，因为之后线程会消亡。
            //如果用ThreadPool线程池的话，线程是可以重用的，如果之前的线程的MDC内容没有清除掉的话，
            // 再次重线程池中获取到这个线程，会取出之前的数据(脏数据)，会导致一些不可预期的错误，
            // 所以当前线程结束后一定要清掉。
        }
    }

    protected abstract void runWithMdc();
}