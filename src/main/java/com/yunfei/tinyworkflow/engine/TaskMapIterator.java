package com.yunfei.tinyworkflow.engine;

import com.yunfei.tinyworkflow.node.WfNode;

import java.util.Iterator;
import java.util.Map;

public class TaskMapIterator implements Iterator<Map.Entry<String, WfNode>> {
    private final Iterator<Map.Entry<String, WfNode>> iterator;
    public TaskMapIterator(Map<String, WfNode> taskMap) {
        this.iterator = taskMap.entrySet().iterator();
    }
    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Map.Entry<String, WfNode> next() {
        return iterator.next();
    }
}
