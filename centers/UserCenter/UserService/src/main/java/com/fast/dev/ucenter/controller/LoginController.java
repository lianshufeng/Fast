package com.fast.dev.ucenter.controller;

import com.fast.dev.ucenter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;

@Controller
public class LoginController extends SuperController {

    @Autowired
    private UserService userService;


    @RequestMapping("login.html")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("login.html");
        modelAndView.addObject("name", new Date().getTime());
        return modelAndView;
    }


    @RequestMapping("login.json")
    public Object loginJson(@RequestParam(defaultValue = "xiaofeng") String userName) {
        ModelAndView modelAndView = new ModelAndView("login.html");
        modelAndView.addObject("name", new Date().getTime());
        this.userService.save(userName);
        return new HashMap<String, Object>() {{
            put("time", new Date().getTime());
        }};
    }



    @ResponseBody
    @RequestMapping("test.json")
    public Object test(){
        return new HashMap<String, Object>() {{
            put("time", new Date().getTime());
        }};
    }




}
