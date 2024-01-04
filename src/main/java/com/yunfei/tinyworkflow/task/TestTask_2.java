package com.yunfei.tinyworkflow.task;

import com.yunfei.tinyworkflow.engine.WfContext;
import com.yunfei.tinyworkflow.node.WfNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestTask_2 implements IWorkflowTask{
    @Override
    public void run(WfNode wfNode, WfContext ctx) throws InterruptedException {
        log.info("Task2 run.");
        Thread.sleep(2000);
        ctx.getResult().put(wfNode.getId(), 2);
        log.info("Task2 completed.");
    }
}
