package com.yunfei.tinyworkflow.threadpool;

import com.yunfei.tinyworkflow.loader.EngineConfigLoader;
import com.yunfei.tinyworkflow.loader.WfThreadPoolConfig;
import lombok.Setter;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WfThreadPool {
    private static volatile ThreadPoolExecutor wfThreadPool;

    private static Integer wfCoreSize;
    private static Integer wfMaxSize;
    private static Long wfKeepAliveTime;

    public static void setWfThreadPoolConfigPara(Integer coreSize, Integer maxSize, Long keepAliveTime) {
        wfCoreSize = coreSize;
        wfMaxSize = maxSize;
        wfKeepAliveTime = keepAliveTime;
    }


    private WfThreadPool(){}


    public static ThreadPoolExecutor getInstance() {
        if (wfThreadPool == null) {
            synchronized (WfThreadPool.class) {
                if (wfThreadPool == null) {
                    wfThreadPool = new ThreadPoolExecutor(
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
