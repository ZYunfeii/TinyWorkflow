package com.yunfei.tinyworkflow.loader;


import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class EngineConfigLoader implements IEngineConfigLoader{
    private WfThreadPoolConfig wfThreadPoolConfig;
    private WfMyBatisPlusConfig wfMyBatisPlusConfig;
    private static final String prefix = "tinyworkflow";
    private static final String threadPoolPrefix = "thread-pool";
    private static final String mybatisPlusPrefix = "mybatis-plus";

    public WfThreadPoolConfig getWfThreadPoolConfig() {
        return wfThreadPoolConfig;
    }
    public WfMyBatisPlusConfig getWfMyBatisPlusConfig() {
        return wfMyBatisPlusConfig;
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

            Map<String, String> mybatisPlusConfig = (Map<String, String>) tinyWorkflow.get(mybatisPlusPrefix);
            if (mybatisPlusConfig == null) {
                log.error("config file doesn't exist 'mybatis-plus'.");
                throw new IllegalArgumentException("config file doesn't exist " + mybatisPlusPrefix);
            }

            String jdbcUrl = mybatisPlusConfig.get("jdbcUrl");
            String userName = mybatisPlusConfig.get("userName");
            String password =  mybatisPlusConfig.get("password");
            String driverName =  mybatisPlusConfig.get("driverName");
            String daoPackageName = mybatisPlusConfig.get("daoPackageName");

            wfMyBatisPlusConfig = WfMyBatisPlusConfig.builder().jdbcUrl(jdbcUrl).userName(userName).password(password).
                    driverName(driverName).daoPackageName(daoPackageName).build();

        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
