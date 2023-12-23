package com.yunfei.tinyworkflow.node;

import com.yunfei.tinyworkflow.engine.WfContext;
import com.yunfei.tinyworkflow.task.IWorkflowTask;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class TaskNode extends WfNode {
    {
        nodeType = NodeType.TASK;
    }
    private IWorkflowTask taskCallback;

    public void work(WfContext wfContext) {
        taskCallback.run(this, wfContext);
    }
}
