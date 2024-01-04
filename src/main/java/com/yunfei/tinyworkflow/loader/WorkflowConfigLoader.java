package com.yunfei.tinyworkflow.loader;

import com.yunfei.tinyworkflow.node.*;
import com.yunfei.tinyworkflow.task.IWorkflowTask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class WorkflowConfigLoader implements IWorkflowConfigLoader {
    private Map<String, WfNode> taskMap;
    private Map<String, List<TransEndpoint<?>>> transition;
    @Override
    public void loadConfig(String fileName) throws IOException, SAXException, ParserConfigurationException {
        InputStream inputStream = WorkflowConfigLoader.class.getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            log.error("read workflow xml error.");
            throw new FileNotFoundException("read workflow xml error.");
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);

        Element catalog = document.getDocumentElement();
        // node get
        NodeList tasks = catalog.getElementsByTagName("nodes");
        taskMap = getTasks(tasks);

        // transitions get
        NodeList trans = catalog.getElementsByTagName("transitions");
        transition = getTransition(trans);
    }

    @Override
    public Map<String, List<TransEndpoint<?>>> getWfTrans() {
        return transition;
    }

    @Override
    public Map<String, WfNode> getTasksMap() {
        return taskMap;
    }

    private Map<String, List<TransEndpoint<?>>> getTransition(NodeList trans) {
        if (trans.getLength() > 1) {
            log.error("trans tag length over 1.");
            throw new IllegalArgumentException("trans tag length over 1.");
        }

        if (trans.getLength() == 0) {
            log.error("trans tag missing.");
            throw new IllegalArgumentException("trans tag missing.");
        }
        Node node = trans.item(0);
        Element element = (Element) node;
        NodeList childNodes = element.getChildNodes();
        Map<String, List<TransEndpoint<?>>> results = new HashMap<>(6);
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element childElement = (Element) childNode;
            String from = childElement.getAttribute("from");
            String to = childElement.getAttribute("to");
            if (StringUtils.isBlank(from) || StringUtils.isBlank(to)) {
                log.error("from/to must be not empty or null!");
                throw new IllegalArgumentException("from/to must be not empty or null!");
            }
            if (!results.containsKey(from)) {
                results.put(from, new ArrayList<>());
            }
            if (!taskMap.containsKey(to) || !taskMap.containsKey(from)) {
                log.error("Can not find node!");
                throw new IllegalArgumentException("Can not find node id!");
            }
            WfNode wfNodeFrom = taskMap.get(from);
            String on = null;
            if (wfNodeFrom.getNodeType().equals(NodeType.DECISION)) {
                on = childElement.getAttribute("on");
                if (StringUtils.isBlank(on)) {
                    log.error("\"on\" must be not empty or null!");
                    throw new IllegalArgumentException("\"on\" must be not empty or null!");
                }
            }

            results.get(from).add(new TransEndpoint<>(
                taskMap.get(to),
                on
            ));
        }
        return results;
    }

    private Map<String, WfNode> getTasks(NodeList tasks) {
        if (tasks.getLength() > 1) {
            log.error("task tag length over 1.");
            throw new IllegalArgumentException("task tag length over 1.");
        }

        if (tasks.getLength() == 0) {
            log.error("task tag missing.");
            throw new IllegalArgumentException("task tag missing.");
        }
        Map<String, WfNode> results = new HashMap<>(6);
        Node node = tasks.item(0);

        if (node.getNodeType() != Node.ELEMENT_NODE) {
            log.error("unknown error");
            throw new IllegalArgumentException("unknown erroor");
        }

        Element element = (Element) node;
        NodeList childNodes = element.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node childNode = childNodes.item(j);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                WfNode wfNode = getNode(childElement);
                if (results.containsKey(wfNode.getId())) {
                    log.error("id must be unique!");
                    throw new IllegalArgumentException("id must be unique!");
                }
                results.put(wfNode.getId(), wfNode);
            }

        }
        return results;
    }
    private WfNode getNode(Element element) {
        String nodeTypeName = element.getTagName();

        NodeType nodeType = NodeType.getNodeTypeByTypeName(nodeTypeName);
        if (nodeType == null) {
            log.error("no such node type.");
            throw new IllegalArgumentException("no such node type.");
        }
        WfNode wfNode = null;
        switch (nodeType) {
            case START:
                wfNode = getStartNode(element);
                break;
            case END:
                wfNode = getEndNode(element);
                break;
            case TASK:
                wfNode = getTaskNode(element);
                break;
            case DECISION:
                wfNode = getDecisionNode(element);
                break;
            default:
                log.error("unknown error.");
                throw new IllegalArgumentException("unknown error.");
        }
        if (wfNode == null) {
            log.error("wfNode is null.");
            throw new NullPointerException("wfNode is null.");
        }
        return wfNode;
    }

    private WfNode getStartNode(Element element) {
        String id = element.getAttribute("id");
        return StartNode.builder().id(id).build();
    }

    private WfNode getEndNode(Element element) {
        String id = element.getAttribute("id");
        return EndNode.builder().id(id).completedOffset(0).build();
    }

    private WfNode getTaskNode(Element element) {
        String id = element.getAttribute("id");
        NodeList assignee = element.getElementsByTagName("assignee");
        if (assignee.getLength() > 1) {
            log.error("one task node has one assignee!");
            throw new IllegalArgumentException("one task node has one assignee!");
        }
        if (assignee.getLength() == 0) {
            log.error("there is no assignee for task node!");
            throw new IllegalArgumentException("there is no assignee for task node!");
        }
        String taskName = assignee.item(0).getTextContent();

        NodeList maxRetryNodeList = element.getElementsByTagName("max-retry");
        Integer maxRetries = maxRetryNodeList.getLength() == 0 ? 0 :
                Integer.parseInt(element.getElementsByTagName("max-retry").item(0).getTextContent());
        IWorkflowTask task;
        try {
            Class<?> clazz = Class.forName(taskName);
            task = (IWorkflowTask) clazz.newInstance();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("task find unknown error.");
        }

        return TaskNode.builder().id(id).maxRetries(maxRetries).taskCallback(task).build();
    }

    private WfNode getDecisionNode(Element element) {
        String id = element.getAttribute("id");
        return DecisionNode.builder().id(id).build();
    }

}
