package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.loader.*;
import com.yunfei.tinyworkflow.threadpool.WfThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WfEngine implements IWfEngine {
    private final WorkflowConfigLoader workflowConfigLoader = new WorkflowConfigLoader();
    private final EngineConfigLoader engineConfigLoader = new EngineConfigLoader();
    private final String engineConfigFileName = "config.yaml";
    private Scheduler scheduler;
    private WfContext ctx = new WfContext();
    @Override
    public void initWithWorkflowConfigFile(String workflowConfigFilePath) {
        try {
            workflowConfigLoader.loadConfig(workflowConfigFilePath);
            engineConfigLoader.loadConfig(engineConfigFileName);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        WfThreadPoolConfig wfThreadPoolConfig = engineConfigLoader.getWfThreadPoolConfig();
        WfThreadPoolFactory.setWfThreadPoolConfigPara(wfThreadPoolConfig.getCoreSize(), wfThreadPoolConfig.getMaxSize(),
                wfThreadPoolConfig.getKeepAliveTime());
        StatusManager statusManager = new StatusManager(workflowConfigLoader.getTasksMap(), workflowConfigLoader.getWfTrans());
        scheduler = Scheduler.builder().statusManager(statusManager).build();
        init();
    }

    @Override
    public void init() {
        scheduler.init();
        ctx.clearWfContextInfo();
    }

    @Override
    public void syncRun() {
        scheduler.run(ctx);
        // FIXME: it is not elegant.
        while (!scheduler.allCompleted()) {}
    }

    @Override
    public void asyncRun(WfAsyncCallback<Object> callback) {
        scheduler.setAsyncCallback(callback);
        scheduler.run(ctx);
    }

    @Override
    public void stop() {
        scheduler.stop();
    }

    @Override
    public Object getNodeResult(String nodeId) {
        return ctx.getResultByNodeId(nodeId);
    }
}
