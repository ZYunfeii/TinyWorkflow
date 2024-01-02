package com.yunfei.tinyworkflow.threadpool;

import com.yunfei.tinyworkflow.util.AdjustableCountDownLatch;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class WfThreadPool extends ThreadPoolExecutor {
    private AdjustableCountDownLatch adjustableCountDownLatch = new AdjustableCountDownLatch(0);

    public WfThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public WfThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public WfThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public WfThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    @Override
    public Future<?> submit(Runnable task) {
        adjustableCountDownLatch.increaseCount();
        return super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        adjustableCountDownLatch.increaseCount();
        return super.submit(task, result);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        adjustableCountDownLatch.increaseCount();
        return super.submit(task);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        adjustableCountDownLatch.countDown();
    }

    public void awaitAllTaskInThreadPoolCompleted() {
        try {
            adjustableCountDownLatch.await();
        } catch (InterruptedException e) {
            log.error("A runtime exception occurs.");
            throw new RuntimeException(e);
        }
    }
}
