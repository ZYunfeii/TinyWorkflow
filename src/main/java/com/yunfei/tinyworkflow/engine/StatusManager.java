package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.loader.TransEndpoint;
import com.yunfei.tinyworkflow.node.EndNode;
import com.yunfei.tinyworkflow.node.NodeStatus;
import com.yunfei.tinyworkflow.node.NodeType;
import com.yunfei.tinyworkflow.node.WfNode;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Data
public class StatusManager {
    private Map<String, WfNode> taskMap;
    private Map<String, List<TransEndpoint<?>>> transition;
    private Map<String, List<WfNode>> upStreamTaskMap = new HashMap<>(6);

    private Map<String, Integer> upStreamNodeCompletedCount = new HashMap<>(12);

    private WfNode endNode;
    private WfNode startNode;

    public void init() {
        upStreamNodeCompletedCount.clear();
        initNode();
    }

    public void initEndNode() {
        ((EndNode) endNode).setCompletedOffset(0);
    }

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
        for (WfNode n : taskMap.values()) {
            if (n.getNodeType().equals(NodeType.END)) {
                endNode = n;
            } else if (n.getNodeType().equals(NodeType.START)) {
                startNode = n;
            }
        }
        if (endNode == null || startNode == null) {
            log.error("start node/end node is null!");
            throw new RuntimeException("start node/end node is null!");
        }
    }

    private Integer upStreamNodeCompletedCountAdd(WfNode node) {
        if (node.getNodeType().equals(NodeType.START)) {
            return 0;
        }
        synchronized (this) {
            if (!upStreamNodeCompletedCount.containsKey(node.getId())) {
                upStreamNodeCompletedCount.put(node.getId(), 1);
                log.info("{}'s upstream node completed count update to {}", node.getId(), upStreamNodeCompletedCount.get(node.getId()));
                return 1;
            }
            upStreamNodeCompletedCount.put(node.getId(), upStreamNodeCompletedCount.get(node.getId()) + 1);
            log.info("{}'s upstream node completed count update to {}", node.getId(), upStreamNodeCompletedCount.get(node.getId()));
            return upStreamNodeCompletedCount.get(node.getId());
        }
    }
    public Boolean upStreamCompletedCountAddAndCheckReady(WfNode node) {
        if (node.getNodeType().equals(NodeType.START)) {
            log.info("START NODE is ready.");
            return true;
        }
        Integer shouldBeCompletedCount = upStreamShouldBeCompletedCount(node);
        if (upStreamNodeCompletedCountAdd(node).equals(shouldBeCompletedCount)) {
            log.info("node:{}'s upstream all completed. Count: {}.", node.getId(), shouldBeCompletedCount);
            return true;
        } else {
            return false;
        }
    }

    public void setAllChildNodesUnreachable(WfNode node) {
        if (node.getNodeType().equals(NodeType.END)) {
            EndNode endNode = (EndNode) (node);
            endNode.setCompletedOffset(endNode.getCompletedOffset() - 1);
            return;
        }
        Queue<WfNode> queue = new LinkedList<>();
        queue.add(node);
        while (!queue.isEmpty()) {
            int sz = queue.size();
            for (int i = 0; i < sz; ++i) {
                WfNode n = queue.poll();
                if (n == null) {
                    log.error("when set child nodes unreachable: NPE!");
                    throw new RuntimeException("NPE");
                }
                n.setNodeStatus(NodeStatus.UNREACHABLE);
                List<TransEndpoint<?>> endpoints = transition.get(n.getId());
                for (TransEndpoint<?> endpoint : endpoints) {
                    if (endpoint.getTo().getNodeType().equals(NodeType.END)) {
                        continue;
                    }
                    queue.add(endpoint.getTo());
                }
            }
        }
    }

    private Integer upStreamShouldBeCompletedCount(WfNode node) {
        if (node.getNodeType().equals(NodeType.START)) {
            return 0;
        }
        List<WfNode> wfNodes = upStreamTaskMap.get(node.getId());
        Integer count = 0;
        for (WfNode wfNode : wfNodes) {
            if (!wfNode.getNodeStatus().equals(NodeStatus.UNREACHABLE)) {
                count++;
            }
        }
        if (node.getNodeType().equals(NodeType.END)) {
            EndNode endNode = (EndNode) node;
            count += endNode.getCompletedOffset();
        }
        return count;

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

    private void initNode() {
        taskMap.forEach((k, v)->{
            v.init();
        });
    }

    public List<TransEndpoint<?>> getTrans(WfNode wfNode) {
        if (wfNode.getNodeType().equals(NodeType.END)) {
            return Collections.emptyList();
        }
        if (!transition.containsKey(wfNode.getId())) {
            log.error("can not find trans for {}", wfNode.getId());
            return null;
        }
        return transition.get(wfNode.getId());

    }


    public Boolean allCompleted() {
        if (upStreamNodeCompletedCount.get(endNode.getId()) == null) {
            return false;
        }
        return upStreamNodeCompletedCount.get(endNode.getId()).equals(
                upStreamShouldBeCompletedCount(endNode)
        );
    }


}
