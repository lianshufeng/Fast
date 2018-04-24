package com.fast.dev.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;

@Controller
public class LoginController extends SuperController {

    @RequestMapping("login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("login.html");
        modelAndView.addObject("name", new Date().getTime());
        return modelAndView;
    }


    @RequestMapping("login.json")
    public Object loginJson() {
        ModelAndView modelAndView = new ModelAndView("login.html");
        modelAndView.addObject("name", new Date().getTime());
        return new HashMap<String, Object>() {{
            put("1", new Date().getTime());
        }};
    }


}
