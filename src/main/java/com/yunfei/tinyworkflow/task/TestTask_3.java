package com.yunfei.tinyworkflow.task;

import com.yunfei.tinyworkflow.engine.WfContext;
import com.yunfei.tinyworkflow.node.WfNode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestTask_3 implements IWorkflowTask{
    @Override
    public void run(WfNode node, WfContext ctx) throws InterruptedException {
        log.info("Task3 run.");

        Thread.sleep(1000);

        ctx.getResult().put(node.getId(), "good");
        log.info("Task3 completed.");
    }
}
