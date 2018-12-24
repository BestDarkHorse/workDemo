package com.activiti6.demo.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/**
 * task 任务的监听,主要是为了动态分配执行人
 */
public class MyTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {

        /**
         * 任务的执行人可以动态的赋值
         *  1.流程变量
         *      可以通过提取流程变量的方式给任务赋值执行人
         *  2.
         */
        //动态分配(这里是从上一个节点中的task变量map中获取,只有流程没有结束,所有的变量,都可以获取
//        String value = (String) delegateTask.getVariable("aaa");
//        delegateTask.setAssignee(value);

        delegateTask.setAssignee("我是班主任");
    }
}
