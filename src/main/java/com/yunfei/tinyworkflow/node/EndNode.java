package com.yunfei.tinyworkflow.node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class EndNode extends WfNode{
    {
        nodeType = NodeType.END;
    }
    private Integer completedOffset;
}
