package com.yunfei.tinyworkflow.node;

import com.yunfei.tinyworkflow.engine.WfContext;
import com.yunfei.tinyworkflow.task.IWorkflowTask;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;


@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Slf4j
public class TaskNode extends WfNode {
    {
        nodeType = NodeType.TASK;
    }
    private IWorkflowTask taskCallback;
    private Integer maxRetries = 0;
    private Integer curRetries = 0;

    public void work(WfContext wfContext) throws Exception {
        log.info("node:{} start work. Current retries:{}. Max Retries:{}.", this.getId(), curRetries, maxRetries);
        this.setNodeStatus(NodeStatus.RUNNING);
        taskCallback.run(this, wfContext);
        this.setNodeStatus(NodeStatus.COMPLETED);
    }

    @Override
    public synchronized void init() {
        super.init();
        curRetries = 0;
    }
}
