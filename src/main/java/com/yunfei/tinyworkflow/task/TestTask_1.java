package com.yunfei.tinyworkflow.task;

import com.yunfei.tinyworkflow.engine.WfContext;
import com.yunfei.tinyworkflow.node.WfNode;

public class TestTask_1 implements IWorkflowTask{
    @Override
    public void run(WfNode wfNode, WfContext ctx) {
        ctx.getResult().put(wfNode.getId(), "approve");
    }
}
