package com.activiti6.demo.controller;

import org.activiti.engine.FormService;
import org.activiti.engine.form.StartFormData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/workflow")
public class ProcessDefinitionController {

    @Autowired
    private FormService formService;

    @RequestMapping("start")
    public ModelAndView readStartForm(String processDefinitionId) throws Exception {
        ModelAndView mav = new ModelAndView("work/start-process-form");
        StartFormData startFormData = formService.getStartFormData(processDefinitionId);
        mav.addObject("startFormData",startFormData);
        mav.addObject("processDefinitionId",processDefinitionId);
        return mav;
    }

}
