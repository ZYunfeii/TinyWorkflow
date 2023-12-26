package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.node.WfNode;

public interface IWfEngine {
    void init(String configFilePath);
    void syncRun();
    void asyncRun(WfAsyncCallback<Object> callback);

    void stop();

    Object getNodeResult(String nodeId);

}
