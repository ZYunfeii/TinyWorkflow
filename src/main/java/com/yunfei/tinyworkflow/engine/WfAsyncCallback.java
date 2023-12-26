package com.yunfei.tinyworkflow.engine;
@FunctionalInterface
public interface WfAsyncCallback<T> {
    void onComplete(T result);
}
