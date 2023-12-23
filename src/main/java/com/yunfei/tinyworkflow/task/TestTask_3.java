package com.yunfei.tinyworkflow.task;

import com.yunfei.tinyworkflow.engine.WfContext;
import com.yunfei.tinyworkflow.node.WfNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestTask_3 implements IWorkflowTask{
    @Override
    public void run(WfNode node, WfContext ctx) {
        log.info("Task3 run.");
        ctx.getResult().put(node.getId(), "task3 result");
    }
}
