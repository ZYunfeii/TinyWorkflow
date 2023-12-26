package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.loader.*;
import com.yunfei.tinyworkflow.threadpool.WfThreadPool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WfEngine implements IWfEngine {
    private final WorkflowConfigLoader workflowConfigLoader = new WorkflowConfigLoader();
    private final EngineConfigLoader engineConfigLoader = new EngineConfigLoader();
    private final String engineConfigFileName = "config.yaml";
    private Scheduler scheduler;
    private WfContext ctx = new WfContext();
    @Override
    public void init(String workflowConfigFilePath) {
        try {
            workflowConfigLoader.loadConfig(workflowConfigFilePath);
            engineConfigLoader.loadConfig(engineConfigFileName);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        WfThreadPoolConfig wfThreadPoolConfig = engineConfigLoader.getWfThreadPoolConfig();
        WfThreadPool.setWfThreadPoolConfigPara(wfThreadPoolConfig.getCoreSize(), wfThreadPoolConfig.getMaxSize(),
                wfThreadPoolConfig.getKeepAliveTime());
        StatusManager statusManager = new StatusManager(workflowConfigLoader.getTasksMap(), workflowConfigLoader.getWfTrans());
        scheduler = Scheduler.builder().statusManager(statusManager).build();
        scheduler.init();
    }

    @Override
    public void syncRun() {
        scheduler.run(ctx);
        // FIXME: it is not elegant.
        while (!scheduler.allCompleted()) {}
    }

    @Override
    public void asyncRun() {
        scheduler.run(ctx);
    }

    @Override
    public Object getNodeResult(String nodeId) {
        return ctx.getResultByNodeId(nodeId);
    }
}
