package com.yunfei.tinyworkflow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class TaskDo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long workflowId;
    private String gmtCreated;
    private String gmtModified;
    private String taskName;
    private String status;
}
