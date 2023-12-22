package com.yunfei.tinyworkflow.node;

import com.yunfei.tinyworkflow.task.IWorkflowTask;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class TaskNode extends WfNode {
    {
        nodeType = NodeType.TASK;
    }
    private IWorkflowTask taskCallback;
}
