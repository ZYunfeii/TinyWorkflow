package com.yunfei.tinyworkflow.loader;

import com.yunfei.tinyworkflow.node.WfNode;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TransEndpoint<T> {
    private WfNode to;
    private T condition;
}