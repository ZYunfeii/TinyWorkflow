package com.yunfei.tinyworkflow.engine;

import lombok.Data;

@Data
public class WfContext<T> {
    T result;
}
