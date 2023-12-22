package com.yunfei.tinyworkflow.node;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder

public class DecisionNode extends WfNode{
    {
        nodeType = NodeType.DECISION;
    }

}
