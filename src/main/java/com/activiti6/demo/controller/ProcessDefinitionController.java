package com.activiti6.demo.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import org.activiti.engine.FormService;
import org.activiti.engine.form.StartFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/workflow")
public class ProcessDefinitionController {

    @Autowired
    private FormService formService;

    @RequestMapping("start")
    @ResponseBody
    public String readStartForm(String processDefinitionId) throws Exception {
//        ModelAndView mav = new ModelAndView("work/start-process-form");
        StartFormData startFormData = formService.getStartFormData(processDefinitionId);
//        mav.addObject("startFormData",startFormData);
//        mav.addObject("processDefinitionId",processDefinitionId);
        HashMap<Object, Object> resultMap = Maps.newHashMap();
        resultMap.put("startFormData", startFormData);
        resultMap.put("processDefinitionId", processDefinitionId);

        return JSON.toJSONString(resultMap);
    }

    @RequestMapping("testJsp")
    public String welcome(Map<String, Object> model){
        model.put("time", new Date());
        model.put("message", "你好");
        return "welcome";
    }


}
