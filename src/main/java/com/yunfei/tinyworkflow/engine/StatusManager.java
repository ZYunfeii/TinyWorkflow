package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.loader.TransEndpoint;
import com.yunfei.tinyworkflow.node.NodeStatus;
import com.yunfei.tinyworkflow.node.NodeType;
import com.yunfei.tinyworkflow.node.WfNode;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
@Builder
@Slf4j
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

    public List<TransEndpoint<?>> getTrans(WfNode wfNode) {
        if (!transition.containsKey(wfNode.getId())) {
            log.error("can not find trans for {}", wfNode.getId());
            return null;
        }
        return transition.get(wfNode.getId());

    }

    public WfNode findWfStartNode() {
        WfNode startNode = null;
        for (WfNode n : taskMap.values()) {
            if (n.getNodeType().equals(NodeType.START)) {
                startNode = n;
            }
        }
        if (startNode == null) {
            log.error("can not find start node for flow");
            return null;
        }
        if (startNode.getNodeStatus().equals(NodeStatus.READY)) {
            return startNode;
        }
        // TODO: recovery
        return null;
    }
}
