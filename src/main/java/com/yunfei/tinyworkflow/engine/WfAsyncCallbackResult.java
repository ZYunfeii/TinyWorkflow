package com.yunfei.tinyworkflow.engine;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class WfAsyncCallbackResult {
    private Map<String, Object> result;
}
