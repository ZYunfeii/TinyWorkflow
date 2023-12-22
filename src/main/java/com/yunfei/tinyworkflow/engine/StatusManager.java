package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.loader.TransEndpoint;
import com.yunfei.tinyworkflow.node.NodeStatus;
import com.yunfei.tinyworkflow.node.WfNode;
import lombok.Builder;

import java.util.List;
import java.util.Map;
@Builder
public class StatusManager {
    private Map<String, WfNode> taskMap;
    private Map<String, List<TransEndpoint<?>>> transition;

    public void updateNodeStatus(String id, NodeStatus nodeStatus) {
        // taskMap和transition值共享引用，只需要更新一个
        taskMap.get(id).setNodeStatus(nodeStatus);
    }

    public NodeStatus getNodeStatusById(String id) {
        return taskMap.get(id).getNodeStatus();
    }

    public void setAllReady() {
        taskMap.forEach((k, v)->{
            v.setNodeStatus(NodeStatus.READY);
        });
    }
}
