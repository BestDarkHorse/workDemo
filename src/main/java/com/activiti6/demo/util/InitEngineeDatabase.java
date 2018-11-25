package com.activiti6.demo.util;

import org.activiti.engine.ProcessEngineConfiguration;

/**
 * @author henryyan
 */
public class InitEngineeDatabase {

    public static void main(String[] args) {
        ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault().buildProcessEngine();
    }

}
