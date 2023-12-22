package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.loader.ConfigLoader;
import com.yunfei.tinyworkflow.loader.IConfigLoader;
import com.yunfei.tinyworkflow.node.NodeStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WfEngine implements IWfEngine {
    private IConfigLoader configLoader = new ConfigLoader();
    private Scheduler scheduler;
    private WfContext<?> ctx;
    @Override
    public void init(String configFilePath) {
        try {
            configLoader.loadConfig(configFilePath);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        StatusManager statusManager = StatusManager.builder().transition(configLoader.getWfTrans()).taskMap(configLoader.getTasksMap()).build();
        scheduler = Scheduler.builder().statusManager(statusManager).build();
    }

    @Override
    public void run() {
        scheduler.run(ctx);
    }
}
