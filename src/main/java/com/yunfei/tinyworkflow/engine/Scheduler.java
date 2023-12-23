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
        if (node.getNodeType().equals(NodeType.TASK)) {
            ((TaskNode) node).work(ctx);
        }
        node.setNodeStatus(NodeStatus.COMPLETED);

        List<TransEndpoint<?>> trans = statusManager.getTrans(node);
        for (TransEndpoint<?> t : trans) {
            if (node.getNodeType().equals(NodeType.DECISION)) {
                List<WfNode> parentNodes = statusManager.parentNodes(node);
                if (parentNodes.size() > 1) {
                    // 判断节点只支持一个父结点
                    log.error("the size of parent nodes over 1.");
                    throw new IllegalArgumentException("the size of parent nodes over 1.");
                }
                if (parentNodes.size() == 0) {
                    log.error("the size of parent nodes is 0.");
                    throw new IllegalArgumentException("the size of parent nodes is 0.");
                }
                WfNode wfNode = parentNodes.get(0);
                Object parentNodeResult = ctx.getResult().get(wfNode.getId());
                if (!parentNodeResult.equals(t.getCondition())) {
                    continue;
                }
            }
            runNode(t.getTo(), ctx);
        }
    }

}
