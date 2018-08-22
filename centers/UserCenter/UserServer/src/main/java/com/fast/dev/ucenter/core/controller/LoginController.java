package com.fast.dev.ucenter.core.controller;

import com.fast.dev.ucenter.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController extends SuperController {

    @Autowired
    private UserService userService;


    @RequestMapping("login.html")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("login.html");
        modelAndView.addObject("name", System.currentTimeMillis());
        return modelAndView;
    }


}
