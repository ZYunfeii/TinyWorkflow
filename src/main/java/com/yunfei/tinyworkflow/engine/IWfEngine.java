package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.node.WfNode;

public interface IWfEngine {
    void init(String configFilePath);
    void run();

    Object getNodeResult(String nodeId);

}
