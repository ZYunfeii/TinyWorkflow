package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.entity.TaskDo;
import com.yunfei.tinyworkflow.loader.WfMyBatisPlusConfig;
import com.yunfei.tinyworkflow.util.MyBatisPlusUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PersistenceManager {
    private MyBatisPlusUtil myBatisPlusUtil;

    private static PersistenceManager instance;
    private PersistenceManager(WfMyBatisPlusConfig wfMyBatisPlusConfig){
        myBatisPlusUtil = MyBatisPlusUtil.builder().daoPackageName(wfMyBatisPlusConfig.getDaoPackageName()).
                driverName(wfMyBatisPlusConfig.getDriverName()).userName(wfMyBatisPlusConfig.getUserName()).
                password(wfMyBatisPlusConfig.getPassword()).jdbcUrl(wfMyBatisPlusConfig.getJdbcUrl()).build();
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


}
