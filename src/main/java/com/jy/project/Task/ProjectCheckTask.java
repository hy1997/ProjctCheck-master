package com.jy.project.Task;

import com.jy.project.controlle.ProjectCheckController;
import com.jy.project.po.ProjectPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * 　* @className: ProjectCheckTask
 *
 * @description: 　* @author: lj
 * 　* @date: 2021/09/08 14:47
 */
@Component
public class ProjectCheckTask {

    @Autowired
    ProjectCheckController projectCheckController;

    @Scheduled(cron = "0 0 9 * * ?")
    public void execute() throws Exception {
        projectCheckController.checkProject(new ProjectPO());
    }

}
