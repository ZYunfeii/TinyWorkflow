package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.loader.TransEndpoint;
import com.yunfei.tinyworkflow.node.NodeStatus;
import com.yunfei.tinyworkflow.node.NodeType;
import com.yunfei.tinyworkflow.node.WfNode;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class StatusManager {
    private Map<String, WfNode> taskMap;
    private Map<String, List<TransEndpoint<?>>> transition;
    private Map<String, List<WfNode>> upStreamTaskMap = new HashMap<>(6);

    public StatusManager(Map<String, WfNode> taskMap, Map<String, List<TransEndpoint<?>>> transition) {
        this.taskMap = taskMap;
        this.transition = transition;
        for (Map.Entry<String, List<TransEndpoint<?>>> entry : transition.entrySet()) {
            String from = entry.getKey();
            List<TransEndpoint<?>> toList = entry.getValue();
            for (TransEndpoint<?> transEndpoint : toList) {
                if (!upStreamTaskMap.containsKey(transEndpoint.getTo().getId())) {
                    upStreamTaskMap.put(transEndpoint.getTo().getId(), new ArrayList<>());
                }
                upStreamTaskMap.get(transEndpoint.getTo().getId()).add(taskMap.get(from));
            }
        }
    }

    public Boolean upStreamAllReady(WfNode node) {
        if (node.getNodeType().equals(NodeType.START)) {
            log.info("START NODE is ready.");
            return true;
        }
        if (!upStreamTaskMap.containsKey(node.getId())) {
            log.error("Can not find node{}'s upstream.", node.getId());
            return false;
        }
        List<WfNode> upStreams = upStreamTaskMap.get(node.getId());
        boolean res = true;
        for (WfNode upStream : upStreams) {
            if (upStream.getNodeType().equals(NodeType.DECISION)) {
                continue;
            }
            res &= upStream.getNodeStatus().equals(NodeStatus.COMPLETED);
        }
        return res;
    }

    public List<WfNode> parentNodes(WfNode wfNode) {
        if (!upStreamTaskMap.containsKey(wfNode.getId())) {
            log.error("Can not find node{}'s parent nodes.", wfNode.getId());
            return Collections.emptyList();
        }
        if (upStreamTaskMap.get(wfNode.getId()) == null) {
            log.info("node{}'s parent nodes is null", wfNode.getId());
            return Collections.emptyList();
        }
        return upStreamTaskMap.get(wfNode.getId());
    }

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
        if (wfNode.getNodeType().equals(NodeType.END)) {
            log.info("Add one to the completion number of upstream nodes of the end node.");
            return Collections.emptyList();
        }
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
