package com.fast.dev.ucenter.service.controller;

import com.fast.dev.ucenter.service.dao.UserLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.HashMap;

@Controller
public class LoginController extends SuperController {

    @Autowired
    private UserLogDao userLogDao;


    @RequestMapping("login")
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView("login.html");
        modelAndView.addObject("name", new Date().getTime());
        return modelAndView;
    }


    @RequestMapping("login.json")
    public Object loginJson(@RequestParam(defaultValue = "xiaofeng") String userName) {
        ModelAndView modelAndView = new ModelAndView("login.html");
        modelAndView.addObject("name", new Date().getTime());
        this.userLogDao.insert(userName);
        return new HashMap<String, Object>() {{
            put("1", new Date().getTime());
        }};
    }


}
