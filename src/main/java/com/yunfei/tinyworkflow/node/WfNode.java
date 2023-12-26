package com.yunfei.tinyworkflow.node;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public abstract class WfNode {
    protected String id;
    protected NodeType nodeType;
    protected NodeStatus nodeStatus = NodeStatus.READY;

    public synchronized NodeStatus getNodeStatus() {
        return nodeStatus;
    }

    public synchronized void setNodeStatus(NodeStatus status) {
        this.nodeStatus = status;
    }
}
