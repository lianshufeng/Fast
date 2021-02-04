package com.fast.dev.open.api.server.core.controller.doc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("doc")
public class ApiDocController {


    @RequestMapping(value = {"/", ""})
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("apidoc");
        return modelAndView;
    }


}
