package com.activiti6.demo.controller;

import com.activiti6.demo.service.ActivitiService;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Controller
@RequestMapping("/workflow")
public class ActivitiController{

    private Logger logger = LoggerFactory.getLogger(ActivitiController.class);

    @Autowired
    private RepositoryService repositoryService;

    //流程定义列表
    @RequestMapping("/process-list")
    @ResponseBody
    public String processList() {
        List<Map<String, Object>> list = Lists.newArrayList();
        Map<String,Object> map;
        List<ProcessDefinition> processDefinitionList = repositoryService.createProcessDefinitionQuery().list();

        for (ProcessDefinition pd : processDefinitionList)
        {
            map = new HashMap<>();
            map.put("processDefinitionId", pd.getId());
            map.put("deploymentId", pd.getDeploymentId());
            map.put("name", pd.getName());
            map.put("key", pd.getKey());
            map.put("version", pd.getVersion());
            map.put("resourceName", pd.getResourceName());
            map.put("diagramResourceName", pd.getDiagramResourceName());
            map.put("deploymentTime", repositoryService.createDeploymentQuery().deploymentId(pd.getDeploymentId()).singleResult().getDeploymentTime());
            list.add(map);
        }
        return JSON.toJSONString(list);
    }

    @RequestMapping("/deploy")
    public String deploy(@RequestParam("file") MultipartFile file) {
        //获取上传文件名
        String fileName = file.getOriginalFilename();
        try {
            InputStream fileInputStream = file.getInputStream();
            String extension = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
            DeploymentBuilder deployment = repositoryService.createDeployment();
            if (extension.equals("zip") || extension.equals("bar")) {
                ZipInputStream zip = new ZipInputStream(fileInputStream);
                deployment.addZipInputStream(zip);
            } else {
                deployment.addInputStream(fileName, fileInputStream);
            }
            deployment.deploy();
        }  catch (Exception e) {
            logger.error("error on deploy process,because of file input stream");
        }
        return "redirect:process-list";
    }

    @RequestMapping("/read-resource")
    public void readResource(@RequestParam("pdid") String processDefinitionId, @RequestParam("resourceName") String resourceName, HttpServletResponse response) throws Exception {
        ProcessDefinitionQuery pdq = repositoryService.createProcessDefinitionQuery();
        ProcessDefinition pd = pdq.processDefinitionId(processDefinitionId).singleResult();
        InputStream resourceAsStream = repositoryService.getResourceAsStream(pd.getDeploymentId(), resourceName);
        byte[] b = new byte[1024];
        int len = -1;
        while ((len = resourceAsStream.read(b, 0, 1024)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }

    @RequestMapping("/delete-deployment")
    public String deleteProcessDefinition(@RequestParam("deploymentId") String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
        return "redirect:process-list";
    }

}
