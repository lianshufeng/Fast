package com.fast.build.helper.core.controller;

import com.fast.build.helper.core.helper.ProjectHelper;
import com.fast.build.helper.core.model.ProjectItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("project")
public class CoreController {


    @Autowired
    private ProjectHelper projectHelper;

    /**
     * 更新项目
     *
     * @return
     */
    @RequestMapping("list")
    public Object list() {
        this.projectHelper.reload();
        return this.projectHelper.getItemsFromSort();
    }


    /**
     * 更新项目配置
     *
     * @param projectItem
     * @return
     */
    @RequestMapping("update")
    public Object udpate(ProjectItem projectItem) {
        Assert.hasText(projectItem.getName(), "项目名不能为空");
        this.projectHelper.reload();
        this.projectHelper.update(projectItem);
        this.projectHelper.save();
        return this.projectHelper.getItems().get(projectItem.getName());
    }


}
