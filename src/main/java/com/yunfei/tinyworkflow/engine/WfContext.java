package com.yunfei.tinyworkflow.engine;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WfContext {
    private Map<String, Object> result = new HashMap<>(12);
    public void clearWfContextInfo() {

    }
}
