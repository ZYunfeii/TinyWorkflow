package com.yunfei.tinyworkflow.node;

public enum NodeType {
    TASK("task-node"), DECISION("decision-node"), START("start-node"), END("end-node");
    private String typeName;
    NodeType(String typeName) {
        this.typeName = typeName;
    }
    public String getTypeName() {
        return typeName;
    }
    public static NodeType getNodeTypeByTypeName(String typeName) {
        for (NodeType type : NodeType.values()) {
            if (type.getTypeName().equals(typeName)) {
                return type;
            }
        }
        return null;
    }
}
