package com.yunfei.tinyworkflow.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yunfei.tinyworkflow.entity.TaskDo;

import java.util.List;

public interface TaskDao extends BaseMapper {
    List<TaskDo> query(TaskDo taskDo);
    List<TaskDo> queryAll();
}
