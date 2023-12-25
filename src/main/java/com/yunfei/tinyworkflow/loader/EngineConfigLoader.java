package com.yunfei.tinyworkflow.loader;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class EngineConfigLoader implements IEngineConfigLoader{
    private WfThreadPoolConfig wfThreadPoolConfig;
    private static final String prefix = "tinyworkflow";
    private static final String threadPoolPrefix = "thread-pool";

    public WfThreadPoolConfig getWfThreadPoolConfig() {
        return wfThreadPoolConfig;
    }
    @Override
    public void loadConfig(String fileName) {
        try (InputStream inputStream = EngineConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            if (inputStream == null) {
                log.error("Can not find file: {}", fileName);
                throw new FileNotFoundException("file: " + fileName + " not found.");
            }
            Yaml yaml = new Yaml();
            Map<String, Object> data = yaml.load(inputStream);
            Map<String, Object> tinyWorkflow = (Map<String, Object>) data.get(prefix);
            if (tinyWorkflow == null) {
                log.error("config file doesn't exist 'tinyworkflow'.");
                throw new IllegalArgumentException("config file doesn't exist "+ prefix);
            }

            Map<String, Integer> threadPoolConfig = (Map<String, Integer>) tinyWorkflow.get(threadPoolPrefix);
            if (threadPoolConfig == null) {
                log.error("config file doesn't exist 'thread-pool'.");
                throw new IllegalArgumentException("config file doesn't exist " + threadPoolPrefix);
            }

            Integer coreSize = threadPoolConfig.get("core-size");
            Integer maxSize = threadPoolConfig.get("max-size");
            Integer keepAliveTime = threadPoolConfig.get("keep-alive-time");
            wfThreadPoolConfig = WfThreadPoolConfig.builder().coreSize(coreSize).maxSize(maxSize).
                    keepAliveTime(Long.valueOf(keepAliveTime)).build();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
