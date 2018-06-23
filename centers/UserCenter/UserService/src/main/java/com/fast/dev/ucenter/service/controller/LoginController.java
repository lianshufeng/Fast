package com.fast.dev.ucenter.service.controller;

import com.fast.dev.ucenter.service.dao.UserLogDao;
import com.fast.dev.ucenter.service.domain.UserLog;
import com.fast.dev.ucenter.service.service.UserService;
import org.bson.types.Decimal128;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

@Controller
public class LoginController extends SuperController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserLogDao userLogDao;


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


    @RequestMapping("find.json")
    public Object find(@RequestParam(defaultValue = "xiaofeng") String userName, @RequestParam(defaultValue = "0") Integer page,@RequestParam(defaultValue = "20") Integer size) {
        ModelAndView modelAndView = new ModelAndView("login.html");
        modelAndView.addObject("name", new Date().getTime());
        Page<UserLog> pages =  this.userLogDao.findByName(userName,PageRequest.of(page,size));
        return new HashMap<String, Object>() {{
            put("time", new Date().getTime());
            put("page",pages.getContent());
        }};
    }



    @RequestMapping("update.json")
    public Object updateJson() {
        userLogDao.updateUser("xiaofeng",1);
        return new HashMap<String, Object>() {{
            put("time", new Date().getTime());
        }};
    }

    @RequestMapping("mapReduce.json")
    public Object mapReduce() {

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
