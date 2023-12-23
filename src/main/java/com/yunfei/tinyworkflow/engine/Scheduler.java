package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.loader.TransEndpoint;
import com.yunfei.tinyworkflow.node.*;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Slf4j
@Builder
public class Scheduler {
    private StatusManager statusManager;
    public void run(WfContext ctx) {
        WfNode startNode = statusManager.findWfStartNode();
        runNode(startNode, ctx);
    }

    public void init() {
        statusManager.setAllReady();
    }

    /**
     * 递归方式执行小规模workflow
     * @param node
     * @param ctx
     */
    public void runNode(WfNode node, WfContext ctx) {
        if (!statusManager.upStreamAllReady(node)) {
            return;
        }
        if (node.getNodeType().equals(NodeType.END)) {
            log.info("work flow run completed!");
            return;
        }
        if (node.getNodeType().equals(NodeType.TASK)) {
            ((TaskNode) node).work(ctx);
        }
        node.setNodeStatus(NodeStatus.COMPLETED);

        List<TransEndpoint<?>> trans = statusManager.getTrans(node);
        for (TransEndpoint<?> t : trans) {
            if (node.getNodeType().equals(NodeType.DECISION)) {
                if (!checkDecisionNode(node, t, ctx)) {
                    continue;
                }
            }
            runNode(t.getTo(), ctx);
        }
    }

    private Boolean checkDecisionNode(WfNode node, TransEndpoint<?> transEndpoint, WfContext ctx) {
        List<WfNode> parentNodes = statusManager.parentNodes(node);
        if (parentNodes.size() != 1) {
            // 判断节点只支持一个父结点
            log.error("the size of parent nodes doesn't equal to 1.");
            throw new IllegalArgumentException("the size of parent nodes doesn't equal to 1.");
        }
        WfNode wfNode = parentNodes.get(0);
        Object parentNodeResult = ctx.getResult().get(wfNode.getId());
        return parentNodeResult.equals(transEndpoint.getCondition());
    }

}
