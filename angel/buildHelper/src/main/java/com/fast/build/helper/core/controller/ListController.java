package com.fast.build.helper.core.controller;

import com.fast.build.helper.core.conf.BuildTaskConf;
import com.fast.build.helper.core.helper.PathHelper;
import com.fast.build.helper.core.model.FileInfoModel;
import com.fast.build.helper.core.service.CoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@Controller
public class ListController {


    @Autowired
    private CoreService coreService;
    @Autowired
    private BuildTaskConf buildTaskConf;
    @Autowired
    private PathHelper pathHelper;

    @Autowired
    private CoreController coreController;


    @RequestMapping("")
    public void index(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect("list");
    }


    @RequestMapping("list")
    public ModelAndView list(HttpServletRequest request) {
        Collection<FileInfoModel> collection = this.coreController.getFileInfoModel(request, this.pathHelper.getBuildPath());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("fileInfos", this.coreController.listFile(request, this.pathHelper.getBuildPath()));
        modelAndView.setViewName("list.html");
        return modelAndView;
    }


}
