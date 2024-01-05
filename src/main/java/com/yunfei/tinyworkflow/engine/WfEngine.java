package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.loader.*;
import com.yunfei.tinyworkflow.node.TaskNode;
import com.yunfei.tinyworkflow.node.WfNode;
import com.yunfei.tinyworkflow.threadpool.WfThreadPoolFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

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
        scheduler = Scheduler.builder().statusManager(statusManager).barrier(new CyclicBarrier(2)).
                build();
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
        scheduler.awaitUntilAllCompleted();
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

    @Override
    public void changeTaskNodeCallback(String taskNodeId, Class<?> cls) {
        scheduler.getStatusManager().setWfNodeCallbackById(taskNodeId, cls);
    }
}
