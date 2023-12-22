package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.loader.ConfigLoader;
import com.yunfei.tinyworkflow.loader.IConfigLoader;
import com.yunfei.tinyworkflow.node.NodeStatus;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WfEngine implements IWfEngine {
    private IConfigLoader configLoader = new ConfigLoader();
    private StatusManager statusManager;
    @Override
    public void init(String configFilePath) {
        try {
            configLoader.loadConfig(configFilePath);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
        statusManager = StatusManager.builder().transition(configLoader.getWfTrans()).taskMap(configLoader.getTasksMap()).build();
    }

    @Override
    public void run() {
        statusManager.updateNodeStatus("task1", NodeStatus.RUNNING);
    }
}
