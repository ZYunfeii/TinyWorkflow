package com.yunfei.tinyworkflow.threadpool;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WfThreadPoolFactory {
    private static volatile WfThreadPool wfThreadPool;

    private static Integer wfCoreSize;
    private static Integer wfMaxSize;
    private static Long wfKeepAliveTime;

    public static void setWfThreadPoolConfigPara(Integer coreSize, Integer maxSize, Long keepAliveTime) {
        wfCoreSize = coreSize;
        wfMaxSize = maxSize;
        wfKeepAliveTime = keepAliveTime;
    }


    private WfThreadPoolFactory(){}


    public static WfThreadPool getInstance() {
        if (wfThreadPool == null) {
            synchronized (WfThreadPoolFactory.class) {
                if (wfThreadPool == null) {
                    wfThreadPool = new WfThreadPool(
                            wfCoreSize,
                            wfMaxSize,
                            wfKeepAliveTime,
                            TimeUnit.SECONDS,
                            new LinkedBlockingDeque<>(100000),
                            Executors.defaultThreadFactory(),
                            new ThreadPoolExecutor.AbortPolicy()
                    );
                }
            }
        }
        return wfThreadPool;
    }
}
