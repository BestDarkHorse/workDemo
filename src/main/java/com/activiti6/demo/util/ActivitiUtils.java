package com.activiti6.demo.util;

import com.google.common.collect.Maps;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @author dongkui.sun
 * @date
 * @apiNote 针对流程管理的工具类
 */
//@Component 就是讲类 注入到spring容器中,这样就可以在该类中注入一些资源
@Component("activitiUtils")
public class ActivitiUtils {

    @Resource(name = "processEngine")
    private ProcessEngine processEngine;

    /**
     * 部署流程
     * 通过zip文件
     */
    public void deployProcess(File file, String processName) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
        this.processEngine.getRepositoryService()
                .createDeployment()
                .name(processName)
                .addZipInputStream(zipInputStream)
                .deploy();
    }

    /**
     * 部署流程
     * 通过字节流来进行部署流程
     */
    public void deployProcessByInputStream(InputStream io, String processName) {
        ZipInputStream zipInputStream = new ZipInputStream(io);
        this.processEngine.getRepositoryService()
                .createDeployment()
                .name(processName)
                .addZipInputStream(zipInputStream)
                .deploy();
    }

    /**
     * 查询所有的流程定义
     */
    public List<Deployment> getAllDeployment() {
        return this.processEngine.getRepositoryService()
                .createDeploymentQuery()
                .orderByDeploymenTime()
                .desc()
                .list();
    }

    /**
     * 查询所有的部署定义信息
     */
    public List<ProcessDefinition> getAllProcessInstance() {
        return this.processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .orderByProcessDefinitionVersion()
                .desc()
                .list();
    }

    /**
     * 根据部署id.来删除部署
     */
    public void deleteDeploymentByPID(String deploymentId) {
        this.processEngine.getRepositoryService()
                .deleteDeployment(deploymentId, true);
    }

    /**
     * 查询某个部署流程的流程图
     */
    public InputStream lookProcessPicture(String pid) {
        return this.processEngine.getRepositoryService().getProcessDiagram(pid);
    }

    /**
     * 开启请假的流程实例
     */
    public void startProcessInstance(Long billId, String userId) {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("userID", userId);
        //billId 可以用来关联业务表  billId = 业务表 + 业务表的id
        this.processEngine.getRuntimeService()
                .startProcessInstanceByKey("shenqing1", "" + billId, variables);

    }

    /**
     * 查看当前登录人的所有任务
     */
    public List<Task> queryCurrentUserTaskByAssigner(String userId) {
        return this.processEngine.getTaskService()
                .createTaskQuery()
                .taskAssignee(userId)
                .orderByTaskCreateTime()
                .desc()
                .list();
    }
    /**
     * 根据taskId,获取到当前的执行节点实例对象
     */

    /**
     * 根据taskId，判断对应的流程实例是否结束
     * 如果结束了，那么得到的流程实例就是返回一个null
     * 否则就是返回对应的流程实例对象
     * 当然也可以选择返回boolean类型的
     *
     * @param taskId 任务ID
     * @return
     */
    public ProcessInstance isFinishProcessInstancs(String taskId) {
        Task task = getTaskByTaskId(taskId);
        finishCurrentTaskByTaskId(taskId);
        //如果实例完成, processInstance 为 null, 如果没有完成,会返回processInstance
        ProcessInstance processInstance = getProcessInstanceByTask(task);
        return processInstance;
    }



    /**
     * 根据Task中的流程实例的ID，来获取对应的流程实例
     * @param task 流程中的任务
     * @return
     */
    public ProcessInstance getProcessInstanceByTask(Task task) {
        return this.processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .singleResult();
    }

    /**
     * 根据taskId，完成任务
     * @param taskId
     */
    public void finishCurrentTaskByTaskId(String taskId) {
        this.processEngine.getTaskService().complete(taskId);
    }

    /**
     * 根据taskId获取到task
     */
    public Task getTaskByTaskId(String taskId) {
        return this.processEngine.getTaskService()
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
    }

    /**
     * 根据Task来获取对应的流程定义信息
     */
    public ProcessDefinition getProcessDefinitionEntityByTask(Task task) {
        ProcessDefinitionEntity processDefinitionEntity =
                (ProcessDefinitionEntity)this.processEngine.getRepositoryService()
                        .getProcessDefinition(task.getProcessInstanceId());
        return processDefinitionEntity;
    }

    /**
     * 根据taskId获取到businesskey,这个值是管理activiti表和自己流程业务表的关键之处
     */
    public String getBusinessKeyByTaskId(String taskId) {
        Task task = this.getTaskByTaskId(taskId);
        ProcessInstance processInstance = this.getProcessInstanceByTask(task);
        return processInstance.getBusinessKey();
    }

    /**
     * 完成任务的同时,进行下一个节点的审批人员的信息传递
     */
    public void finishCurrentTaskByTaskId(String taskId, Object object) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("assignUser", object);
        this.processEngine.getTaskService().complete(taskId,map);
    }




}
