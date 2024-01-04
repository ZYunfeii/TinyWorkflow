package com.yunfei.tinyworkflow.task;

import com.yunfei.tinyworkflow.engine.WfContext;
import com.yunfei.tinyworkflow.node.WfNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestTask_1 implements IWorkflowTask{
    @Override
    public void run(WfNode wfNode, WfContext ctx) throws InterruptedException {
        log.info("Task1 run.");

        Thread.sleep(1000);

        ctx.getResult().put(wfNode.getId(), "approve");
        log.info("Task1 completed.");
    }
}
