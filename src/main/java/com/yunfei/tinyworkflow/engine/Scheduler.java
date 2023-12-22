package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.loader.TransEndpoint;
import com.yunfei.tinyworkflow.node.*;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Scheduler {
    private StatusManager statusManager;
    public void run(WfContext<?> ctx) {
        WfNode startNode = statusManager.findWfStartNode();
        runNode(startNode, ctx);

    }

    public void runNode(WfNode node, WfContext<?> ctx) {
        if (node.getNodeType().equals(NodeType.TASK)) {
            ((TaskNode) node).work(ctx);
        }
        node.setNodeStatus(NodeStatus.COMPLETED);
        List<TransEndpoint<?>> trans = statusManager.getTrans(node);
        for (TransEndpoint<?> t : trans) {
            if (t.getTo().getNodeType().equals(NodeType.DECISION)) {
                if (ctx.getResult().equals(t.getCondition())) {
                    runNode(t.getTo(), ctx);
                }
            }
        }
    }

}
