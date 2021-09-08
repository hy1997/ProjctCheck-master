package com.jy.project.controlle;

import com.alibaba.excel.EasyExcel;
import com.jy.project.po.ProjectInfoPO;
import com.jy.project.po.ProjectPO;
import com.jy.project.service.ProjectCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/project")
public class ProjectCheckController {

    @Autowired
    ProjectCheckService projectCheckService;

    @RequestMapping("index")
    public String index() {
        return "add";
    }

    @RequestMapping("add")
    @ResponseBody
    public Integer add(ProjectPO po) {
        return projectCheckService.add(po);
    }

    @RequestMapping("checkProject")
    @ResponseBody
    public String checkProject(ProjectPO po) throws Exception {
        return projectCheckService.projectCheck(po);
    }

    @RequestMapping("projectList")
    public String projectList(ProjectPO po, Model model){
          model.addAttribute("projects",projectCheckService.projectList(po));
        return "projectList";
    }

    @RequestMapping("projectInfoList")
    public String projectInfoList(ProjectInfoPO po, Model model) {
        model.addAttribute("projectInfos",projectCheckService.projectInfoList(po));
        return "projectInfoList";
    }

    @GetMapping("/download")
    public void download(HttpServletResponse response, Integer page, Integer size, String types, Date startTime, Date deadline, String likeWord){
        try {
           projectCheckService.excel();
        } catch (Exception e) {

        }
    }


}
