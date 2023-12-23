package com.yunfei.tinyworkflow.engine;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Data
@Slf4j
public class WfContext {
    private Map<String, Object> result = new HashMap<>(12);
    public void clearWfContextInfo() {
        result.clear();
    }
    public Object getResultByNodeId(String nodeId) {
        if (!result.containsKey(nodeId)) {
            log.info("The context doesn't have result for {}", nodeId);
            return null;
        }
        return result.get(nodeId);
    }
}
