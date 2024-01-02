package com.yunfei.tinyworkflow.util;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class AdjustableCountDownLatch {
    private int count;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public AdjustableCountDownLatch(int count) {
        if (count < 0) throw new IllegalArgumentException("count < 0");
        this.count = count;
    }

    public void await() throws InterruptedException {
        lock.lock();
        try {
            while (count > 0) {
                condition.await();
            }
        } finally {
            lock.unlock();
        }
    }

    public void countDown() {
        lock.lock();
        try {
            if (count > 0) {
                count--;
                if (count == 0) {
                    condition.signalAll();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void increaseCount() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }

    public int getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}

