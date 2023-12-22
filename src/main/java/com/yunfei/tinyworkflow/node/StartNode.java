package com.yunfei.tinyworkflow.node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class StartNode extends WfNode{
    {
        nodeType = NodeType.START;
    }
}
