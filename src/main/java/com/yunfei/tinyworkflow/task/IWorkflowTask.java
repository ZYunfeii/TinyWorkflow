package com.yunfei.tinyworkflow.task;

import com.yunfei.tinyworkflow.engine.WfContext;
import com.yunfei.tinyworkflow.node.WfNode;

public interface IWorkflowTask {
    void run(WfNode node, WfContext ctx);
}
