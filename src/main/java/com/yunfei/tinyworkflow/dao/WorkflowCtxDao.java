package com.yunfei.tinyworkflow.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunfei.tinyworkflow.entity.TaskDo;
import com.yunfei.tinyworkflow.entity.WorkflowCtxDo;

import java.util.List;

public interface WorkflowCtxDao extends BaseMapper {
    List<TaskDo> query(WorkflowCtxDo workflowCtxDo);
}
