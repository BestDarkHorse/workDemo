package com.activiti6.demo;

import com.activiti6.demo.util.DbUnit;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Activiti6DemoApplicationTests {


    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskService taskService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testUser() throws Exception {
//        IdentityService identityService = activitiRule.getIdentityService();
        User user = identityService.newUser("henryyan");
        user.setFirstName("Henry");
        user.setLastName("yan");
        user.setEmail("yanghonglei@gmail.com");
        identityService.saveUser(user);
        User userInDb = identityService.createUserQuery().userId("henryyan").singleResult();
        Assert.assertNotNull(userInDb);
        identityService.deleteUser("henryyan");
        userInDb = identityService.createUserQuery().userId("henryyan").singleResult();
        Assert.assertNull(userInDb);
    }

    @Test
    public void testInsertDB() {
        DbUnit dbUnit = new DbUnit("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/activiti6?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false", "root", "123456");
        IDataSet dataSet = getDataSet("F:\\practice\\workDemo\\src\\main\\resources\\data\\identity-data.xml");
        try {
            dbUnit.setSchema("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbUnit.insertData(dataSet);
    }


    public IDataSet getDataSet(String path) {

        FlatXmlDataSet dataSet = null;
        try {
            dataSet = new FlatXmlDataSet(new FileInputStream(new File(path)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataSet;
    }

    @Test
    public void testTaskService() {
        List<Task> list = taskService.createTaskQuery().list();
        System.out.println(list);

    }

    @Test
    public void testIdentityService() {
        List<Group> henry = identityService.createGroupQuery().groupMember("henry").list();
        System.out.println(henry.get(0).getId());
    }


    /**
     * 1:部署一个Activiti流程
     */
    @Test
    public void createActivitiTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRepositoryService()
                .createDeployment()
                .addClasspathResource("processes/shengqing.bpmn")
                .addClasspathResource("processes/shengqing.png")
                .deploy();
    }

    @Test
    public void testStartProcessInstance() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRuntimeService().startProcessInstanceById("shengqing:1:5004");
    }

    /**
     * 完成请假申请  这相当于提交请假申请
     */
    @Test
    public void testQingjia() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getTaskService().complete("7505"); //查看act_ru_task表
    }

    /**
     * 小明学习的班主任  小毛 查询当前正在执行的任务
     */
    @Test
    public void testQueryTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Task> tasks = processEngine.getTaskService()
                .createTaskQuery()
                .taskAssignee("小毛")
                .list();
        for (Task task : tasks) {
            System.out.println(task.getName());
        }

    }

    /**
     * 班主任小毛完成任务
     */
    @Test
    public void testFinishTask_manager() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getTaskService().complete("10002");
    }

    /**
     * 教务处的大毛完成的任务
     */
    @Test
    public void testFinishtTask_BOss() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getTaskService().complete("12502");
    }

    /**
     * 部署方式二
     */
    @Test
    public void testDeployFromInputStream() {
        InputStream bpmnStream = this.getClass().getClassLoader().getResourceAsStream("processes/shengqing.bpmn");
        //得到流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRepositoryService()
                .createDeployment()
                .addInputStream("shenqing.bpmn", bpmnStream)
                .deploy();

    }

    /**
     * 部署方式三
     */
    @Test
    public void testDeployFromZipinputStream() {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("processes/shengqing.zip");
        ZipInputStream zipInputStream = new ZipInputStream(in);
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRepositoryService()
                .createDeployment().addZipInputStream(zipInputStream).deploy();
    }

    /**
     * 删除已经部署的Activiti 流程
     */
    @Test
    public void testDelete() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRepositoryService().deleteDeployment("20001", true);
    }

    /**
     * 根据名称查询流程部署
     */
    @Test
    public void testQueryDeploymentByName() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Deployment> deployments = processEngine.getRepositoryService()
                .createDeploymentQuery()
                .orderByDeploymenTime()
                .desc()
                .deploymentName("请假流程")
                .list();
        for (Deployment deployment : deployments) {
            System.out.println(deployment.getId());
        }
    }

    /**
     * 查询所有的部署流程
     */
    @Test
    public void queryAllDeployment() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Deployment> deployments = processEngine.getRepositoryService().createDeploymentQuery().orderByDeploymenTime().desc().list();
        for (Deployment deployment : deployments) {
            System.out.println(deployment.getId() + " 部署名称: " + deployment.getName());
        }
    }

    /**
     * 查询所有的流程定义
     */
    @Test
    public void testQueryAllPD() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<ProcessDefinition> list = processEngine.getRepositoryService().createProcessDefinitionQuery().orderByProcessDefinitionVersion().desc().list();
        for (ProcessDefinition pd : list) {
            System.out.println(pd.getName());
        }
    }

    /**
     * 查看流程图
     */
    @Test
    public void testShowImage() throws Exception {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        InputStream inputStream = processEngine.getRepositoryService().getResourceAsStream("5001", "processes/shengqing.png");
        OutputStream outputStream = new FileOutputStream("e:/processimg.png");
        int b = -1;
        while ((b = inputStream.read()) != -1) {
            outputStream.write(b);
        }
        inputStream.close();
        outputStream.close();


    }

    /**
     * 根据 pdid 查看图片
     */
    @Test
    public void testShowImage2() throws Exception {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        InputStream inputStream = processEngine.getRepositoryService().getProcessDiagram("shengqing:1:5004");
        OutputStream outputStream = new FileOutputStream("e:/processimg123.png");
        int b = -1;
        while ((b = inputStream.read()) != -1) {
            outputStream.write(b);
        }
        inputStream.close();
        outputStream.close();
    }

    /**
     * 查看 bpmn文件
     */
    @Test
    public void testShowBpmn() throws Exception {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        InputStream inputStream = processEngine.getRepositoryService().getProcessModel("shengqing:1:5004");
        FileOutputStream outputStream = new FileOutputStream("e:/processModel.bpmn");
        int b = -1;
        while ((b = inputStream.read()) != -1) {
            outputStream.write(b);
        }
        inputStream.close();
        outputStream.close();
    }

    /**
     * 启动流程实例,通过PID
     */
    @Test
    public void testStartProcessInstanceByPID() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessInstance processInstance = processEngine.getRuntimeService().startProcessInstanceById("shengqing:1:5004");
        System.out.println(processInstance.getId());
    }

    /**
     * 根据pdkey启动流程实例,默认启动最高版本的
     */
    @Test
    public void testStartPIByPDKEY() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessInstance shengqing = processEngine.getRuntimeService().startProcessInstanceByKey("shengqing");
        System.out.println(shengqing.getId());
    }

    /**
     * 完成任务
     */
    @Test
    public void testFinishTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getTaskService().complete("22505");
    }

    /**
     * 查询任务
     * 根据任务的执行人查询正在执行的任务
     */
    @Test
    public void testQueryTaskByAssignee() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Task> tasks = processEngine.getTaskService().createTaskQuery().orderByTaskCreateTime().desc().taskAssignee("小毛").list();
        for (Task task : tasks) {
            System.out.println(task.getId());
            System.out.println(task.getAssignee());
        }
    }

    /**
     * 查询所有正在执行的任务
     */
    @Test
    public void testQueryTasks() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Task> tasks = processEngine.getTaskService().createTaskQuery().list();
        for (Task task : tasks) {
            System.out.println(task.getId());
            System.out.println(task.getName());
            System.out.println(task.getAssignee());
            System.out.println("-----------------------------");
        }
    }

    /**
     * 根据piid查询任务
     */
    @Test
    public void testQueryTaskByPIID() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<Task> tasks = processEngine.getTaskService().createTaskQuery().executionId("22502").list();
        for (Task task : tasks) {
            System.out.println(task.getId());
            System.out.println(task.getName());
            System.out.println("---------");
        }

    }

    /**
     * 根据piid得到当前正在执行的流程实例的正在活动的节点
     */
    @Test
    public void testActiviti() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<ProcessInstance> processInstanceList = processEngine.getRuntimeService().createProcessInstanceQuery().processInstanceId("25001").list();
        for (ProcessInstance processInstance : processInstanceList) {
            System.out.println(processInstance.getId());
            System.out.println(processInstance.getProcessDefinitionId());
            System.out.println(processInstance.getActivityId());
            System.out.println("-------------------");
        }

    }

    /**
     * 查看已经完成的任务和当前在执行的任务
     */
    @Test
    public void findHistoryTask() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        List<HistoricTaskInstance> historicTaskInstances1 = processEngine.getHistoryService().createHistoricTaskInstanceQuery().taskDeleteReason("comleted").list();

        List<HistoricTaskInstance> historicTaskInstances2 = processEngine.getHistoryService().createHistoricTaskInstanceQuery().list();

        System.out.println("执行完成的任务: " + historicTaskInstances1.size());
        System.out.println("所有的总任务数(执行完和当前未执行完) : " + historicTaskInstances2.size());
        for (HistoricTaskInstance instance : historicTaskInstances2) {
            System.out.println(instance.getId());
            System.out.println("--------------------");
        }
    }

    /**
     * 根据pdid 得到processDefinitionEntity
     */
    @Test
    public void testProcessDefinitionEntity() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) processEngine.getRepositoryService().getProcessDefinition("shengqing:1:5004");
        System.out.println(processDefinitionEntity);
    }

    /**
     * 根据pdid得到processDefinitionEntity中的activityimpl
     * <p>
     * 升级到Activiti6.0.0 之后，发现pvm 包整个被删掉了。。。。这样一来就导致之前的跟踪流失效了。代码连编译都通过不了。
     * 因为pvm包没了，所以就不能再使用ActivityImpl 等相关类了。只能改成用org.activiti.bpmn.model包下的FlowNode类来替代。好在他们差不多，所以代码改动也不大。下面是完整代码：
     */
    @Test
    public void testGetActivityImpl() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        /**
         * 根据pdid获取processDefinitionEntity
         */
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) processEngine.getRepositoryService().getProcessDefinition("shengqing:1:5004");

        BpmnModel bpmnModel = processEngine.getRepositoryService().getBpmnModel("shengqing:1:5004");
        System.out.println(JSON.toJSONString(bpmnModel));
    }


    /**
     * 部署流程
     */
    @Test
    public void startDeployTest() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRepositoryService()
                .createDeployment()
                .name("请假流程: 情况一")
                .addClasspathResource("processes/shenqing1.bpmn")
                .deploy();
    }

    /**
     * 启动流程实例
     * 设置一个流程变量
     */
    @Test
    public void testStartPI() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("student", "小明");
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRuntimeService().startProcessInstanceById("shenqing1:1:30004", variables);
    }

    /**
     * 在完成请假申请的任务的时候，给班主任审批的节点赋值任务的执行人
     */
    @Test
    public void testFinishTask_Teacher() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("teacher", "我是小明的班主任");
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getTaskService().complete("32506", variables);
    }

    /**
     * 在完成班主任审批的情况下，给教务处节点赋值
     */
    @Test
    public void teatFinishTask_Manager() {
        Map<String, Object> variables = Maps.newHashMap();
        variables.put("manager", "我是小明的教务处处长");
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getTaskService().complete("35003",variables);
    }

    /**
     * 结束流程实例
     */
    @Test
    public void testFinishTasks() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getTaskService().complete("37503");
    }

}
