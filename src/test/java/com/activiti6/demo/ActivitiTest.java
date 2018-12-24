package com.activiti6.demo;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.junit.Test;

public class ActivitiTest {

    /**
     * 1:部署一个Activiti流程
     */
    @Test
    public void createActivitiTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("shengqing.bpmn")
                .addClasspathResource("shengqing.png")
                .deploy();
    }

}
