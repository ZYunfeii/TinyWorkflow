package com.yunfei.tinyworkflow.task;

import com.yunfei.tinyworkflow.engine.WfContext;

public interface IWorkflowTask {
    void run(WfContext ctx);
}
