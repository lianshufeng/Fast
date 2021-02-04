package com.fast.build.helper.core.controller;

import com.fast.dev.core.helper.ViewHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

    @Autowired
    private ViewHelper viewHelper;


    @RequestMapping({"", "/", "index.html", "index"})
    public ModelAndView index() {
        ModelAndView modelAndView = viewHelper.buildModelAndView("javabuild");
        modelAndView.addObject("script_set_baseUrl", viewHelper.getRemoteHost());
        return modelAndView;
    }

}
