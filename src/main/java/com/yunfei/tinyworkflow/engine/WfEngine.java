package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.loader.ConfigLoader;
import com.yunfei.tinyworkflow.loader.IConfigLoader;
import com.yunfei.tinyworkflow.node.NodeStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WfEngine implements IWfEngine {
    private IConfigLoader configLoader = new ConfigLoader();
    private Scheduler scheduler;
    private WfContext ctx = new WfContext();
    @Override
    public void init(String configFilePath) {
        try {
            configLoader.loadConfig(configFilePath);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }

        StatusManager statusManager = new StatusManager(configLoader.getTasksMap(), configLoader.getWfTrans());
        scheduler = Scheduler.builder().statusManager(statusManager).build();
    }

    @Override
    public void run() {
        scheduler.init();
        scheduler.run(ctx);
    }
}
