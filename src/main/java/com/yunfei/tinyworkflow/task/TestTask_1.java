package com.yunfei.tinyworkflow.task;

import com.yunfei.tinyworkflow.engine.WfContext;
import com.yunfei.tinyworkflow.node.WfNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestTask_1 implements IWorkflowTask{
    @Override
    public void run(WfNode wfNode, WfContext ctx) {
        log.info("Task1 run.");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ctx.getResult().put(wfNode.getId(), "approve");
        log.info("Task1 completed.");
    }
}
