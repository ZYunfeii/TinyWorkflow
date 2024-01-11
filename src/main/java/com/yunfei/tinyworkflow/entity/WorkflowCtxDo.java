package com.yunfei.tinyworkflow.entity;

import lombok.Data;
@Data
public class WorkflowCtxDo {
    private Long workflowId;
    private String gmtCreated;
    private String gmtModified;
    private String ctx;
}
