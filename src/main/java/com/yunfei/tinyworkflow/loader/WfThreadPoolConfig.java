package com.yunfei.tinyworkflow.loader;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WfThreadPoolConfig {
    private Integer coreSize;
    private Integer maxSize;
    private Long keepAliveTime;
}
