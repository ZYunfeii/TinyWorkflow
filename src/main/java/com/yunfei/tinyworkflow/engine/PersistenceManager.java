package com.yunfei.tinyworkflow.engine;

import com.alibaba.fastjson.JSON;
import com.yunfei.tinyworkflow.dao.TaskDao;
import com.yunfei.tinyworkflow.dao.WorkflowCtxDao;
import com.yunfei.tinyworkflow.entity.TaskDo;
import com.yunfei.tinyworkflow.entity.WorkflowCtxDo;
import com.yunfei.tinyworkflow.loader.WfMyBatisPlusConfig;
import com.yunfei.tinyworkflow.node.NodeStatus;
import com.yunfei.tinyworkflow.util.MyBatisPlusUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.util.List;

@Slf4j
public class PersistenceManager {
    private MyBatisPlusUtil myBatisPlusUtil;

    private static volatile PersistenceManager instance;
    private PersistenceManager(WfMyBatisPlusConfig wfMyBatisPlusConfig) {
        myBatisPlusUtil = MyBatisPlusUtil.builder().daoPackageName(wfMyBatisPlusConfig.getDaoPackageName()).
                driverName(wfMyBatisPlusConfig.getDriverName()).userName(wfMyBatisPlusConfig.getUserName()).
                password(wfMyBatisPlusConfig.getPassword()).jdbcUrl(wfMyBatisPlusConfig.getJdbcUrl()).build();
        try {
            myBatisPlusUtil.init();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    public static void init(WfMyBatisPlusConfig wfMyBatisPlusConfig) {
        if (instance == null) {
            synchronized (PersistenceManager.class) {
                if (instance == null) {
                    instance = new PersistenceManager(wfMyBatisPlusConfig);
                }
            }
        }
    }

    public static PersistenceManager getInstance() {
        if (instance == null) {
            log.info("please invoke init(WfMyBatisPlusConfig wfMyBatisPlusConfig)");
            return null;
        }
        return instance;
    }

    private Integer ctxSizeLast = 0;
    public void setContext(Long workflowId, WfContext wfContext) {
        log.info("Begin to set workflow context: workflowId:{}", workflowId);
        if (wfContext.getResult().size() < ctxSizeLast) {
            log.info("already update the context.");
            return;
        }
        SqlSession session = myBatisPlusUtil.getSession();
        WorkflowCtxDao mapper = session.getMapper(WorkflowCtxDao.class);
        WorkflowCtxDo workflowCtxDo = new WorkflowCtxDo();
        workflowCtxDo.setWorkflowId(workflowId);

        List<WorkflowCtxDo> query = mapper.query(workflowCtxDo);
        workflowCtxDo.setCtx(JSON.toJSONString(wfContext));
        if (!query.isEmpty()) {
            mapper.update(workflowCtxDo);
        } else {
            mapper.insert(workflowCtxDo);
        }
        ctxSizeLast = wfContext.getResult().size();
        session.commit();
        session.close();
    }

    public WfContext getContext(Long workflowId) {
        SqlSession session = myBatisPlusUtil.getSession();
        WorkflowCtxDao mapper = session.getMapper(WorkflowCtxDao.class);
        WorkflowCtxDo workflowCtxDo = new WorkflowCtxDo();
        workflowCtxDo.setWorkflowId(workflowId);
        List<WorkflowCtxDo> query = mapper.query(workflowCtxDo);
        session.close();
        if (query.isEmpty()) {
            log.info("There is no workflow corresponding to the workflowId:{}.", workflowId);
            return null;
        }
        if (query.size() > 2) {
            log.warn("There is more than one workflow corresponding to workflowId:{}.", workflowId);
        }
        return JSON.parseObject(query.get(0).getCtx(), WfContext.class);
    }

    public NodeStatus getNodeStatus(Long workflowId, String taskName) {
        SqlSession session = myBatisPlusUtil.getSession();
        TaskDao mapper = session.getMapper(TaskDao.class);
        TaskDo taskDo = new TaskDo();
        taskDo.setTaskName(taskName);
        taskDo.setWorkflowId(workflowId);
        List<TaskDo> query = mapper.query(taskDo);
        session.close();
        if (query.isEmpty()) {
            log.info("There is no corresponding task.");
            return null;
        }
        if (query.size() >= 2) {
            log.warn("There is more than one corresponding task here.");
        }
        return NodeStatus.valueOf(query.get(0).getStatus());
    }

    public void setNodeStatus(Long workflowId, String taskName, NodeStatus nodeStatus) {
        log.info("Begin to set node status: workflowId:{}, taskName:{}, nodeStatus:{}", workflowId, taskName, nodeStatus.toString());
        SqlSession session = myBatisPlusUtil.getSession();
        TaskDao mapper = session.getMapper(TaskDao.class);
        TaskDo taskDo = new TaskDo();
        taskDo.setTaskName(taskName);
        taskDo.setWorkflowId(workflowId);

        List<TaskDo> query = mapper.query(taskDo);
        taskDo.setStatus(nodeStatus.toString());
        if (!query.isEmpty()) {
            mapper.update(taskDo);
        } else {
            mapper.insert(taskDo);
        }
        session.commit();
        session.close();
    }


}
