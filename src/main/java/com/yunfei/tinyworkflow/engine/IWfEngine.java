package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.node.WfNode;

public interface IWfEngine {
    void initWithWorkflowConfigFile(String configFilePath);
    void init();
    void syncRun();
    void asyncRun(WfAsyncCallback<Object> callback);

    void stop();

    Object getNodeResult(String nodeId);

}
