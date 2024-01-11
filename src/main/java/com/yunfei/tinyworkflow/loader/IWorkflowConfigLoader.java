package com.yunfei.tinyworkflow.loader;


import com.yunfei.tinyworkflow.node.WfNode;

import java.util.List;
import java.util.Map;

public interface IWorkflowConfigLoader {
    Long loadConfig(String fileName) throws Exception;
    Map<String, List<TransEndpoint<?>>> getWfTrans();
    Map<String, WfNode> getTasksMap();
}
