package com.yunfei.tinyworkflow.task;

import com.yunfei.tinyworkflow.engine.WfContext;
import com.yunfei.tinyworkflow.node.WfNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestTask_4 implements IWorkflowTask{
    @Override
    public void run(WfNode node, WfContext ctx) throws InterruptedException {
        log.info("Task4 run.");

        Thread.sleep(1000);

        ctx.getResult().put(node.getId(), "task4 result");
        log.info("Task4 completed.");
    }
}
