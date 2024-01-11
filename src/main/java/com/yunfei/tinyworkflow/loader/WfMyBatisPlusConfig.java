package com.yunfei.tinyworkflow.loader;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WfMyBatisPlusConfig {
    private String jdbcUrl;
    private String userName;
    private String password;
    private String driverName;
    private String daoPackageName;
}
